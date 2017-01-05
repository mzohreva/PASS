package pass.web.view;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import pass.core.filesystem.SubmissionFiles;
import pass.core.hibernate.HibernateSession;
import pass.core.hibernate.SubmissionsRepository;
import pass.core.model.Submission;
import pass.core.scheduling.Status;
import pass.core.scheduling.TaskManager;
import pass.core.service.ErrorCode;
import pass.core.service.ServiceException;
import pass.web.common.Container;
import pass.web.common.WebUtil;

@WebServlet ("/admin/view_submission.do")
public class ViewSubmissionAdmin extends HttpServlet
{

    private final static Logger LOGGER = Logger.getLogger(ViewSubmissionAdmin.class.getName());

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException
    {
        int submissionId = Integer.parseInt(request.getParameter("id"));
        response.setContentType("text/html;charset=UTF-8");
        try (HibernateSession hs = new HibernateSession()) {
            SubmissionsRepository subRepo = new SubmissionsRepository(hs);
            Submission submission = subRepo.findById(submissionId);
            if (submission == null) {
                throw new ServiceException(ErrorCode.NOT_FOUND);
            }
            SubmissionFiles sfiles = new SubmissionFiles(submission);
            TaskManager tm = Container.getInstance().getTaskManager();
            Status.TaskState state = Status.TaskState.FINISHED;
            String statusMessage = null;
            {
                Status status = tm.getEvaluateSubmissionTaskStatus(submissionId);
                if (status != null) {
                    state = status.getState();
                    statusMessage = status.toString();
                }
            }
            Map<String, Object> data = new HashMap<>();
            data.put("sub", submission);
            data.put("files", sfiles.listFiles());
            data.put("state", state);
            data.put("statusMessage", statusMessage);
            WebUtil.renderTemplate(request, response, "private/admin/view_submission.ftl", data);
        }
    }
}
