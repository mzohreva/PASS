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
import javax.servlet.http.HttpServletResponse;
import pass.core.common.Config;

@WebFilter ("/*")
public class RefererValidationFilter implements Filter
{

    private static final Logger LOGGER = Logger.getLogger(RefererValidationFilter.class.getName());

    private String serverUrl;

    private final String[] exemptViews = {
        "/signin.do",
        "/register.do",
        "/verify.do",
        "/recover_account.do"
    };

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

    private boolean isExempt(String servletPath)
    {
        for (String exempt : exemptViews) {
            if (exempt.equals(servletPath)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void doFilter(ServletRequest req,
                         ServletResponse res,
                         FilterChain chain)
            throws IOException, ServletException
    {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;

        String referer = request.getHeader("Referer");
        if (!isExempt(request.getServletPath())) {
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
