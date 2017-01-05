package pass.web.servletapi;

import javax.servlet.http.HttpSession;
import pass.core.service.AuthenticatedUser;

public class SessionStore
{

    private static final String USER_SESSION_KEY = "auth_user";

    public static AuthenticatedUser getAuthenticatedUser(HttpSession session)
    {
        return (AuthenticatedUser) session.getAttribute(USER_SESSION_KEY);
    }

    public static void removeAuthenticatedUser(HttpSession session)
    {
        session.removeAttribute(USER_SESSION_KEY);
    }

    public static void storeAuthenticatedUser(HttpSession session,
                                              AuthenticatedUser user)
    {
        session.setAttribute(USER_SESSION_KEY, user);
    }

    public static boolean isAuthenticated(HttpSession session)
    {
        return session.getAttribute(USER_SESSION_KEY) != null;
    }
}
