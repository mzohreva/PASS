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
import pass.web.common.WebUtil;

@WebServlet ("/recover_account.do")
public class RecoverAccount extends HttpServlet
{

    private final static Logger LOGGER = Logger.getLogger(RecoverAccount.class.getName());

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException
    {
        String username = request.getParameter("username");
        if (username == null) {
            response.sendError(401);
            return;
        }
        VerificationService vServ = new VerificationService(Container.getInstance().getMailer());
        AccountService accountService = new AccountService(vServ);
        accountService.recover(username);
        Map<String, Object> data = new HashMap<>();
        data.put("title", "Recover your account");
        data.put("message",
                 "If the information you entered match an "
                 + "existing account, we send a password reset link to the "
                 + "email address on file. Check your email and click on "
                 + "the password reset link to continue.");
        WebUtil.renderTemplate(request, response, "public/message.ftl", data);
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException
    {
        WebUtil.renderTemplate(request, response, "public/recover_account.ftl");
    }
}
