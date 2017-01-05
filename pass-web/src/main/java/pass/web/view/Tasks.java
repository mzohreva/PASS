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
import pass.core.scheduling.TaskManager;
import pass.core.scheduling.TaskStatus;
import pass.core.scheduling.TaskType;
import pass.web.common.Container;
import pass.web.common.WebUtil;

@WebServlet ("/admin/tasks.do")
public class Tasks extends HttpServlet
{

    private final static Logger LOGGER = Logger.getLogger(Tasks.class.getName());

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException
    {
        TaskManager tm = Container.getInstance().getTaskManager();
        List<TaskStatus> queuedTasks = tm.listQueuedTasks();
        List<TaskStatus> runningTasks = tm.listRunningTasks();
        long nEvaluations = tm.completedTasksByType(TaskType.EVALUATION);
        long nCleanups = tm.completedTasksByType(TaskType.CLEANUP);
        long nZips = tm.completedTasksByType(TaskType.ZIP);
        Map<String, Object> data = new HashMap<>();
        data.put("queuedTasks", queuedTasks);
        data.put("runningTasks", runningTasks);
        data.put("completedEvaluations", nEvaluations);
        data.put("completedCleanups", nCleanups);
        data.put("completedZips", nZips);
        WebUtil.renderTemplate(request, response, "private/admin/tasks.ftl", data);
    }
}
