package pass.core.service;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import pass.core.common.Config;
import pass.core.email.Mailer;
import pass.core.hibernate.HibernateSession;
import pass.core.hibernate.VerificationCodeRepository;
import pass.core.model.VerificationCode;

public class VerificationService
{

    private static final Logger LOGGER = Logger.getLogger(VerificationService.class.getName());

    private final Mailer mailer;

    public VerificationService(Mailer mailer)
    {
        this.mailer = mailer;
    }

    public boolean sendCodeByEmail(VerificationCode vc)
    {
        final String url = vc.getLink();
        final String site = Config.getInstance().getSiteTitle()
                            + " submission site";
        String subject = "";
        String body = "Hello " + vc.getUser().getFirstname() + ",\n\n";
        switch (vc.getReason()) {
            case ACCOUNT_CREATION:
                subject = "Activate your account on " + site;
                body += "To finish registration and activate your account on";
                body += " the submission site for ";
                body += Config.getInstance().getSiteTitle();
                body += " click on the following link:\n\n" + url;
                break;
            case PASSWORD_RESET:
                subject = "Reset your password for " + site;
                body += "Someone has requested password reset for the account";
                body += " associated with your email address. To reset your";
                body += " password click on the following link and follow";
                body += " the instructions:\n\n" + url + "\n\n";
                body += "If you did not initiate this request, you can";
                body += " safely ignore this message.";
                break;

        }
        body += "\n\nThanks\n";
        try {
            mailer.sendMessage(vc.getUser().getEmail(), subject, body);
            return true;
        }
        catch (Exception err) {
            LOGGER.log(Level.SEVERE,
                       "Failed to send code by email to "
                       + "'" + vc.getUser().getEmail() + "'"
                       + ", reason = " + vc.getReason()
                       + ", username = " + vc.getUser().getUsername(),
                       err);
            return false;
        }
    }

    public VerificationCode getVerificationCode(String codeStr)
            throws ServiceException
    {
        UUID code;
        try {
            code = UUID.fromString(codeStr);
        }
        catch (IllegalArgumentException | NullPointerException ex) {
            LOGGER.log(Level.INFO, "Could not convert {0} to UUID", codeStr);
            throw new ServiceException(ErrorCode.INVALID_VCODE);
        }
        try (HibernateSession hs = new HibernateSession()) {
            VerificationCode vc = new VerificationCodeRepository(hs).find(code);
            if (vc == null) {
                LOGGER.info("Verification code not found in db");
                throw new ServiceException(ErrorCode.INVALID_VCODE);
            }
            return vc;
        }
    }
}
