package pass.web.view;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import pass.core.common.Util;
import pass.core.hibernate.HibernateSession;
import pass.core.hibernate.UsersRepository;
import pass.core.model.User;
import pass.core.service.AccountService;
import pass.core.service.ErrorCode;
import pass.core.service.ServiceException;
import pass.core.service.VerificationService;
import pass.web.common.Container;
import pass.web.common.WebConfig;
import pass.web.common.WebUtil;
import pass.web.servletapi.SessionManager;

@WebServlet ("/admin/manage_users.do")
public class ManageUsers extends HttpServlet
{

    private static final Logger LOGGER = Logger.getLogger(ManageUsers.class.getName());

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException
    {
        try (HibernateSession hs = new HibernateSession()) {
            UsersRepository repo = new UsersRepository(hs);
            List<User> allUsers = repo.list();
            Map<String, Object> data = new HashMap<>();
            data.put("allUsers", allUsers);
            data.put("activeUsers", SessionManager.getInstance().listActiveUsers());
            WebUtil.renderTemplate(request, response, "private/admin/manage_users.ftl", data);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException
    {
        String action = request.getParameter("action");
        String username = request.getParameter("username");
        String sessionId = request.getParameter("sessionId");
        switch (action) {
            case "reset_password":
                resetPassword(username, request, response);
                break;
            case "delete_user":
                deleteUser(username, request, response);
                break;
            case "end_session":
                endSession(sessionId, request, response);
                break;
            default:
                throw new ServiceException(ErrorCode.NOT_FOUND);
        }
    }

    private void resetPassword(String username,
                               HttpServletRequest request,
                               HttpServletResponse response)
            throws ServletException, IOException
    {
        if (username == null || username.equals("")) {
            response.sendError(404);    // Not found
            return;
        }
        VerificationService vServ = new VerificationService(Container.getInstance().getMailer());
        AccountService accountService = new AccountService(vServ);
        String new_password = Util.generateRandomPassword(16);
        boolean result = accountService.changePassword(username, new_password);
        Map<String, Object> data = new HashMap<>();
        data.put("success", result);
        data.put("username", username);
        data.put("new_password", new_password);
        WebUtil.renderTemplate(request, response, "private/admin/password_reset.ftl", data);
    }

    private void deleteUser(String username,
                            HttpServletRequest request,
                            HttpServletResponse response)
            throws ServletException, IOException
    {
        if (username == null || username.isEmpty()) {
            throw new ServiceException(ErrorCode.NOT_FOUND);
        }
        if (username.equals("admin")) {
            throw new ServiceException(ErrorCode.NOT_ALLOWED);
        }
        try (HibernateSession hs = new HibernateSession()) {
            UsersRepository usersRepo = new UsersRepository(hs);
            boolean deleted = usersRepo.deleteUser(username);
            if (!deleted) {
                throw new ServiceException(ErrorCode.DELETE_FAILED);
            }
            Map<String, Object> data = new HashMap<>();
            data.put("title", "User deleted");
            data.put("message", "User <mark>"
                                + username
                                + "</mark> deleted successfully.");
            WebUtil.renderTemplate(request, response, "private/message.ftl", data);
        }
    }

    private void endSession(String sessionId,
                            HttpServletRequest request,
                            HttpServletResponse response) throws IOException
    {
        SessionManager.getInstance().endSession(sessionId);
        response.sendRedirect(WebConfig.getInstance().view("manage_users"));
    }
}
