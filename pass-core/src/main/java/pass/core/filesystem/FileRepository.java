package pass.core.filesystem;

import java.nio.file.Path;
import java.nio.file.Paths;
import pass.core.common.Config;

public class FileRepository
{

    private static Path getRepositoryPath()
    {
        return Paths.get(Config.getInstance().getRepositoryPath());
    }

    public static Path getProjectDirectory(int projectId)
    {
        return getRepositoryPath()
                .resolve("projects")
                .resolve(String.valueOf(projectId));
    }

    public static Path getScriptsDirectory()
    {
        return getRepositoryPath()
                .resolve("scripts");
    }

    public static Path getSubmissionDirectory(int projectId, int submissionId)
    {
        return getRepositoryPath()
                .resolve("upload")
                .resolve("prj" + projectId)
                .resolve(String.valueOf(submissionId));
    }

    public static Path getArchiveDirectory()
    {
        return getRepositoryPath()
                .resolve("archive");
    }

    public static Path getUsersList()
    {
        return getRepositoryPath()
                .resolve("list.csv");
    }

    public static Path getUnderMaintenance()
    {
        return getRepositoryPath()
                .resolve("under_maintenance");
    }
}
