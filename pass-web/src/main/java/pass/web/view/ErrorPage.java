package pass.web.view;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import pass.core.service.AuthenticatedUser;
import pass.core.service.ServiceException;
import pass.web.common.WebUtil;
import pass.web.servletapi.SessionStore;

@WebServlet ("/error.do")
public class ErrorPage extends HttpServlet
{

    private final static Logger LOGGER = Logger.getLogger(ErrorPage.class.getName());

    private String generateDescriptionList(Map<String, String> items,
                                           String dd_CSS_class)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("<dl class='dl-horizontal'>");
        for (Map.Entry<String, String> kv : items.entrySet()) {
            sb.append("<dt>").append(kv.getKey()).append("</dt>");
            sb.append("<dd class='").append(dd_CSS_class).append("'>");
            sb.append(kv.getValue()).append("</dd>");
        }
        sb.append("</dl>");
        return sb.toString();
    }

    protected void processError(HttpServletRequest request,
                                HttpServletResponse response)
            throws ServletException, IOException
    {
        AuthenticatedUser webUser = SessionStore.getAuthenticatedUser(request.getSession());

        Throwable throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        String servletName = (String) request.getAttribute("javax.servlet.error.servlet_name");
        if (servletName == null) {
            servletName = "Unknown";
        }
        String requestUri = (String) request.getAttribute("javax.servlet.error.request_uri");
        if (requestUri == null) {
            requestUri = "Unknown";
        }

        int errorCode = statusCode == null ? 500 : statusCode;
        String errorDescription = "Internal Error";
        if (throwable instanceof ServiceException) {
            ServiceException se = (ServiceException) throwable;
            errorCode = se.getErrorCode().getCode();
            errorDescription = se.getErrorCode().getDescription();
            LOGGER.log(Level.INFO,
                       "ServiceException from {0}: ErrorCode = {1} {2}",
                       new Object[] {
                           servletName,
                           String.valueOf(se.getErrorCode().getCode()),
                           se.getErrorCode()
                       });
        }
        else if (throwable != null) {
            // Other errors
            Map<String, String> info = new LinkedHashMap<>();
            info.put("Servlet", servletName.replaceAll("pass.web.View.", ""));
            info.put("Request URI", requestUri);
            info.put("Exception", throwable.getClass().getName());
            info.put("Message", throwable.getMessage());
            UUID errorId = UUID.randomUUID();
            info.put("Error Code", errorId.toString());
            errorDescription = generateDescriptionList(info, "text-danger")
                               + "When enquiring about this error, please "
                               + "include the error code listed above.";
            LOGGER.log(Level.SEVERE,
                       "Exception from "
                       + servletName
                       + " {" + errorId.toString() + "}",
                       throwable);
        }

        final String template = (webUser == null)
                                ? "public/error_page.ftl"
                                : "private/error_page.ftl";
        Map<String, Object> data = new HashMap<>();
        data.put("home_page", WebUtil.getUserHomePage(webUser));
        data.put("error_code", errorCode);
        data.put("error_description", errorDescription);
        WebUtil.renderTemplate(request, response, template, data);
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException
    {
        processError(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException
    {
        processError(request, response);
    }
}
