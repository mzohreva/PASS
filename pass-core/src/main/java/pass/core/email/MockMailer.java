package pass.core.email;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MockMailer implements Mailer
{

    private static final Logger LOGGER = Logger.getLogger(MockMailer.class.getName());

    @Override
    public boolean sendMessage(String address, String subject, String body)
    {
        LOGGER.log(Level.INFO, "--------------------------------------------");
        LOGGER.log(Level.INFO, "Sending the following message:");
        LOGGER.log(Level.INFO, "    To: <{0}>:", address);
        LOGGER.log(Level.INFO, "    Subject: {0}", subject);
        LOGGER.log(Level.INFO, "    Body: {0}", body);
        LOGGER.log(Level.INFO, "--------------------------------------------");
        return true;
    }
}
