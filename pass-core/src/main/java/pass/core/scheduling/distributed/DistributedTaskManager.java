package pass.core.scheduling.distributed;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import pass.core.scheduling.Status;
import pass.core.scheduling.TaskManager;
import pass.core.scheduling.TaskSpecification;
import pass.core.scheduling.TaskStatus;
import pass.core.scheduling.TaskType;

public class DistributedTaskManager implements TaskManager
{

    private static final Logger LOGGER = Logger.getLogger(DistributedTaskManager.class.getName());

    private final ConcurrentMap<UUID, TaskStatus> statusMap;
    private final ConcurrentMap<TaskType, Long> completedTasks;
    private Connection connection;
    private Channel suChannel;
    private Channel jobChannel;
    private StatusUpdateConsumer suConsumer;

    public DistributedTaskManager()
    {
        this.statusMap = new ConcurrentHashMap<>();
        this.completedTasks = new ConcurrentHashMap<>();
        this.setupConnection();
    }

    private void setupConnection()
    {
        try {
            LOGGER.info("Connecting to message broker...");
            ConnectionFactory factory = BrokerSettings.getConnectionFactory();
            connection = factory.newConnection();
            suChannel = connection.createChannel();

            BrokerSettings.declareStatusUpdateExchange(suChannel);
            String queueName = suChannel.queueDeclare().getQueue();
            suChannel.queueBind(queueName,
                                BrokerSettings.STATUS_UPDATE_EXCHANGE,
                                "");

            suConsumer = new StatusUpdateConsumer(suChannel,
                                                  statusMap,
                                                  completedTasks);
            suChannel.basicConsume(queueName, /* autoAck: */ true, suConsumer);

            jobChannel = connection.createChannel();
            BrokerSettings.declareJobsQueue(jobChannel);
            LOGGER.info("Connection successful");
        }
        catch (IOException | TimeoutException ex) {
            LOGGER.log(Level.SEVERE,
                       "Failed to setup connection to message broker",
                       ex);
        }
    }

    private boolean queueJob(Job job)
    {
        try {
            jobChannel.basicPublish("",
                                    BrokerSettings.JOBS_QUEUE,
                                    null,
                                    job.serialize());
            return true;
        }
        catch (IOException ex) {
            LOGGER.log(Level.SEVERE,
                       "Failed to queue job " + job.getTaskSpec().toString(),
                       ex);
            return false;
        }
    }

    private void createJob(TaskSpecification taskSpec)
    {
        UUID taskId = UUID.randomUUID();
        Job job = new Job(taskId, taskSpec);
        boolean queued = queueJob(job);
        if (queued) {
            Status status = new Status(Status.TaskState.QUEUED, null);
            TaskStatus taskStatus = new TaskStatus(taskId,
                                                   taskSpec,
                                                   "n/a",
                                                   status);
            statusMap.put(taskId, taskStatus);
        }
    }

    @Override
    public void evaluateSubmission(int submissionId)
    {
        TaskSpecification ts = TaskSpecification.evaluation(submissionId);
        createJob(ts);
    }

    @Override
    public void cleanupSubmissions(String username, int projectId)
    {
        TaskSpecification ts = TaskSpecification.cleanup(username, projectId);
        createJob(ts);
    }

    @Override
    public void zipSubmissions(int projectId)
    {
        TaskSpecification ts = TaskSpecification.zip(projectId);
        createJob(ts);
    }

    @Override
    public Status getEvaluateSubmissionTaskStatus(int submissionId)
    {
        for (Map.Entry<UUID, TaskStatus> kv : statusMap.entrySet()) {
            TaskSpecification ts = kv.getValue().getTaskSpec();
            if (ts.isSubmissionEvaluation(submissionId)) {
                return kv.getValue().getStatus();
            }
        }
        return null;
    }

    private List<TaskStatus> listTasksByState(Status.TaskState state)
    {
        List<TaskStatus> list = new ArrayList<>();
        statusMap.entrySet().stream()
                .filter((kv) -> (kv.getValue().getStatus().getState() == state))
                .forEachOrdered((kv) -> {
                    list.add(kv.getValue());
                });
        return list;
    }

    @Override
    public List<TaskStatus> listQueuedTasks()
    {
        return listTasksByState(Status.TaskState.QUEUED);
    }

    @Override
    public List<TaskStatus> listRunningTasks()
    {
        return listTasksByState(Status.TaskState.RUNNING);
    }

    @Override
    public List<TaskStatus> listTasksByType(TaskType taskType)
    {
        List<TaskStatus> list = new ArrayList<>();
        statusMap.entrySet().stream()
                .filter((kv) -> (kv.getValue().getTaskSpec().getType() == taskType))
                .forEachOrdered((kv) -> {
                    list.add(kv.getValue());
                });
        return list;
    }

    @Override
    public long completedTasksByType(TaskType taskType)
    {
        Long x = completedTasks.get(taskType);
        return (x == null ? 0L : x);
    }

    @Override
    public void shutdown()
    {
        try {
            suChannel.basicCancel(suConsumer.getConsumerTag());
            suChannel.close();
            jobChannel.close();
            connection.close();
            LOGGER.info("Shutdown complete.");
        }
        catch (IOException | TimeoutException ex) {
            LOGGER.log(Level.SEVERE,
                       "Failed to close the connection to message broker",
                       ex);
        }
    }
}
