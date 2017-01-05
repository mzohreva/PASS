package pass.core.email;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/*
 * Secure because it autheniticates to a mail server using SSL
 */
public class SecureMailer implements Mailer
{

    private static final Logger LOGGER = Logger.getLogger(SecureMailer.class.getName());
    //
    private final String emailServer;
    private final String emailUser;
    private final String emailPassword;
    private final String fromAddress;
    private final String fromName;

    public SecureMailer(String emailServer,
                        String emailUser,
                        String emailPassword,
                        String fromAddress,
                        String fromName)
    {
        this.emailServer = emailServer;
        this.emailUser = emailUser;
        this.emailPassword = emailPassword;
        this.fromAddress = fromAddress;
        this.fromName = fromName;
    }

    @Override
    public boolean sendMessage(String address, String subject, String body)
    {
        Properties mailConfig = new Properties();
        mailConfig.put("mail.smtps.host", emailServer);
        mailConfig.put("mail.smtps.auth", "true");
        Session session = Session.getDefaultInstance(mailConfig, null);
        MimeMessage message = new MimeMessage(session);
        try {
            message.addRecipients(Message.RecipientType.TO, address);
            message.setSubject(subject);
            //message.setContent(body, "text/html");
            message.setText(body);
            message.setFrom(new InternetAddress(fromAddress, fromName));
            Transport trans = session.getTransport("smtps");
            trans.connect(emailServer, emailUser, emailPassword);
            trans.sendMessage(message, message.getAllRecipients());
            trans.close();
            return true;
        }
        catch (MessagingException | UnsupportedEncodingException err) {
            LOGGER.log(Level.SEVERE, "Error sending message by email", err);
            return false;
        }
    }
}
