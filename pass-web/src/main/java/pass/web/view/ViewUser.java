package pass.web.view;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import pass.core.hibernate.HibernateSession;
import pass.core.hibernate.SubmissionsRepository;
import pass.core.hibernate.UsersRepository;
import pass.core.model.Submission;
import pass.core.model.User;
import pass.core.service.ErrorCode;
import pass.core.service.ServiceException;
import pass.web.common.WebUtil;

@WebServlet ("/admin/view_user.do")
public class ViewUser extends HttpServlet
{

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException
    {
        String username = request.getParameter("user");
        try (HibernateSession hs = new HibernateSession()) {
            UsersRepository userRepo = new UsersRepository(hs);
            User user = userRepo.findByUsername(username);
            if (user == null) {
                throw new ServiceException(ErrorCode.NOT_FOUND);
            }
            SubmissionsRepository subRepo = new SubmissionsRepository(hs);
            List<Submission> submissions = subRepo.listSubmissionsFor(username);

            Map<String, Object> data = new HashMap<>();
            data.put("user", user);
            data.put("submissions", submissions);
            WebUtil.renderTemplate(request, response, "private/admin/view_user.ftl", data);
        }
    }
}
