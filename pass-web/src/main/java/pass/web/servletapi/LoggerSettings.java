package pass.web.servletapi;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import pass.core.common.UnixStyleLogFormatter;
import pass.core.scheduling.distributed.StatusUpdateConsumer;

public class LoggerSettings
{

    private static final String GENERAL_LOG_PATTERN = "%t/pass%g.log";
    private static final String ACCESS_LOG_PATTERN = "%t/pass_access%g.log";
    private static final String JOBS_LOG_PATTERN = "%t/pass_jobs%g.log";
    private static final int LOG_FILE_SIZE_LIMIT = 10 * 1024 * 1024;
    private static final int LOG_FILE_ROTATION_COUNT = 10;

    private static void setupGeneralLogging() throws IOException
    {
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

    private static void setupAccessLogging() throws IOException
    {
        FileHandler accessHandler = new FileHandler(ACCESS_LOG_PATTERN,
                                                    LOG_FILE_SIZE_LIMIT,
                                                    LOG_FILE_ROTATION_COUNT);
        accessHandler.setFormatter(new UnixStyleLogFormatter(true, false));
        accessHandler.setLevel(Level.ALL);
        {
            Logger logger = Logger.getLogger(RequestLoggerFilter.class.getName());
            logger.addHandler(accessHandler);
            logger.setLevel(Level.ALL);
            logger.setUseParentHandlers(false);
        }
    }

    private static void setupJobsLogging() throws IOException
    {
        FileHandler jobsHandler = new FileHandler(JOBS_LOG_PATTERN,
                                                  LOG_FILE_SIZE_LIMIT,
                                                  LOG_FILE_ROTATION_COUNT);
        jobsHandler.setFormatter(new UnixStyleLogFormatter(true, false));
        jobsHandler.setLevel(Level.ALL);
        {
            Logger logger = Logger.getLogger(StatusUpdateConsumer.class.getName());
            logger.addHandler(jobsHandler);
            logger.setLevel(Level.ALL);
            logger.setUseParentHandlers(false);
        }
    }

    public static void Setup() throws IOException
    {
        LogManager.getLogManager().reset();
        setupGeneralLogging();
        setupAccessLogging();
        setupJobsLogging();
    }
}
