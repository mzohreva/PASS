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
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import pass.core.common.Config;

@WebFilter ("/*")
public class RefererValidationFilter implements Filter
{

    private static final Logger LOGGER = Logger.getLogger(RefererValidationFilter.class.getName());

    private String serverUrl;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException
    {
        serverUrl = Config.getInstance().getServerUrl();
    }

    private void log(String servletPath, String referer, String decision)
    {
        LOGGER.log(Level.INFO,
                   "serverUrl = {0}, Referer = {1}, servletPath = {2} ... {3}",
                   new Object[] {serverUrl, referer, servletPath, decision});
    }

    @Override
    public void doFilter(ServletRequest req,
                         ServletResponse res,
                         FilterChain chain)
            throws IOException, ServletException
    {
        HttpServletRequest request = (HttpServletRequest) req;

        if (request.getMethod().equals("POST")) {
            String referer = request.getHeader("Referer");
            if (referer != null) {
                // Check the header value against serverUrl
                boolean ok = referer.startsWith(serverUrl);
                if (!ok) {
                    log(request.getServletPath(), referer, "access denied");
                    return;
                }
            }
        }
        chain.doFilter(req, res);
    }

    @Override
    public void destroy()
    {
    }
}
