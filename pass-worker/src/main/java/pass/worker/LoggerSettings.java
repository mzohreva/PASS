package pass.worker;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import pass.core.common.UnixStyleLogFormatter;

public class LoggerSettings
{

    private static final String GENERAL_LOG_PATTERN = "%t/pass_worker%g.log";
    private static final int LOG_FILE_SIZE_LIMIT = 10 * 1024 * 1024;
    private static final int LOG_FILE_ROTATION_COUNT = 10;

    static public void Setup() throws IOException
    {
        LogManager.getLogManager().reset();
        FileHandler generalHandler = new FileHandler(GENERAL_LOG_PATTERN,
                                                     LOG_FILE_SIZE_LIMIT,
                                                     LOG_FILE_ROTATION_COUNT);
        generalHandler.setFormatter(new UnixStyleLogFormatter(false, true));
        generalHandler.setLevel(Level.ALL);
        {
            Logger logger = Logger.getLogger("pass");
            logger.addHandler(generalHandler);
            logger.setLevel(Level.CONFIG);
        }
        {
            Logger logger = Logger.getLogger("org.hibernate");
            logger.addHandler(generalHandler);
            logger.setLevel(Level.WARNING);
        }
    }
}
