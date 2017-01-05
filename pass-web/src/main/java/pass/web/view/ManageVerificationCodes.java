package pass.web.view;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import pass.core.hibernate.HibernateSession;
import pass.core.hibernate.VerificationCodeRepository;
import pass.core.model.VerificationCode;
import pass.web.common.WebUtil;

@WebServlet ("/admin/manage_codes.do")
public class ManageVerificationCodes extends HttpServlet
{

    private static final Logger LOGGER = Logger.getLogger(ManageVerificationCodes.class.getName());

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException
    {
        try (HibernateSession hs = new HibernateSession()) {
            List<VerificationCode> allCodes = new VerificationCodeRepository(hs).listAll();
            Map<String, Object> data = new HashMap<>();
            data.put("allCodes", allCodes);
            WebUtil.renderTemplate(request, response, "private/admin/manage_codes.ftl", data);
        }
    }
}
