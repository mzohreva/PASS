package pass.web.view;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import pass.core.filesystem.ProjectFiles;
import pass.core.filesystem.ScriptManager;
import pass.core.hibernate.HibernateSession;
import pass.core.hibernate.ProjectsRepository;
import pass.core.model.Project;
import pass.core.service.ProjectService;
import pass.core.service.UploadedFile;
import pass.web.common.ProjectsCache;
import pass.web.common.WebConfig;
import pass.web.common.WebUtil;

@WebServlet ("/admin/edit_project.do")
@MultipartConfig
public class EditProject extends HttpServlet
{

    private static final Logger LOGGER = Logger.getLogger(EditProject.class.getName());

    private List<String> listParameterValues(HttpServletRequest request, String name)
    {
        String[] values = request.getParameterValues(name);
        return (values == null) ? new ArrayList<>() : Arrays.asList(values);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException
    {
        int projectId = Integer.parseInt(request.getParameter("projectId"));
        String title = request.getParameter("title");
        String assignedStr = request.getParameter("assigned");
        String dueDateStr = request.getParameter("dueDate");
        String dueTimeStr = request.getParameter("dueTime");
        String instructions = request.getParameter("submissionInstructions");
        String compileScript = request.getParameter("selectCompileScript");
        String testScript = request.getParameter("selectTestScript");
        int gph = Integer.parseInt(request.getParameter("gracePeriodDays")) * 24;
        gph += Integer.parseInt(request.getParameter("gracePeriodHours"));
        if (title != null && title.length() >= 3
            && assignedStr != null && assignedStr.length() >= 8
            && dueDateStr != null && dueDateStr.length() >= 8
            && dueTimeStr != null && dueTimeStr.length() >= 5
            && compileScript != null && testScript != null) {
            boolean visible = request.getParameter("visible") != null;
            Date assigned;
            Date due;
            try {
                assigned = new SimpleDateFormat("MM/dd/yyyy")
                        .parse(assignedStr);
                due = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
                        .parse(dueDateStr + " " + dueTimeStr);
            }
            catch (ParseException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
                response.sendError(401);
                return;
            }
            UploadedFile gradingTests
                    = WebUtil.partToUploadedFile(
                            request.getPart("fileGradingTests"));
            List<String> attachmentsToRemove
                    = listParameterValues(request, "attachmentsToRemove");
            List<UploadedFile> attachmentsToAdd
                    = WebUtil.getUploadedFilesByName(
                            request, "attachmentsToAdd");
            List<String> auxiliaryFilesToRemove
                    = listParameterValues(request, "auxiliaryFilesToRemove");
            List<UploadedFile> auxiliaryFilesToAdd
                    = WebUtil.getUploadedFilesByName(
                            request, "auxiliaryFilesToAdd");

            ProjectService projectServ = new ProjectService();
            boolean success = projectServ.editProject(projectId,
                                                      title,
                                                      assigned,
                                                      due,
                                                      gph,
                                                      visible,
                                                      instructions,
                                                      compileScript,
                                                      testScript,
                                                      gradingTests,
                                                      attachmentsToRemove,
                                                      attachmentsToAdd,
                                                      auxiliaryFilesToRemove,
                                                      auxiliaryFilesToAdd);
            ProjectsCache.getInstance().invalidate();
            if (success) {
                response.sendRedirect(
                        WebConfig.getInstance().view("manage_projects"));
            }
            else {
                response.sendError(401);
            }
        }
        else {
            response.sendError(401);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException
    {
        try (HibernateSession hs = new HibernateSession()) {
            int projectId = Integer.parseInt(request.getParameter("project"));
            Project project = new ProjectsRepository(hs).findById(projectId);
            ProjectFiles pfiles = new ProjectFiles(projectId);
            Map<String, Object> data = new HashMap<>();
            data.put("project", project);
            data.put("attachments", pfiles.listAttachmentNames());
            data.put("auxiliaryFiles", pfiles.listAuxiliaryFileNames());
            data.put("gradingTests", pfiles.listGradingTestNames());
            data.put("compileScript", pfiles.getCompileScript());
            data.put("testScript", pfiles.getTestScript());
            data.put("allCompileScripts", ScriptManager.getAllCompileScripts());
            data.put("allTestScripts", ScriptManager.getAllTestScripts());
            WebUtil.renderTemplate(request, response, "private/admin/edit_project.ftl", data);
        }
    }
}
