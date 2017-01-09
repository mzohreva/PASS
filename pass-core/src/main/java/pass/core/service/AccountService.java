package pass.core.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import pass.core.common.HashedPassword;
import pass.core.filesystem.FileRepository;
import pass.core.hibernate.HibernateSession;
import pass.core.hibernate.UsersRepository;
import pass.core.hibernate.VerificationCodeRepository;
import pass.core.model.User;
import pass.core.model.VerificationCode;

public class AccountService
{

    private static final Logger LOGGER = Logger.getLogger(AccountService.class.getName());

    private final VerificationService verificationService;

    public AccountService(VerificationService verificationService)
    {
        this.verificationService = verificationService;
    }

    private class StudentInfo
    {

        public String firstName;
        public String lastName;
        public String email;
    }

    private boolean isOnTheList(String studentId,
                                String username,
                                StudentInfo out_info) throws IOException
    {
        Path usersList = FileRepository.getUsersList();
        List<String> allLines = Files.readAllLines(usersList,
                                                   StandardCharsets.UTF_8);
        for (String line : allLines) {
            // Format: Student Id, Username, Last Name, First Name, Email
            String[] fields = line.split(",");
            if (fields.length >= 5) {
                if (fields[0].equals(studentId)
                    && fields[1].equals(username)) {
                    out_info.lastName = fields[2];
                    out_info.firstName = fields[3];
                    out_info.email = fields[4];
                    return true;
                }
            }
        }
        return false;
    }

    private void checkPassword(String password) throws ServiceException
    {
        if (password == null || password.length() < 6) {
            throw new ServiceException(ErrorCode.PASSWORD_TOO_SHORT);
        }
    }

    public boolean register(String username, String password, String studentId)
            throws ServiceException
    {
        try {
            StudentInfo studentInfo = new StudentInfo();
            if (!isOnTheList(studentId, username, studentInfo)) {
                throw new ServiceException(ErrorCode.UNLISTED);
            }
            checkPassword(password);
            try (HibernateSession hs = new HibernateSession()) {
                UsersRepository userRepo = new UsersRepository(hs);
                User existingByUsername = userRepo.findByUsername(username);
                if (existingByUsername != null) {
                    throw new ServiceException(ErrorCode.USER_ALREADY_EXISTS);
                }
                User user = userRepo.addUser(
                        username, password, studentId,
                        studentInfo.firstName, studentInfo.lastName,
                        studentInfo.email, /* verified: */ false
                );
                if (user == null) {
                    throw new ServiceException(ErrorCode.UNKNOWN_ERROR);
                }
                VerificationCode vCode = VerificationCode.generate(
                        VerificationCode.Reason.ACCOUNT_CREATION,
                        user);
                boolean stored = new VerificationCodeRepository(hs).store(vCode);
                boolean sent = verificationService.sendCodeByEmail(vCode);
                return stored && sent;
            }
        }
        catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Failed to register", ex);
            throw new ServiceException(ErrorCode.UNKNOWN_ERROR);
        }
    }

    public void registerAdmin(
            String firstName,
            String lastName,
            String password,
            String studentId,
            String email) throws ServiceException
    {
        String username = "admin";
        checkPassword(password);
        try (HibernateSession hs = new HibernateSession()) {
            UsersRepository userRepo = new UsersRepository(hs);
            User existingUser = userRepo.findByUsername(username);
            if (existingUser != null) {
                throw new ServiceException(ErrorCode.USER_ALREADY_EXISTS);
            }
            User user = userRepo.addUser(
                    username, password, studentId,
                    firstName, lastName, email, /* verified: */ true
            );
            if (user == null) {
                throw new ServiceException(ErrorCode.UNKNOWN_ERROR);
            }
        }
    }

    public void verifyAccount(VerificationCode vc) throws ServiceException
    {
        if (vc.getReason() != VerificationCode.Reason.ACCOUNT_CREATION) {
            throw new ServiceException(ErrorCode.INVALID_VCODE);
        }
        try (HibernateSession hs = new HibernateSession()) {
            // verify user account
            User user = vc.getUser();
            user.setVerified(true);
            UsersRepository userRepo = new UsersRepository(hs);
            userRepo.updateUser(user);
            // NOTE: vc will be deleted after successful login
        }
    }

    public void recover(String username)
    {
        try (HibernateSession hs = new HibernateSession()) {
            UsersRepository userRepo = new UsersRepository(hs);
            User user = userRepo.findByUsername(username);
            if (user == null) {
                LOGGER.log(Level.INFO,
                           "Could not find user for username ''{0}''",
                           username);
                return;
            }
            final VerificationCode.Reason REASON = VerificationCode.Reason.PASSWORD_RESET;
            VerificationCodeRepository vcRepo;
            vcRepo = new VerificationCodeRepository(hs);
            List<VerificationCode> oldCodes;
            oldCodes = vcRepo.listBy(username, REASON);
            VerificationCode vc;
            if (oldCodes.size() > 0) {
                vc = oldCodes.get(0);
            }
            else {
                vc = VerificationCode.generate(REASON, user);
                boolean stored = vcRepo.store(vc);
                if (!stored) {
                    vc = null;
                }
            }
            if (vc != null) {
                verificationService.sendCodeByEmail(vc);
            }
        }
    }

    private boolean changePasswordHelper(String username, String new_pass)
            throws ServiceException
    {
        checkPassword(new_pass);
        try (HibernateSession hs = new HibernateSession()) {
            UsersRepository usersRepo = new UsersRepository(hs);
            User user = usersRepo.findByUsername(username);
            if (user == null) {
                throw new ServiceException(ErrorCode.NOT_FOUND);
            }
            HashedPassword hp;
            hp = HashedPassword.generate(new_pass);
            user.setSalt(hp.getSalt());
            user.setPassword(hp.getHash());
            boolean success = usersRepo.updateUser(user);
            return success;
        }
        catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            LOGGER.log(Level.SEVERE, "Error changing password", ex);
            return false;
        }
    }

    public boolean changePassword(String username, String new_pass)
            throws ServiceException
    {
        if (username.equals("admin")) {
            throw new ServiceException(ErrorCode.NOT_ALLOWED);
        }
        return changePasswordHelper(username, new_pass);
    }

    public boolean resetPassword(VerificationCode vc, String new_pass)
            throws ServiceException
    {
        if (vc.getReason() != VerificationCode.Reason.PASSWORD_RESET) {
            throw new ServiceException(ErrorCode.INVALID_VCODE);
        }
        boolean success = changePasswordHelper(
                vc.getUser().getUsername(),
                new_pass);
        if (success) {
            try (HibernateSession hs = new HibernateSession()) {
                new VerificationCodeRepository(hs).remove(vc);
            }
        }
        return success;
    }
}
