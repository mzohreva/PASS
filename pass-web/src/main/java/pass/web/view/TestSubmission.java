package pass.web.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import pass.core.common.Config;
import pass.core.common.Tools;
import pass.core.filesystem.ProjectFiles;
import pass.core.hibernate.HibernateSession;
import pass.core.hibernate.ProjectsRepository;
import pass.core.model.Project;
import pass.core.service.AuthenticatedUser;
import pass.core.service.CompileOption;
import pass.core.service.SubmissionService;
import pass.core.service.UploadedFile;
import pass.web.common.Container;
import pass.web.common.WebConfig;
import pass.web.common.WebUtil;
import pass.web.servletapi.SessionStore;

@WebServlet ("/user/test.do")
@MultipartConfig
public class TestSubmission extends HttpServlet
{

    private final static Logger LOGGER = Logger.getLogger(TestSubmission.class.getName());

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException
    {
        int projectId = Integer.parseInt(request.getParameter("project"));
        try (HibernateSession hs = new HibernateSession()) {
            Project project = new ProjectsRepository(hs).findById(projectId);
            if (project == null || !project.isVisible()) {
                response.sendError(404);
                return;
            }
            ProjectFiles pfiles = new ProjectFiles(projectId);
            List<String> fileNames = pfiles.listAttachmentNames();
            String submissionInstructions = Tools.markdownToHTML(project.getSubmissionInstructions());
            Map<String, Object> data = new HashMap<>();
            data.put("maxNumberOfFilesPerSubmission", Config.getInstance().getMaxNumberOfFilesPerSubmission());
            data.put("maxTotalFileSizePerSubmission", Config.getInstance().getMaxTotalFileSizePerSubmission());
            data.put("project", project);
            data.put("projectFiles", fileNames);
            data.put("submissionInstructions", submissionInstructions);
            data.put("commonOptions", CompileOption.getOptionsForCategory(CompileOption.Category.COMMON));
            data.put("cDialects", CompileOption.getOptionsForCategory(CompileOption.Category.C_DIALECTS));
            data.put("cppDialects", CompileOption.getOptionsForCategory(CompileOption.Category.CPP_DIALECTS));
            WebUtil.renderTemplate(request, response, "private/user/submit.ftl", data);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException
    {
        AuthenticatedUser webUser = SessionStore.getAuthenticatedUser(request.getSession());
        // Read request
        int projectId = Integer.parseInt(request.getParameter("projectId"));
        List<UploadedFile> files = WebUtil.getUploadedFilesByName(request, "files");
        // Get compile options
        List<CompileOption> compileOptions = new ArrayList<>();
        for (CompileOption opt : CompileOption.getAllOptions()) {
            boolean checked = request.getParameter(opt.getId()) != null;
            if (checked) {
                compileOptions.add(opt);
            }
        }
        String cDialect = request.getParameter("C_DIALECT");
        if (cDialect != null && !"none".equals(cDialect)) {
            compileOptions.add(CompileOption.getOptionById(cDialect));
        }
        String cppDialect = request.getParameter("CPP_DIALECT");
        if (cppDialect != null && !"none".equals(cppDialect)) {
            compileOptions.add(CompileOption.getOptionById(cppDialect));
        }
        SubmissionService ss = new SubmissionService(Container.getInstance().getTaskManager());
        int submissionId = ss.submit(projectId, webUser.username, files, compileOptions);
        response.sendRedirect(WebConfig.getInstance().view("view_submission") + "?id=" + submissionId);
    }
}
