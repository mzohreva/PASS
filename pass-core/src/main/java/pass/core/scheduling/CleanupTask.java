package pass.core.scheduling;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import pass.core.common.Config;
import pass.core.filesystem.SubmissionFiles;
import pass.core.hibernate.HibernateSession;
import pass.core.hibernate.SubmissionsRepository;
import pass.core.model.Submission;

public class CleanupTask extends BaseTask
{

    private static final Logger LOGGER = Logger.getLogger(CleanupTask.class.getName());

    private final String username;
    private final int projectId;
    private final int keeping;

    public CleanupTask(UUID taskId,
                       TaskSpecification taskSpec,
                       String username,
                       int projectId,
                       StatusUpdater statusUpdater)
    {
        super(taskId, taskSpec, statusUpdater);
        this.username = username;
        this.projectId = projectId;
        this.keeping = Config.getInstance().getKeepSubmissionsPerUserProject();
    }

    private void logResults(int subId, boolean filesDeleted, boolean subDeleted)
    {

        if (filesDeleted && subDeleted) {
            LOGGER.log(Level.INFO,
                       "Submission {0} deleted successfully",
                       String.valueOf(subId));
        }
        else if (filesDeleted) {
            LOGGER.log(Level.SEVERE,
                       "Failed to delete submission {0} after its files were removed",
                       String.valueOf(subId));
        }
        else {
            LOGGER.log(Level.SEVERE,
                       "Failed to remove submission {0}''s files",
                       String.valueOf(subId));

        }
    }

    @Override
    protected void runTask()
    {
        try (HibernateSession hs = new HibernateSession()) {
            SubmissionsRepository repo = new SubmissionsRepository(hs);
            List<Submission> toRemove = repo.listOldSubmissionsFor(username,
                                                                   projectId,
                                                                   keeping);
            final int total = toRemove.size();
            updateStatus(Status.TaskState.RUNNING,
                         total + " submissions to remove");
            // Sort them to start from the oldest
            Collections.sort(toRemove,
                             (s1, s2) -> s1.getId().compareTo(s2.getId()));
            int i = 0;
            for (Submission sub : toRemove) {
                i++;
                updateStatus(Status.TaskState.RUNNING,
                             "removing submission " + sub.getId()
                             + " (" + i + "/" + total + ")");
                SubmissionFiles subFiles = new SubmissionFiles(sub);
                boolean filesDeleted = subFiles.removeAllFiles();
                boolean subDeleted = false;
                if (filesDeleted) {
                    subDeleted = repo.deleteSubmission(sub);
                }
                logResults(sub.getId(), filesDeleted, subDeleted);
            }
        }
    }
}
