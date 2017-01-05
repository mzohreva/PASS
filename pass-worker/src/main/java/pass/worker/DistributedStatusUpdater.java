package pass.worker;

import com.rabbitmq.client.Channel;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import pass.core.scheduling.Status;
import pass.core.scheduling.StatusUpdater;
import pass.core.scheduling.TaskSpecification;
import pass.core.scheduling.TaskStatus;
import pass.core.scheduling.distributed.BrokerSettings;

public class DistributedStatusUpdater implements StatusUpdater
{

    private static final Logger LOGGER = Logger.getLogger(DistributedStatusUpdater.class.getName());

    private final Channel channel;
    private final String worker;

    public DistributedStatusUpdater(Channel channel, String worker)
    {
        this.channel = channel;
        this.worker = worker;
    }

    @Override
    public void send(UUID taskId, TaskSpecification taskSpec, Status status)
    {
        try {
            TaskStatus message;
            message = new TaskStatus(taskId,
                                     taskSpec,
                                     worker + "-C" + channel.getChannelNumber(),
                                     status);
            channel.basicPublish(BrokerSettings.STATUS_UPDATE_EXCHANGE,
                                 "",
                                 null,
                                 message.serialize());
        }
        catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
}
