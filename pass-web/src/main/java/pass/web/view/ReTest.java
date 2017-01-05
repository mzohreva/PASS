package pass.web.view;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import pass.core.scheduling.TaskManager;
import pass.core.service.SubmissionService;
import pass.web.common.Container;
import pass.web.common.WebConfig;

@WebServlet ("/admin/retest.do")
public class ReTest extends HttpServlet
{

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException
    {
        TaskManager taskManager = Container.getInstance().getTaskManager();
        SubmissionService ss = new SubmissionService(taskManager);
        String submissions = request.getParameter("submissions");
        for (String idStr : submissions.split(" ")) {
            int submissionId = Integer.parseInt(idStr);
            ss.reTest(submissionId);
        }
        response.sendRedirect(WebConfig.getInstance().view("tasks"));
    }
}
