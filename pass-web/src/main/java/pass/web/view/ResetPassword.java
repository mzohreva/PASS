package pass.web.view;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import pass.core.model.VerificationCode;
import pass.core.service.AccountService;
import pass.core.service.VerificationService;
import pass.web.common.Container;
import pass.web.common.WebConfig;
import pass.web.common.WebUtil;

@WebServlet ("/reset_password.do")
public class ResetPassword extends HttpServlet
{

    private final static Logger LOGGER = Logger.getLogger(ResetPassword.class.getName());

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException
    {
        VerificationService vServ = new VerificationService(Container.getInstance().getMailer());
        VerificationCode vc = vServ.getVerificationCode(request.getParameter("code"));
        AccountService accServ = new AccountService(vServ);
        boolean success = accServ.resetPassword(vc, request.getParameter("password"));
        if (success) {
            Map<String, Object> data = new HashMap<>();
            data.put("title", "Password changed successfully");
            data.put("message",
                     "You can now sign in using your new password. "
                     + "Proceed to <a href=\""
                     + WebConfig.getInstance().view("signin")
                     + "\">sign in page</a>.");
            WebUtil.renderTemplate(request, response, "public/message.ftl", data);
        }
        else {
            LOGGER.log(Level.WARNING,
                       "Failed to reset password for {0}",
                       vc.toString());
        }
    }
}
