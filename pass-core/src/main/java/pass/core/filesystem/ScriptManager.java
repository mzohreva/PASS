package pass.core.filesystem;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import pass.core.common.Util;

public class ScriptManager
{

    private static final Logger LOGGER = Logger.getLogger(ScriptManager.class.getName());

    public static final String DEFAULT_COMPILE_SCRIPT = "compile.sh";
    public static final String DEFAULT_TEST_SCRIPT = "test.sh";

    public static List<String> getAllCompileScripts()
    {
        try {
            DirectoryStream.Filter<Path> filter = (Path file) -> {
                return Files.isExecutable(file)
                       && file.getFileName().toString().startsWith("compile");
            };
            Path dir = FileRepository.getScriptsDirectory();
            return Util.listDirectoryContents(dir, filter);
        }
        catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return new ArrayList<>();
        }
    }

    public static List<String> getAllTestScripts()
    {
        try {
            DirectoryStream.Filter<Path> filter = (Path file) -> {
                return Files.isExecutable(file)
                       && file.getFileName().toString().startsWith("test");
            };
            Path dir = FileRepository.getScriptsDirectory();
            return Util.listDirectoryContents(dir, filter);
        }
        catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return new ArrayList<>();
        }
    }

    public static Path getScript(String scriptName)
    {
        return FileRepository.getScriptsDirectory().resolve(scriptName);
    }
}
