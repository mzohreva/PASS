package pass.web.servletapi;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import pass.core.service.AuthenticatedUser;
import pass.web.common.WebConfig;

public class AuthorizationFilter implements Filter
{

    private static final Logger LOGGER = Logger.getLogger(AuthorizationFilter.class.getName());

    private boolean adminOnly;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException
    {
        adminOnly = Boolean.parseBoolean(filterConfig.getInitParameter("adminOnly"));
    }

    private void log(String url, String decision)
    {
        LOGGER.log(Level.INFO,
                   "{0} for url = {1} ... {2}",
                   new Object[] {
                       adminOnly ? "(Admin-Only)" : "(Authorized-Only)",
                       url, decision}
        );
    }

    @Override
    public void doFilter(ServletRequest req,
                         ServletResponse res,
                         FilterChain chain)
            throws IOException, ServletException
    {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;
        HttpSession session = request.getSession();

        if (!SessionStore.isAuthenticated(session)) {
            log(request.getRequestURI(), "not authenticated");
            response.sendRedirect(WebConfig.getInstance().view("signin"));
            return;
        }
        AuthenticatedUser webUser = SessionStore.getAuthenticatedUser(session);
        if (adminOnly && !webUser.isAdmin()) {
            log(request.getRequestURI(),
                "access denied for '" + webUser.username + "'");
            response.sendError(403);    // Forbidden
            return;
        }
        webUser.updateLastAccess(request.getServletPath());
        chain.doFilter(req, res);
    }

    @Override
    public void destroy()
    {
    }
}
