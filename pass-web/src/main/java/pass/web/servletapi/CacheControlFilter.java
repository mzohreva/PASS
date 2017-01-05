package pass.web.servletapi;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class CacheControlFilter implements Filter
{

    private boolean enableCaching;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException
    {
        enableCaching = Boolean.parseBoolean(filterConfig.getInitParameter("enableCaching"));
    }

    @Override
    public void doFilter(ServletRequest req,
                         ServletResponse res,
                         FilterChain chain)
            throws IOException, ServletException
    {
        HttpServletResponse response = (HttpServletResponse) res;
        String value = (enableCaching
                        ? "public, max-age=31536000"
                        : "no-cache, no-store, must-revalidate");
        response.setHeader("Cache-Control", value);
        chain.doFilter(req, res);
    }

    @Override
    public void destroy()
    {
    }
}
