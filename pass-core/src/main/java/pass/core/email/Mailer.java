package pass.core.email;

public interface Mailer
{

    boolean sendMessage(String address, String subject, String body);
}
