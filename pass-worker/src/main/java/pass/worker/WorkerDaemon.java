package pass.worker;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import pass.core.common.Config;
import pass.core.hibernate.HibernateSession;
import pass.core.scheduling.StatusUpdater;
import pass.core.scheduling.distributed.BrokerSettings;

public class WorkerDaemon implements Daemon
{

    private static final Logger LOGGER = Logger.getLogger(WorkerDaemon.class.getName());

    private int nConsumers;
    private String workerName;
    private Connection connection;
    private List<Channel> channels;
    private List<JobConsumer> consumers;

    private void setupLogging() throws DaemonInitException
    {
        try {
            LoggerSettings.Setup();
        }
        catch (IOException err) {
            throw new DaemonInitException("Failed to setup logging", err);
        }
    }

    private static String getDefaultWorkerName()
    {
        try {
            return "worker-" + InetAddress.getLocalHost().toString();
        }
        catch (UnknownHostException ex) {
            return "worker";
        }
    }

    private void parseArguments(String[] args) throws DaemonInitException
    {
        // Set default values
        nConsumers = 1;
        workerName = getDefaultWorkerName();
        // Check args
        if (args.length >= 1) {
            try {
                nConsumers = Integer.parseInt(args[0]);
                if (nConsumers < 1 || nConsumers > 10) {
                    throw new DaemonInitException("Unacceptable value for number of consumers!");
                }
            }
            catch (NumberFormatException err) {
                throw new DaemonInitException("Not a valid integer: " + args[0]);
            }
            if (args.length >= 2) {
                workerName = args[1];
            }
        }
        LOGGER.log(Level.INFO, "nConsumers = {0}", nConsumers);
        LOGGER.log(Level.INFO, "workerName = {0}", workerName);
    }

    private void loadConfig() throws DaemonInitException
    {
        LOGGER.info("Loading config...");
        try {
            Config.getInstance().loadConfig();
        }
        catch (IOException err) {
            throw new DaemonInitException("Failed to initialize config", err);
        }
    }

    private void checkDB() throws DaemonInitException
    {
        LOGGER.info("Checking database...");
        try (HibernateSession hs = new HibernateSession()) {
            Session session = hs.getSession();
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                Query q = session.createQuery("from Project p");
                q.list();
                tx.commit();
                LOGGER.info("Database OK");
            }
            catch (Exception ex) {
                if (tx != null) {
                    tx.rollback();
                }
                throw new DaemonInitException("Error while connecting to database", ex);
            }
        }
    }

    private void checkFileRepository() throws DaemonInitException
    {
        LOGGER.info("Checking file repository...");
        String repositoryPath = Config.getInstance().getRepositoryPath();
        Path path = Paths.get(repositoryPath);
        boolean exists = Files.exists(path);
        boolean isDir = Files.isDirectory(path);
        boolean isReadable = Files.isReadable(path);
        if (!exists || !isDir || !isReadable) {
            throw new DaemonInitException("Problem with repository ("
                                          + repositoryPath + ")"
                                          + " exists = " + exists
                                          + " isDir = " + isDir
                                          + " isReadable = " + isReadable);
        }
        LOGGER.info("File system OK");
    }

    private void checkMessageBroker() throws DaemonInitException
    {
        LOGGER.info("Checking message broker...");
        try {
            ConnectionFactory factory = BrokerSettings.getConnectionFactory();
            factory.newConnection();
            LOGGER.info("Message broker OK");
        }
        catch (IOException | TimeoutException ex) {
            throw new DaemonInitException("Failed to connect to message broker", ex);
        }
    }

    @Override
    public void init(DaemonContext context)
            throws DaemonInitException, Exception
    {
        try {
            setupLogging();
            LOGGER.info("Initializing...");
            parseArguments(context.getArguments());
            loadConfig();
            checkDB();
            checkFileRepository();
            checkMessageBroker();
            LOGGER.info("Initialized successfully");
        }
        catch (DaemonInitException err) {
            LOGGER.severe(err.getMessageWithCause());
            throw err;
        }
    }

    @Override
    public void start() throws Exception
    {
        LOGGER.info("Starting...");
        channels = new ArrayList<>();
        consumers = new ArrayList<>();
        try {
            ConnectionFactory factory = BrokerSettings.getConnectionFactory();
            connection = factory.newConnection();
            LOGGER.info("Connection established. Creating channels...");
            for (int i = 0; i < nConsumers; i++) {
                Channel channel = connection.createChannel();
                BrokerSettings.declareJobsQueue(channel);
                BrokerSettings.declareStatusUpdateExchange(channel);
                channel.basicQos(1);
                StatusUpdater statusUpdater = new DistributedStatusUpdater(channel, workerName);
                JobConsumer consumer = new JobConsumer(channel, statusUpdater);
                channel.basicConsume(BrokerSettings.JOBS_QUEUE, /* autoAck: */ false, consumer);

                channels.add(channel);
                consumers.add(consumer);
                LOGGER.log(Level.INFO, "Channel {0} created", channel.getChannelNumber());
            }
            LOGGER.info("Started successfully");
        }
        catch (IOException | TimeoutException ex) {
            LOGGER.log(Level.SEVERE, "Failed to start", ex);
        }
    }

    @Override
    public void stop() throws Exception
    {
        LOGGER.info("Stopping...");
        for (JobConsumer consumer : consumers) {
            Channel channel = consumer.getChannel();
            LOGGER.log(Level.INFO, "Canceling consumer on channel #{0}...", channel.getChannelNumber());
            channel.basicCancel(consumer.getConsumerTag());
        }
        boolean allDone = true;
        for (int i = 0; i < 60; i++) {
            Thread.sleep(1000);
            allDone = true;
            for (JobConsumer consumer : consumers) {
                allDone = allDone && !consumer.isProcessing();
            }
            if (allDone) {
                LOGGER.info("All jobs finished.");
                break;
            }
            LOGGER.log(Level.INFO, "Waiting for all jobs to finish...{0}", (1 + i));
        }
        if (!allDone) {
            LOGGER.warning("Some job(s) might not have finished properly");
        }
        LOGGER.info("Closing channels...");
        for (Channel channel : channels) {
            LOGGER.log(Level.INFO, "Closing channel #{0}", channel.getChannelNumber());
            channel.close();
        }
        LOGGER.info("Closing connection...");
        connection.close();
        LOGGER.info("Stopped successfully");
    }

    @Override
    public void destroy()
    {
        LOGGER.info("Destroyed");
    }
}
