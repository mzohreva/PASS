package pass.web.common;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import pass.core.model.Project;
import pass.core.service.AuthenticatedUser;
import pass.core.service.UploadedFile;
import pass.web.servletapi.SessionStore;

public class WebUtil
{

    private static final Logger LOGGER = Logger.getLogger(WebUtil.class.getName());

    public static UploadedFile partToUploadedFile(Part p) throws IOException
    {
        return new UploadedFile(p.getInputStream(),
                                p.getContentType(),
                                p.getSubmittedFileName(),
                                p.getSize());
    }

    public static List<UploadedFile> getUploadedFilesByName(
            HttpServletRequest request,
            String name) throws IOException, ServletException
    {
        List<UploadedFile> uploadedFiles = new ArrayList<>();
        for (Part p : request.getParts()) {
            if (name.equals(p.getName())) {
                String fn = p.getSubmittedFileName();
                if (fn != null && !fn.equals("")) {
                    uploadedFiles.add(partToUploadedFile(p));
                }
            }
        }
        return uploadedFiles;
    }

    public static void downloadFile(Path file, HttpServletResponse response)
            throws IOException
    {
        response.setHeader("Content-Disposition",
                           "attachment; filename=\""
                           + file.getFileName().toString() + "\"");
        response.setHeader("Content-Length", String.valueOf(Files.size(file)));
        response.setContentType(Files.probeContentType(file));
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            Files.copy(file, outputStream);
            outputStream.flush();
        }
    }

    public static String getUserHomePage(AuthenticatedUser webUser)
    {
        String view;
        if (webUser == null) {
            view = "signin";
        }
        else if (webUser.isAdmin()) {
            view = "manage_projects";
        }
        else {
            view = "list_projects";
        }
        return WebConfig.getInstance().view(view);
    }

    public static void renderTemplate(HttpServletRequest request,
                                      HttpServletResponse response,
                                      String template,
                                      Map<String, Object> dataModel)
            throws IOException
    {
        dataModel.put("WebConfig", WebConfig.getInstance());
        HttpSession session = request.getSession();
        if (SessionStore.isAuthenticated(session)) {
            AuthenticatedUser webUser;
            webUser = SessionStore.getAuthenticatedUser(session);
            dataModel.put("User", webUser);
            List<Project> projects;
            projects = ProjectsCache.getInstance().listVisibleProjects();
            dataModel.put("Projects", projects);
        }
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("X-Frame-Options", "SAMEORIGIN");
        try (PrintWriter out = response.getWriter()) {
            Template t = Container.getInstance().getTemplate(template);
            t.process(dataModel, out);
        }
        catch (TemplateException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public static void renderTemplate(HttpServletRequest request,
                                      HttpServletResponse response,
                                      String template)
            throws IOException
    {
        renderTemplate(request, response, template, new HashMap<>());
    }
}
