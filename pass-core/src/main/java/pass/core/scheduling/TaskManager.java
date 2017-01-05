package pass.core.scheduling;

import java.util.List;

public interface TaskManager
{

    void evaluateSubmission(int submissionId);

    void cleanupSubmissions(String username, int projectId);

    void zipSubmissions(int projectId);

    Status getEvaluateSubmissionTaskStatus(int submissionId);

    List<TaskStatus> listQueuedTasks();

    List<TaskStatus> listRunningTasks();

    List<TaskStatus> listTasksByType(TaskType taskType);

    long completedTasksByType(TaskType taskType);

    void shutdown();

}
