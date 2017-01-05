package pass.web.view;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import pass.core.service.ProjectService;
import pass.core.service.UploadedFile;
import pass.web.common.ProjectsCache;
import pass.web.common.WebConfig;
import pass.web.common.WebUtil;

@WebServlet ("/admin/new_project.do")
@MultipartConfig
public class NewProject extends HttpServlet
{

    private static final Logger LOGGER = Logger.getLogger(NewProject.class.getName());

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException
    {
        // Process the request
        String title = request.getParameter("title");
        String assignedStr = request.getParameter("assigned");
        String dueDateStr = request.getParameter("dueDate");
        String dueTimeStr = request.getParameter("dueTime");
        String submissionInstructions = request.getParameter("submissionInstructions");
        String compileScript = request.getParameter("selectCompileScript");
        String testScript = request.getParameter("selectTestScript");
        int gracePeriodHours = Integer.parseInt(request.getParameter("gracePeriodDays")) * 24;
        gracePeriodHours += Integer.parseInt(request.getParameter("gracePeriodHours"));
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
            UploadedFile gradingTests;
            gradingTests = WebUtil.partToUploadedFile(request.getPart("fileGradingTests"));
            List<UploadedFile> attachments;
            attachments = WebUtil.getUploadedFilesByName(request, "attachments");
            List<UploadedFile> auxiliaryFiles;
            auxiliaryFiles = WebUtil.getUploadedFilesByName(request, "auxiliary");
            ProjectService projectService = new ProjectService();
            boolean success = projectService.newProject(title,
                                                        assigned,
                                                        due,
                                                        gracePeriodHours,
                                                        visible,
                                                        submissionInstructions,
                                                        compileScript,
                                                        testScript,
                                                        gradingTests,
                                                        attachments,
                                                        auxiliaryFiles);
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
}
