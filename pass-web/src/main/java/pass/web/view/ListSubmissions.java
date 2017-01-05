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
import pass.core.hibernate.SubmissionsRepository;
import pass.core.model.Project;
import pass.core.model.Submission;
import pass.core.service.AuthenticatedUser;
import pass.web.common.WebUtil;
import pass.web.servletapi.SessionStore;

@WebServlet ("/user/list_submissions.do")
public class ListSubmissions extends HttpServlet
{

    private static final Logger LOGGER = Logger.getLogger(ListSubmissions.class.getName());

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException
    {
        AuthenticatedUser webUser = SessionStore.getAuthenticatedUser(request.getSession());
        int projectId = Integer.parseInt(request.getParameter("project"));
        try (HibernateSession hs = new HibernateSession()) {
            ProjectsRepository projectsRepo = new ProjectsRepository(hs);
            Project project = projectsRepo.findById(projectId);
            if (project == null || !project.isVisible()) {
                response.sendError(404);
                return;
            }
            SubmissionsRepository repo = new SubmissionsRepository(hs);
            List<Submission> submissions;
            submissions = repo.listSubmissionsFor(webUser.username, projectId);
            Map<String, Object> data = new HashMap<>();
            data.put("project", project);
            data.put("submissionsList", submissions);
            WebUtil.renderTemplate(request, response, "private/user/list_submissions.ftl", data);
        }
    }
}
