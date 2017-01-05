package pass.web.servletapi;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class RequestLoggerFilter implements Filter
{

    private static final Logger LOGGER = Logger.getLogger(RequestLoggerFilter.class.getName());

    @Override
    public void init(FilterConfig filterConfig) throws ServletException
    {
    }

    private void logParameter(String key, String value)
    {
        String valueToLog = value;
        if (key.contains("password")) {
            valueToLog = "*****";
        }
        LOGGER.log(Level.INFO, "    > {0} = {1}", new Object[] {
            key, valueToLog
        });
    }

    @Override
    public void doFilter(ServletRequest req,
                         ServletResponse res,
                         FilterChain chain)
            throws IOException, ServletException
    {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpSession session = request.getSession(false);

        boolean authenticated = false;
        String username = "";
        if (session != null) {
            authenticated = SessionStore.isAuthenticated(session);
            if (authenticated) {
                username = "(" + SessionStore.getAuthenticatedUser(session).username + ")";
            }
        }
        String servletPath = request.getServletPath();
        String method = request.getMethod();
        String protocol = request.getProtocol();
        String clientIP = request.getRemoteAddr();
        String qs = request.getQueryString();
        String url = request.getRequestURL().toString() + (qs == null ? "" : "?" + qs);
        String referer = request.getHeader("Referer");
        boolean sessionIdInURL = request.isRequestedSessionIdFromURL();
        boolean sessionIdInCookie = request.isRequestedSessionIdFromCookie();
        LOGGER.log(Level.INFO, "{0} {1} {2} {3} {4} {5}:", new Object[] {
            clientIP, method, servletPath, protocol, (authenticated ? "authenticated" : ""), username
        });
        LOGGER.log(Level.INFO, "    url: {0}", url);
        LOGGER.log(Level.INFO, "    referer: {0}", referer);
        LOGGER.log(Level.INFO, "    has session: {0}, sessionid in cookie: {1}, sessionid in url: {2}", new Object[] {
            (session != null), sessionIdInCookie, sessionIdInURL
        });
        for (Map.Entry<String, String[]> x : request.getParameterMap().entrySet()) {
            for (String y : x.getValue()) {
                logParameter(x.getKey(), y);
            }
        }
        chain.doFilter(req, res);
    }

    @Override
    public void destroy()
    {
    }
}
