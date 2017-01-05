package pass.web.view;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import pass.core.service.AccountService;
import pass.core.service.VerificationService;
import pass.web.common.Container;
import pass.web.common.WebConfig;
import pass.web.common.WebUtil;

@WebServlet ("/register.do")
public class Register extends HttpServlet
{

    private final static Logger LOGGER = Logger.getLogger(Register.class.getName());

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException
    {
        String username = request.getParameter("username");
        String studentId = request.getParameter("studentId");
        String password = request.getParameter("password");
        if (username == null || password == null || studentId == null) {
            response.sendError(401);
            return;
        }
        VerificationService vServ = new VerificationService(Container.getInstance().getMailer());
        AccountService accountService = new AccountService(vServ);
        accountService.register(username, password, studentId);
        Map<String, Object> data = new HashMap<>();
        data.put("title", "Registration successful");
        data.put("message",
                 "Check your email for a message from <code>"
                 + WebConfig.getInstance().getServerEmailAddress()
                 + "</code>. You need to click on the link in the email "
                 + "to activate your account.");
        WebUtil.renderTemplate(request, response, "public/message.ftl", data);
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException
    {
        WebUtil.renderTemplate(request, response, "public/register.ftl");
    }
}
