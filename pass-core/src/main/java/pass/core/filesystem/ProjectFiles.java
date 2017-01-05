package pass.core.filesystem;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import pass.core.common.Util;

public class ProjectFiles
{

    private final static Logger LOGGER = Logger.getLogger(ProjectFiles.class.getName());

    private final Path projectDir;
    private final Path scriptsDir;
    private final Path attachmentsDir;
    private final Path gradingTestsDir;
    private final Path auxDir;

    public ProjectFiles(int projectId)
    {
        this.projectDir = FileRepository.getProjectDirectory(projectId);
        this.scriptsDir = FileRepository.getScriptsDirectory();
        this.attachmentsDir = projectDir.resolve("attachments");
        this.gradingTestsDir = projectDir.resolve("grading_tests");
        this.auxDir = projectDir.resolve("auxiliary");
        try {
            Files.createDirectories(projectDir);
            Files.createDirectories(attachmentsDir);
            Files.createDirectories(auxDir);
        }
        catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void saveAuxiliaryFile(String fileName, InputStream input)
            throws IOException
    {
        Path file = auxDir.resolve(fileName);
        Files.copy(input, file);
    }

    public boolean removeAuxiliaryFile(String fileName) throws IOException
    {
        Path file = auxDir.resolve(fileName);
        boolean successfullyDeleted = Files.deleteIfExists(file);
        return successfullyDeleted;
    }

    public List<String> listAuxiliaryFileNames()
    {
        if (Files.exists(auxDir)) {
            try {
                return Util.listDirectoryContents(auxDir);
            }
            catch (IOException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }
        return new ArrayList<>();
    }

    public void saveAttachment(String fileName, InputStream input)
            throws IOException
    {
        Path file = attachmentsDir.resolve(fileName);
        Files.copy(input, file);
    }

    public boolean removeAttachment(String fileName) throws IOException
    {
        Path file = attachmentsDir.resolve(fileName);
        boolean successfullyDeleted = Files.deleteIfExists(file);
        return successfullyDeleted;
    }

    public void saveGradingTests(String fileName, InputStream input)
            throws IOException
    {
        Path zipFile = projectDir.resolve(fileName);
        Files.copy(input, zipFile);
        Util.extractZipFile(zipFile.toString(),
                            gradingTestsDir.toString());
        Files.delete(zipFile);
    }

    public void replaceGradingTests(String fileName, InputStream input)
            throws IOException
    {
        Util.removeDirectoryContents(gradingTestsDir);
        saveGradingTests(fileName, input);
    }

    public List<String> listAttachmentNames()
    {
        if (Files.exists(attachmentsDir)) {
            try {
                return Util.listDirectoryContents(attachmentsDir);
            }
            catch (IOException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }
        return new ArrayList<>();
    }

    public List<String> listGradingTestNames()
    {
        if (Files.exists(gradingTestsDir)) {
            try {
                return Util.listDirectoryContents(gradingTestsDir);
            }
            catch (IOException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }
        return new ArrayList<>();
    }

    public boolean attachmentExists(String fileName)
    {
        Path file = attachmentsDir.resolve(fileName);
        return Files.exists(file);
    }

    public Path getAttachment(String fileName)
    {
        Path file = attachmentsDir.resolve(fileName);
        return file;
    }

    public boolean deleteFiles()
    {
        if (!Files.exists(projectDir)) {
            return true;
        }
        try {
            SimpleFileVisitor<Path> visitor = new SimpleFileVisitor<Path>()
            {
                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException
                {
                    LOGGER.log(Level.INFO, "Deleting {0}", dir.toString());
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
                {
                    LOGGER.log(Level.INFO, "Deleting {0}", file.toString());
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

            };
            Files.walkFileTree(projectDir, visitor);
            return true;
        }
        catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public void setCompileScript(String compileScript) throws IOException
    {
        Path link = projectDir.resolve("compile.sh");
        Path target = scriptsDir.resolve(compileScript);
        Files.deleteIfExists(link);
        Files.createSymbolicLink(link, projectDir.relativize(target));
    }

    public void setTestScript(String testScript) throws IOException
    {
        Path link = projectDir.resolve("test.sh");
        Path target = scriptsDir.resolve(testScript);
        Files.deleteIfExists(link);
        Files.createSymbolicLink(link, projectDir.relativize(target));
    }

    public String getCompileScript() throws IOException
    {
        Path link = projectDir.resolve("compile.sh");
        if (Files.exists(link) && Files.isSymbolicLink(link)) {
            Path target = Files.readSymbolicLink(link);
            return target.getFileName().toString();
        }
        else {
            return ScriptManager.DEFAULT_COMPILE_SCRIPT;
        }
    }

    public Path getCompileScriptFullPath() throws IOException
    {
        return ScriptManager.getScript(getCompileScript());
    }

    public String getTestScript() throws IOException
    {
        Path link = projectDir.resolve("test.sh");
        if (Files.exists(link) && Files.isSymbolicLink(link)) {
            Path target = Files.readSymbolicLink(link);
            return target.getFileName().toString();
        }
        else {
            return ScriptManager.DEFAULT_TEST_SCRIPT;
        }
    }

    public Path getTestScriptFullPath() throws IOException
    {
        return ScriptManager.getScript(getTestScript());
    }

    public String getGradingTestsPath()
    {
        return gradingTestsDir.toString();
    }

    public String getAuxiliaryFilesPath()
    {
        return auxDir.toString();
    }
}
