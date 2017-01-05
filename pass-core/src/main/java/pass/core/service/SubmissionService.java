package pass.core.service;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import pass.core.common.Config;
import pass.core.filesystem.SubmissionFiles;
import pass.core.hibernate.HibernateSession;
import pass.core.hibernate.ProjectsRepository;
import pass.core.hibernate.SubmissionsRepository;
import pass.core.model.Project;
import pass.core.model.Submission;
import pass.core.scheduling.Status;
import pass.core.scheduling.TaskManager;

public class SubmissionService
{

    private static final Logger LOGGER = Logger.getLogger(SubmissionService.class.getName());

    private final TaskManager taskManager;

    public SubmissionService(TaskManager taskManager)
    {
        this.taskManager = taskManager;
    }

    private void checkProject(Project project)
    {
        if (project == null || !project.isVisible()) {
            throw new ServiceException(ErrorCode.NOT_FOUND);
        }
        if (project.isDeadlinePassed()) {
            throw new ServiceException(ErrorCode.DEADLINE_PASSED);
        }
    }

    private void checkFiles(List<UploadedFile> files)
    {
        if (files.isEmpty()) {
            throw new ServiceException(ErrorCode.NO_FILE);
        }
        int maxCount = Config.getInstance().getMaxNumberOfFilesPerSubmission();
        if (files.size() > maxCount) {
            throw new ServiceException(ErrorCode.TOO_MANY_FILES);
        }
        long totalSize = 0;
        totalSize = files.stream()
                .map((uf) -> uf.getSize())
                .reduce(totalSize, (acc, size) -> acc + size);
        int maxSize = Config.getInstance().getMaxTotalFileSizePerSubmission();
        if (totalSize > maxSize) {
            throw new ServiceException(ErrorCode.FILES_TOO_LARGE);
        }
    }

    private void checkPreviousSubmissions(List<Submission> previousSubmissions)
    {
        for (Submission sub : previousSubmissions) {
            Status s = taskManager.getEvaluateSubmissionTaskStatus(sub.getId());
            if (s != null && s.getState() != Status.TaskState.FINISHED) {
                throw new ServiceException(ErrorCode.ANOTHER_EVALUATING);
            }
        }
    }

    private boolean shouldCleanup(List previousSubmissions)
    {
        /*
         * Using this formula, if keepSubmissionsPerUserProject == 3,
         * we would cleanup after the 6th submission is evaluated
         */
        int THR = 1 + Config.getInstance().getKeepSubmissionsPerUserProject();
        return previousSubmissions.size() > THR;
    }

    public int submit(int projectId,
                      String username,
                      List<UploadedFile> files,
                      List<CompileOption> compileOptions)
            throws ServiceException
    {
        try (HibernateSession hs = new HibernateSession()) {
            ProjectsRepository projectRepo = new ProjectsRepository(hs);
            Project project = projectRepo.findById(projectId);
            checkProject(project);
            checkFiles(files);
            SubmissionsRepository subRepo = new SubmissionsRepository(hs);
            List<Submission> prvSubs = subRepo.listSubmissionsFor(username,
                                                                  projectId);
            checkPreviousSubmissions(prvSubs);
            // Create submission object
            Submission submission = subRepo.addSubmission(projectId,
                                                          username,
                                                          compileOptions);
            if (submission == null) {
                // Error occured while saving submission in db
                LOGGER.log(Level.SEVERE,
                           "Could not save submission in db for "
                           + "username={0}, fileCount={1}, projectId={2}",
                           new Object[] {username, files.size(), projectId});
                throw new ServiceException(ErrorCode.UNKNOWN_ERROR);
            }
            // Save files
            SubmissionFiles sfiles = new SubmissionFiles(submission);
            try {
                for (UploadedFile p : files) {
                    sfiles.saveFile(p.getSubmittedFileName(),
                                    p.getInputStream());
                }
            }
            catch (IOException ex) {
                LOGGER.log(Level.SEVERE,
                           "Failed to save file for submission "
                           + submission.getId(),
                           ex);
                throw new ServiceException(ErrorCode.UNKNOWN_ERROR);
            }
            subRepo.updateSubmission(submission);
            int submissionId = submission.getId();
            taskManager.evaluateSubmission(submissionId);
            if (shouldCleanup(prvSubs)) {
                taskManager.cleanupSubmissions(username, projectId);
            }
            return submissionId;
        }
    }

    public void reTest(int submissionId) throws ServiceException
    {
        try (HibernateSession hs = new HibernateSession()) {
            SubmissionsRepository repo = new SubmissionsRepository(hs);
            Submission submission = repo.findById(submissionId);
            if (submission == null) {
                throw new ServiceException(ErrorCode.NOT_FOUND);
            }
        }
        taskManager.evaluateSubmission(submissionId);
    }
}
