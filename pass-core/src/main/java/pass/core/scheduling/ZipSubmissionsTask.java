package pass.core.scheduling;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import pass.core.common.Util;
import pass.core.filesystem.FileRepository;
import pass.core.filesystem.SubmissionFiles;
import pass.core.hibernate.HibernateSession;
import pass.core.hibernate.ProjectsRepository;
import pass.core.model.Project;
import pass.core.model.Submission;

/*
 * Creates a zip archive containing latest submissions
 * of all users for the specified project
 */
public class ZipSubmissionsTask extends BaseTask
{

    private static final Logger LOGGER = Logger.getLogger(ZipSubmissionsTask.class.getName());

    private final int projectId;

    public ZipSubmissionsTask(UUID taskId,
                              TaskSpecification taskSpec,
                              int projectId,
                              StatusUpdater statusUpdater)
    {
        super(taskId, taskSpec, statusUpdater);
        this.projectId = projectId;
        // This is not a good idea if there are
        // no other task-executing threads:
        this.threadPriority = Thread.MIN_PRIORITY;
    }

    private void copyOneSubmission(Path zipRoot, Submission s)
            throws IOException
    {
        SubmissionFiles sfiles = new SubmissionFiles(s);
        Path diskDir = Paths.get(sfiles.getSubmissionPath());
        Path zipDir = zipRoot.resolve(String.valueOf(s.getId()));
        Files.createDirectories(zipDir);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(diskDir)) {
            for (Path diskFile : stream) {
                String fileName = diskFile.getFileName().toString();
                Path zipFile = zipDir.resolve(fileName);
                Files.copy(diskFile, zipFile);
            }
        }
    }

    private void copySubmissions(FileSystem zipfs,
                                 Set<Submission> submissions)
            throws IOException
    {
        Path zipRoot = zipfs.getPath("/submissions-p" + projectId);
        Files.createDirectories(zipRoot);
        int i = 0;
        final int N = submissions.size();
        for (Submission s : submissions) {
            i++;
            String msg = "Copying submission " + s.getId()
                         + " (" + i + "/" + N + ")";
            updateStatus(Status.TaskState.RUNNING, msg);
            LOGGER.info(msg);
            copyOneSubmission(zipRoot, s);
        }
    }

    @Override
    void runTask()
    {
        try (HibernateSession hs = new HibernateSession()) {
            ProjectsRepository projectRepo = new ProjectsRepository(hs);
            Project project = projectRepo.findById(projectId);
            if (project == null) {
                LOGGER.log(Level.SEVERE,
                           "Could not find project {0}",
                           String.valueOf(projectId));
                return;
            }
            Set<Submission> submissions = project.getLastSubmissionsOfUsers();
            final int N = submissions.size();
            updateStatus(Status.TaskState.RUNNING, N + " submissions to zip");
            try {
                Path temp = Util.tempDirectory()
                        .resolve(taskId.toString() + ".zip");
                LOGGER.log(Level.INFO, "Temporary file: {0}", temp.toString());
                URI uri = new URI("jar", temp.toUri().toString(), null);
                Map<String, String> env = new HashMap<>();
                env.put("create", "true");
                try (FileSystem zipfs = FileSystems.newFileSystem(uri, env)) {
                    copySubmissions(zipfs, submissions);
                }
                DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
                String suffix = sdf.format(new Date());
                String name = "submissions-p" + projectId + "-" + suffix + ".zip";
                Path artifactPath = FileRepository
                        .getArchiveDirectory()
                        .resolve(name);
                String msg = "Moving produced artifact to archive directory";
                updateStatus(Status.TaskState.RUNNING, msg);
                LOGGER.info(msg);
                Files.move(temp, artifactPath);
                LOGGER.log(Level.INFO, "Archive created: {0}", name);
            }
            catch (IOException | URISyntaxException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }
    }
}
