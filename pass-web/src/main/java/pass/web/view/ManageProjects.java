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
import pass.core.filesystem.ScriptManager;
import pass.core.hibernate.HibernateSession;
import pass.core.hibernate.ProjectsRepository;
import pass.core.model.Project;
import pass.core.scheduling.TaskManager;
import pass.core.service.ErrorCode;
import pass.core.service.ProjectService;
import pass.core.service.ServiceException;
import pass.web.common.Container;
import pass.web.common.ProjectsCache;
import pass.web.common.WebConfig;
import pass.web.common.WebUtil;

@WebServlet ("/admin/manage_projects.do")
public class ManageProjects extends HttpServlet
{

    private static final Logger LOGGER = Logger.getLogger(ManageProjects.class.getName());

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException
    {
        try (HibernateSession hs = new HibernateSession()) {
            List<Project> projects = new ProjectsRepository(hs).list(false);
            Map<String, Object> data = new HashMap<>();
            data.put("projectsList", projects);
            data.put("allCompileScripts", ScriptManager.getAllCompileScripts());
            data.put("allTestScripts", ScriptManager.getAllTestScripts());
            WebUtil.renderTemplate(request, response, "private/admin/manage_projects.ftl", data);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException
    {
        String action = request.getParameter("action");
        int projectId = Integer.parseInt(request.getParameter("project"));
        int amount = Integer.parseInt(request.getParameter("amount"));
        ProjectService projectServ = new ProjectService();
        String mpv = WebConfig.getInstance().view("manage_projects");
        switch (action) {
            case "toggle_visible": {
                projectServ.toggleVisiblity(projectId);
                ProjectsCache.getInstance().invalidate();
                response.sendRedirect(mpv);
                break;
            }
            case "add_due": {
                projectServ.addDaysToDueDate(projectId, amount);
                response.sendRedirect(mpv);
                break;
            }
            case "delete_project": {
                projectServ.deleteProject(projectId);
                ProjectsCache.getInstance().invalidate();
                response.sendRedirect(mpv);
                break;
            }
            case "zip_submissions": {
                TaskManager tm = Container.getInstance().getTaskManager();
                tm.zipSubmissions(projectId);
                response.sendRedirect(WebConfig.getInstance().view("manage_archives"));
                break;
            }
            default:
                throw new ServiceException(ErrorCode.NOT_FOUND);
        }
    }
}
