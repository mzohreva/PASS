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
import pass.core.hibernate.ProjectsRepository;
import pass.core.model.UserProject;
import pass.core.service.AuthenticatedUser;
import pass.web.common.WebUtil;
import pass.web.servletapi.SessionStore;

@WebServlet ("/user/list_projects.do")
public class ListProjects extends HttpServlet
{

    private static final Logger LOGGER = Logger.getLogger(ListProjects.class.getName());

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException
    {
        AuthenticatedUser webUser = SessionStore.getAuthenticatedUser(request.getSession());
        try (HibernateSession hs = new HibernateSession()) {
            ProjectsRepository repo = new ProjectsRepository(hs);
            List<UserProject> upl = repo.listUserProjects(true, webUser.username);
            Map<String, Object> data = new HashMap<>();
            data.put("userProjectsList", upl);
            WebUtil.renderTemplate(request, response, "private/user/list_projects.ftl", data);
        }
    }
}
