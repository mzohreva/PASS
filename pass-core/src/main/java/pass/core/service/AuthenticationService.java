package pass.core.service;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import pass.core.common.HashedPassword;
import pass.core.hibernate.HibernateSession;
import pass.core.hibernate.UsersRepository;
import pass.core.hibernate.VerificationCodeRepository;
import pass.core.model.User;
import pass.core.model.VerificationCode;

public class AuthenticationService
{

    private static final Logger LOGGER = Logger.getLogger(AuthenticationService.class.getName());

    public AuthenticationService()
    {
    }

    public enum AuthenticationStatus
    {
        AUTHENTICATED,
        INVALID_CREDENTIALS,
        ACCOUNT_NOT_VERIFIED,
        UNDER_MAINTENANCE
    }

    public class AuthenticationResult
    {

        public final AuthenticationStatus status;
        public final AuthenticatedUser user;

        public AuthenticationResult(AuthenticationStatus status,
                                    AuthenticatedUser user)
        {
            this.status = status;
            this.user = user;
        }
    }

    public AuthenticationResult authenticate(String username, String password)
    {
        if (username == null || password == null) {
            return new AuthenticationResult(
                    AuthenticationStatus.INVALID_CREDENTIALS,
                    null);
        }
        try (HibernateSession hs = new HibernateSession()) {
            boolean passOk = false;
            boolean verified = false;
            AuthenticatedUser authUser = null;
            UsersRepository usersRepo = new UsersRepository(hs);
            User user = usersRepo.findByUsername(username);
            if (user != null) {
                HashedPassword hp = new HashedPassword(user.getSalt(),
                                                       user.getPassword());
                passOk = hp.matches(password);
                if (passOk) {
                    verified = user.isVerified();
                    authUser = new AuthenticatedUser(user.getUsername(),
                                                     user.getFirstname(),
                                                     user.getLastname(),
                                                     user.getStudentId());
                    if (verified) {
                        // Remove any existing verification codes
                        // for this user for ACCOUNT_CREATION
                        VerificationCodeRepository vcRepo;
                        vcRepo = new VerificationCodeRepository(hs);
                        VerificationCode.Reason REASON;
                        REASON = VerificationCode.Reason.ACCOUNT_CREATION;
                        vcRepo.removeBy(username, REASON);
                    }
                }
            }
            ServerStatusService sss = new ServerStatusService();
            if (sss.isUnderMaintenance() && !username.equals("admin")) {
                return new AuthenticationResult(
                        AuthenticationStatus.UNDER_MAINTENANCE,
                        null);
            }
            AuthenticationStatus status;
            status = (passOk && verified
                      ? AuthenticationStatus.AUTHENTICATED
                      : (!passOk
                         ? AuthenticationStatus.INVALID_CREDENTIALS
                         : AuthenticationStatus.ACCOUNT_NOT_VERIFIED));
            return new AuthenticationResult(status, authUser);
        }
        catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            LOGGER.log(Level.SEVERE, "Error authenticating user", ex);
            return new AuthenticationResult(
                    AuthenticationStatus.INVALID_CREDENTIALS,
                    null);
        }
    }
}
