package pass.core.filesystem;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import pass.core.common.Util;
import pass.core.model.Submission;

public class SubmissionFiles
{

    private static final Logger LOGGER = Logger.getLogger(SubmissionFiles.class.getName());

    private final Path subDir;

    public SubmissionFiles(int submissionId, int projectId)
    {
        subDir = FileRepository.getSubmissionDirectory(projectId, submissionId);
    }

    public SubmissionFiles(Submission sub)
    {
        this(sub.getId(), sub.getProject().getId());
    }

    public void saveFile(String fileName, InputStream input) throws IOException
    {
        Files.createDirectories(subDir);
        Path targetPath = subDir.resolve(fileName);
        Files.copy(input, targetPath);
    }

    public List<String> listFiles()
    {
        try {
            return Util.listDirectoryContents(subDir);
        }
        catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return new ArrayList<>();
    }

    public boolean removeAllFiles()
    {
        try {
            Util.removeDirectoryContents(subDir);
            Files.delete(subDir);
            return true;
        }
        catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public String getSubmissionPath()
    {
        return subDir.toString();
    }
}
