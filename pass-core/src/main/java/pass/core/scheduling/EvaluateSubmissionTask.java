package pass.core.scheduling;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import pass.core.common.LineVisitor;
import pass.core.common.Util;
import pass.core.filesystem.ProjectFiles;
import pass.core.filesystem.SubmissionFiles;
import pass.core.hibernate.HibernateSession;
import pass.core.hibernate.SubmissionsRepository;
import pass.core.model.Submission;
import pass.core.service.CompileOption;

public class EvaluateSubmissionTask extends BaseTask
{

    private static final Logger LOGGER = Logger.getLogger(EvaluateSubmissionTask.class.getName());

    private final int submissionId;

    public EvaluateSubmissionTask(UUID taskId,
                                  TaskSpecification taskSpec,
                                  int submissionId,
                                  StatusUpdater statusUpdater)
    {
        super(taskId, taskSpec, statusUpdater);
        this.submissionId = submissionId;
    }

    private static String prepareOptions(List<CompileOption> compileOptions,
                                         CompileOption.Category category)
    {
        StringBuilder sbOptions = new StringBuilder();
        compileOptions.stream()
                .filter((opt) -> (opt.getCategory() == category))
                .forEachOrdered((opt) -> {
                    sbOptions.append(opt.getArgs()).append(" ");
                });
        return sbOptions.toString();
    }

    private static void truncateCompileMessage(StringBuffer sbMsg)
    {
        final int MAX_SIZE = 2048;
        if (sbMsg.length() > MAX_SIZE) {
            sbMsg.delete(MAX_SIZE - 4, sbMsg.length());
            sbMsg.append("...");
        }
    }

    private boolean compileSubmission(Submission submission,
                                      String compileScript,
                                      String submissionPath,
                                      String executable,
                                      String auxiliaryFilesPath,
                                      String scratchPath,
                                      SubmissionsRepository repo)
    {
        try {
            List<CompileOption> options = submission.getCompileOptionsList();
            String[] cmdCompile = new String[] {
                compileScript,
                submissionPath,
                executable,
                scratchPath,
                auxiliaryFilesPath,
                prepareOptions(options, CompileOption.Category.COMMON),
                prepareOptions(options, CompileOption.Category.C_DIALECTS),
                prepareOptions(options, CompileOption.Category.CPP_DIALECTS)
            };
            StringBuffer sbCompileMsg = new StringBuffer();
            AtomicBoolean compileSuccessful = new AtomicBoolean(false);
            LineVisitor stdoutVisitor = (String line) -> {
                sbCompileMsg.append(line.replaceAll(submissionPath, ""));
                sbCompileMsg.append("\n");
                if (line.contains("COMPILE_SUCCESS")) {
                    compileSuccessful.set(true);
                }
            };
            LineVisitor stderrVisitor = (String line) -> {
                sbCompileMsg.append(line.replaceAll(submissionPath, ""));
                sbCompileMsg.append("\n");
            };
            Util.runCommand(cmdCompile, stdoutVisitor, stderrVisitor);
            truncateCompileMessage(sbCompileMsg);
            submission.setCompileResults(sbCompileMsg.toString(),
                                         compileSuccessful.get());
            repo.updateSubmission(submission);
            return compileSuccessful.get();
        }
        catch (IOException | InterruptedException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return false;
        }
    }

    private boolean testSubmission(Submission submission,
                                   String gradingTestsPath,
                                   String testScript,
                                   String executable,
                                   String scratchPath,
                                   SubmissionsRepository repo)
    {
        try {
            String[] cmdTest = new String[] {
                testScript,
                gradingTestsPath,
                executable,
                scratchPath
            };
            StringBuffer sbTestResult = new StringBuffer();
            LineVisitor stdoutVisitor = (String line) -> {
                sbTestResult.append(line).append("<br/>\n");
            };
            Util.runCommand(cmdTest, stdoutVisitor, null);
            submission.setTestResult(sbTestResult.toString());
            repo.updateSubmission(submission);
            return true;
        }
        catch (IOException | InterruptedException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    protected void runTask()
    {
        try (HibernateSession hs = new HibernateSession()) {
            SubmissionsRepository repo = new SubmissionsRepository(hs);
            Submission submission = repo.findById(submissionId);
            if (submission == null) {
                LOGGER.log(Level.SEVERE,
                           "Could not find submission {0}",
                           String.valueOf(submissionId));
                return;
            }
            SubmissionFiles sfiles = new SubmissionFiles(submission);
            int projectId = submission.getProject().getId();
            ProjectFiles pfiles = new ProjectFiles(projectId);
            String compileScript = pfiles.getCompileScriptFullPath().toString();
            String testScript = pfiles.getTestScriptFullPath().toString();
            String gradingTestsPath = pfiles.getGradingTestsPath();
            String auxiliaryFilesPath = pfiles.getAuxiliaryFilesPath();
            String submissionPath = sfiles.getSubmissionPath();
            String scratchDir = Util.makeScratchDirectory(submissionId);
            String executable = scratchDir + "/a.out";
            submission.setCompileResults(null, false);
            submission.setTestResult(null);
            repo.updateSubmission(submission);
            updateStatus(Status.TaskState.RUNNING, "Compiling");
            boolean compileOK = compileSubmission(submission,
                                                  compileScript,
                                                  submissionPath,
                                                  executable,
                                                  auxiliaryFilesPath,
                                                  scratchDir,
                                                  repo);
            if (compileOK) {
                updateStatus(Status.TaskState.RUNNING, "Testing");
                testSubmission(submission,
                               gradingTestsPath,
                               testScript,
                               executable,
                               scratchDir,
                               repo);
            }
            Files.deleteIfExists(Paths.get(executable));
            // NOTE: the scripts should clean up the scratch directory
            Files.deleteIfExists(Paths.get(scratchDir));
        }
        catch (IOException err) {
            LOGGER.log(Level.SEVERE,
                       "Exception while evaluating submission " + submissionId,
                       err);
        }
    }
}
