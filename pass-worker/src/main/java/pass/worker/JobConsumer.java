package pass.worker;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import pass.core.scheduling.BaseTask;
import pass.core.scheduling.StatusUpdater;
import pass.core.scheduling.TaskSpecification;
import pass.core.scheduling.distributed.Job;

public class JobConsumer extends DefaultConsumer
{

    private static final Logger LOGGER = Logger.getLogger(JobConsumer.class.getName());

    private final StatusUpdater statusUpdater;
    private volatile boolean canceled;
    private volatile boolean processing;

    public JobConsumer(Channel channel, StatusUpdater statusUpdater)
    {
        super(channel);
        this.statusUpdater = statusUpdater;
        this.canceled = false;
        this.processing = false;
    }

    private void log(String msg)
    {
        LOGGER.log(Level.INFO,
                   "[C{0}] {1}",
                   new Object[] {getChannel().getChannelNumber(), msg});
    }

    public boolean isProcessing()
    {
        return processing;
    }

    @Override
    public void handleDelivery(String consumerTag,
                               Envelope envelope,
                               AMQP.BasicProperties properties,
                               byte[] body) throws IOException
    {
        if (!canceled) {
            processing = true;
            try {
                Job job = Job.deserialize(body);
                if (job == null) {
                    log("Could not deserialize job spec");
                    return;
                }
                TaskSpecification spec = job.getTaskSpec();
                log("+New job: "
                    + job.getTaskId().toString() + " "
                    + spec.toString());
                BaseTask task = spec.makeCorrespondingTask(job.getTaskId(),
                                                           statusUpdater);
                if (task == null) {
                    log("Could not create the corresponding task for "
                        + spec.toString());
                    return;
                }
                task.run();
                log("-Job done: " + spec.toString());
            }
            finally {
                getChannel().basicAck(envelope.getDeliveryTag(), false);
                processing = false;
            }
        }
        else {
            log("Cannot handle new delivery, already canceled!");
        }
    }

    @Override
    public void handleCancelOk(String consumerTag)
    {
        this.canceled = true;
        log("CancelOk");
    }

    @Override
    public void handleShutdownSignal(String consumerTag,
                                     ShutdownSignalException sig)
    {
        log("Shutdown signal received"
            + ", reason: " + sig.getReason().protocolMethodName()
            + ", initiated by broker: " + !sig.isInitiatedByApplication());
    }
}
