package pass.web.view;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import pass.core.hibernate.HibernateSession;
import pass.core.hibernate.ProjectsRepository;
import pass.core.model.Project;
import pass.core.model.Submission;
import pass.web.common.WebConfig;
import pass.web.common.WebUtil;

@WebServlet ("/admin/manage_submissions.do")
public class ManageSubmissions extends HttpServlet
{

    private static final Logger LOGGER = Logger.getLogger(ManageSubmissions.class.getName());

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException
    {
        int projectId = Integer.parseInt(request.getParameter("project"));
        try (HibernateSession hs = new HibernateSession()) {
            Project project = new ProjectsRepository(hs).findById(projectId);
            String showAllSubmissions = request.getParameter("show_all");
            Set<Submission> ss;
            String current_mode;
            if (showAllSubmissions != null && showAllSubmissions.equals("1")) {
                ss = project.getSubmissions();
                current_mode = "all";
            }
            else {
                ss = project.getLastSubmissionsOfUsers();
                current_mode = "latest";
            }
            String msv = WebConfig.getInstance().view("manage_submissions");
            String allLink = msv + "?project=" + projectId + "&show_all=1";
            String latestLink = msv + "?project=" + projectId;
            Map<String, Object> data = new HashMap<>();
            data.put("projectSubmissions", ss);
            data.put("all_link", allLink);
            data.put("latest_link", latestLink);
            data.put("current_mode", current_mode);
            data.put("project", project);
            WebUtil.renderTemplate(request, response, "private/admin/manage_submissions.ftl", data);
        }
    }
}
