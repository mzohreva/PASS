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
import pass.core.model.VerificationCode;
import pass.core.service.AccountService;
import pass.core.service.VerificationService;
import pass.web.common.Container;
import pass.web.common.WebConfig;
import pass.web.common.WebUtil;

@WebServlet ("/verify.do")
public class Verify extends HttpServlet
{

    private final static Logger LOGGER = Logger.getLogger(Verify.class.getName());

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException
    {
        VerificationService vServ = new VerificationService(Container.getInstance().getMailer());
        VerificationCode vc = vServ.getVerificationCode(request.getParameter("code"));
        switch (vc.getReason()) {
            case ACCOUNT_CREATION: {
                AccountService accountService = new AccountService(vServ);
                accountService.verifyAccount(vc);
                Map<String, Object> data = new HashMap<>();
                data.put("title", "Account verified");
                data.put("message",
                         "You can now sign in with your username and "
                         + "password. Proceed to <a href=\""
                         + WebConfig.getInstance().view("signin")
                         + "\">sign in page</a>.");
                WebUtil.renderTemplate(request, response, "public/message.ftl", data);
                break;
            }
            case PASSWORD_RESET: {
                Map<String, Object> data = new HashMap<>();
                data.put("verificationCode", vc.getCode().toString());
                WebUtil.renderTemplate(request, response, "public/password_reset_form.ftl", data);
                break;
            }
        }
    }
}
