package pass.web.view;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import pass.core.filesystem.ArchiveManager;
import pass.core.scheduling.TaskManager;
import pass.core.scheduling.TaskStatus;
import pass.core.scheduling.TaskType;
import pass.core.service.ErrorCode;
import pass.core.service.ServiceException;
import pass.web.common.Container;
import pass.web.common.WebConfig;
import pass.web.common.WebUtil;

@WebServlet ("/admin/manage_archives.do")
public class ManageArchives extends HttpServlet
{

    private static final Logger LOGGER = Logger.getLogger(ManageArchives.class.getName());

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException
    {
        ArchiveManager archiveManager = new ArchiveManager();
        TaskManager tm = Container.getInstance().getTaskManager();
        List<TaskStatus> zipTasks = tm.listTasksByType(TaskType.ZIP);
        Map<String, Object> data = new HashMap<>();
        data.put("archives", archiveManager.listArchives());
        data.put("zipTasks", zipTasks);
        WebUtil.renderTemplate(request, response, "private/admin/manage_archives.ftl", data);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException
    {
        String action = request.getParameter("action");
        String archive = request.getParameter("archive");
        String mav = WebConfig.getInstance().view("manage_archives");
        switch (action) {
            case "download_archive": {
                Path archivePath = new ArchiveManager().getArchive(archive);
                WebUtil.downloadFile(archivePath, response);
                break;
            }
            case "delete_archive": {
                Path archivePath = new ArchiveManager().getArchive(archive);
                Files.deleteIfExists(archivePath);
                response.sendRedirect(mav);
                break;
            }
            default: {
                throw new ServiceException(ErrorCode.NOT_FOUND);
            }
        }
    }
}
