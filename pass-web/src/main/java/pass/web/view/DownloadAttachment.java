package pass.web.view;

import java.io.IOException;
import java.nio.file.Path;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import pass.core.filesystem.ProjectFiles;
import pass.core.hibernate.HibernateSession;
import pass.core.hibernate.ProjectsRepository;
import pass.core.model.Project;
import pass.web.common.WebUtil;

@WebServlet ("/user/download.do")
public class DownloadAttachment extends HttpServlet
{

    protected void processRequest(HttpServletRequest request,
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
            String fileName = request.getParameter("file");
            ProjectFiles pfiles = new ProjectFiles(projectId);
            if (!pfiles.attachmentExists(fileName)) {
                response.sendError(404);
                return;
            }
            // Everything is OK, so download the file to client
            Path attachment = pfiles.getAttachment(fileName);
            WebUtil.downloadFile(attachment, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException
    {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException
    {
        processRequest(request, response);
    }
}
