package pass.web.view;

import java.io.IOException;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import pass.core.service.AuthenticatedUser;
import pass.core.service.AuthenticationService;
import pass.core.service.ErrorCode;
import pass.core.service.ServiceException;
import pass.web.common.WebConfig;
import pass.web.common.WebUtil;
import pass.web.servletapi.SessionStore;

@WebServlet ("/signin.do")
public class SignIn extends HttpServlet
{

    private static final Logger LOGGER = Logger.getLogger(SignIn.class.getName());

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException
    {
        HttpSession session = request.getSession();
        if (SessionStore.isAuthenticated(session)) {
            AuthenticatedUser webUser = SessionStore.getAuthenticatedUser(session);
            response.sendRedirect(WebUtil.getUserHomePage(webUser));
            return;
        }
        WebUtil.renderTemplate(request, response, "public/signin.ftl");
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException
    {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        AuthenticationService authService = new AuthenticationService();
        AuthenticationService.AuthenticationResult result;
        result = authService.authenticate(username, password);
        String siv = WebConfig.getInstance().view("signin");
        switch (result.status) {
            case AUTHENTICATED:
                SessionStore.storeAuthenticatedUser(request.getSession(),
                                                    result.user);
                response.sendRedirect(WebUtil.getUserHomePage(result.user));
                break;
            case INVALID_CREDENTIALS:
                response.sendRedirect(siv + "?err=1");
                break;
            case ACCOUNT_NOT_VERIFIED:
                response.sendRedirect(siv + "?err=2");
                break;
            case UNDER_MAINTENANCE:
                throw new ServiceException(ErrorCode.UNDER_MAINTENANCE);
        }
    }
}
