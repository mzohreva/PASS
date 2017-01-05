package pass.core.scheduling.distributed;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import pass.core.scheduling.Status;
import pass.core.scheduling.TaskStatus;
import pass.core.scheduling.TaskType;

public class StatusUpdateConsumer extends DefaultConsumer
{

    private static final Logger LOGGER = Logger.getLogger(StatusUpdateConsumer.class.getName());

    private final ConcurrentMap<UUID, TaskStatus> statusMap;
    private final ConcurrentMap<TaskType, Long> completedTasks;

    public StatusUpdateConsumer(Channel channel,
                                ConcurrentMap<UUID, TaskStatus> statusMap,
                                ConcurrentMap<TaskType, Long> completedTasks)
    {
        super(channel);
        this.statusMap = statusMap;
        this.completedTasks = completedTasks;
        LOGGER.log(Level.INFO, "{0} initialized", this.getClass().getName());
    }

    private void handleMessage(byte[] body)
    {
        TaskStatus message = TaskStatus.deserialize(body);
        if (message == null) {
            LOGGER.severe("Could not deserialize status update message");
            return;
        }
        statusMap.put(message.getTaskId(), message);
        if (message.getStatus().getState() == Status.TaskState.FINISHED) {
            statusMap.remove(message.getTaskId());
            TaskType taskType = message.getTaskSpec().getType();
            Long x = completedTasks.get(taskType);
            completedTasks.put(taskType, (x == null ? 1L : x + 1));
        }
        LOGGER.log(Level.INFO, "{0} {1} {2} {3}", new Object[] {
            message.getTaskId().toString(),
            message.getTaskSpec().toString(),
            message.getWorker(),
            message.getStatus().toString()
        });
    }

    @Override
    public void handleDelivery(String consumerTag,
                               Envelope envelope,
                               AMQP.BasicProperties properties,
                               byte[] body) throws IOException
    {
        handleMessage(body);
    }

    @Override
    public void handleShutdownSignal(String consumerTag,
                                     ShutdownSignalException sig)
    {
        LOGGER.log(Level.INFO,
                   "Shutdown signal received, reason: {0}, initiated by broker: {1}",
                   new Object[] {
                       sig.getReason().protocolMethodName(), !sig.isInitiatedByApplication()
                   });
    }
}
