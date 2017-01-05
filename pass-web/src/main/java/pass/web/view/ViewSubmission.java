package pass.web.view;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import pass.core.hibernate.HibernateSession;
import pass.core.hibernate.SubmissionsRepository;
import pass.core.model.Submission;
import pass.core.scheduling.Status;
import pass.core.scheduling.TaskManager;
import pass.core.service.AuthenticatedUser;
import pass.core.service.ErrorCode;
import pass.core.service.ServiceException;
import pass.web.common.Container;
import pass.web.common.WebUtil;
import pass.web.servletapi.SessionStore;

@WebServlet ("/user/view_submission.do")
public class ViewSubmission extends HttpServlet
{

    private final static Logger LOGGER = Logger.getLogger(ViewSubmission.class.getName());

    private long delayCurve(double x)
    {
        final double min = -100;
        final double max = 120;
        final double mid_x = 0;
        final double growth = 0.1;
        // This is a logistic function
        // At x = 0, it returns 10 and the maximum value is 120
        double y = min + (max - min) / (1 + Math.exp(growth * (mid_x - x)));
        return (long) Math.floor(y);
    }

    private void setPageRefreshDelay(Submission submission,
                                     Status.TaskState taskState,
                                     HttpServletResponse response)
    {
        long delay = 0;
        boolean shouldRefresh = false;
        if (taskState != Status.TaskState.FINISHED) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(submission.getSubmissionDate());
            cal.add(Calendar.HOUR, 1);
            Date now = new Date();
            if (now.before(cal.getTime())) {
                shouldRefresh = true;
                double t = now.getTime() - submission.getSubmissionDate().getTime();
                t = t / (60000);        // In minutes scale
                delay = delayCurve(t);  // TODO: consider server load factor
            }
        }
        if (shouldRefresh) {
            response.setHeader("refresh", "" + delay);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException
    {
        AuthenticatedUser webUser = SessionStore.getAuthenticatedUser(request.getSession());
        int submissionId = Integer.parseInt(request.getParameter("id"));
        try (HibernateSession hs = new HibernateSession()) {
            SubmissionsRepository subRepo = new SubmissionsRepository(hs);
            Submission submission = subRepo.findById(submissionId);
            if (submission == null) {
                throw new ServiceException(ErrorCode.NOT_FOUND);
            }
            if (!(webUser.isAdmin() || webUser.username.equals(submission.getUser().getUsername()))) {
                throw new ServiceException(ErrorCode.NOT_FOUND);
            }
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
            data.put("submissionId", submissionId);
            data.put("submissionDate", submission.getSubmissionDate());
            data.put("isLate", submission.isLate());
            data.put("daysLateHumanReadable", submission.getDaysLateHumanReadable());
            data.put("projectTitle", submission.getProject().getTitle());
            data.put("state", state);
            data.put("statusMessage", statusMessage);
            data.put("compileSuccessful", submission.isCompileSuccessful());
            data.put("compilerOutput", submission.getCompileMessageHtmlEscaped());
            data.put("testResult", submission.getTestResult());
            data.put("compileOptions", submission.getCompileOptions());
            setPageRefreshDelay(submission, state, response);
            WebUtil.renderTemplate(request, response, "private/user/view_submission.ftl", data);
        }
    }
}
