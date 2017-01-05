package pass.core.scheduling;

import java.io.Serializable;
import java.util.UUID;

public class TaskSpecification implements Serializable
{

    private final TaskType type;
    private final int submissionId;
    private final String username;
    private final int projectId;

    private TaskSpecification(TaskType type,
                              int submissionId,
                              String username,
                              int projectId)
    {
        this.type = type;
        this.submissionId = submissionId;
        this.username = username;
        this.projectId = projectId;
    }

    public static TaskSpecification evaluation(int submissionId)
    {
        return new TaskSpecification(TaskType.EVALUATION,
                                     submissionId,
                                     "",
                                     0);
    }

    public static TaskSpecification cleanup(String username, int projectId)
    {
        return new TaskSpecification(TaskType.CLEANUP,
                                     0,
                                     username,
                                     projectId);
    }

    public static TaskSpecification zip(int projectId)
    {
        return new TaskSpecification(TaskType.ZIP,
                                     0,
                                     "",
                                     projectId);
    }

    public BaseTask makeCorrespondingTask(UUID taskId,
                                          StatusUpdater statusUpdater)
    {
        switch (type) {
            case EVALUATION:
                return new EvaluateSubmissionTask(taskId,
                                                  this,
                                                  submissionId,
                                                  statusUpdater);
            case CLEANUP:
                return new CleanupTask(taskId,
                                       this,
                                       username,
                                       projectId,
                                       statusUpdater);
            case ZIP:
                return new ZipSubmissionsTask(taskId,
                                              this,
                                              projectId,
                                              statusUpdater);
        }
        return null;
    }

    public TaskType getType()
    {
        return type;
    }

    public boolean isSubmissionEvaluation(int submissionId)
    {
        return this.type == TaskType.EVALUATION
               && this.submissionId == submissionId;
    }

    @Override
    public String toString()
    {
        switch (type) {
            case EVALUATION:
                return "Evaluate(" + submissionId + ")";
            case CLEANUP:
                return "Cleanup(" + username + "," + projectId + ")";
            case ZIP:
                return "Zip(" + projectId + ")";
        }
        return "?";
    }
}
