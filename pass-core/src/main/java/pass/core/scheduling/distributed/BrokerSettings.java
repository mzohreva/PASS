package pass.core.scheduling.distributed;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import pass.core.common.Config;

public class BrokerSettings
{

    public static final String JOBS_QUEUE = "pass_jobs";
    public static final String STATUS_UPDATE_EXCHANGE = "pass_status_updates";

    public static ConnectionFactory getConnectionFactory()
    {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(Config.getInstance().getBrokerHost());
        factory.setUsername(Config.getInstance().getBrokerUser());
        factory.setPassword(Config.getInstance().getBrokerPassword());
        return factory;
    }

    public static void declareJobsQueue(Channel channel) throws IOException
    {
        channel.queueDeclare(JOBS_QUEUE, true, false, false, null);
    }

    public static void declareStatusUpdateExchange(Channel channel)
            throws IOException
    {
        channel.exchangeDeclare(STATUS_UPDATE_EXCHANGE, "fanout");
    }
}
