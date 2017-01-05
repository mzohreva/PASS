package pass.web.common;

import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateNotFoundException;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import pass.core.common.Config;
import pass.core.email.Mailer;
import pass.core.email.SecureMailer;
import pass.core.scheduling.TaskManager;
import pass.core.scheduling.distributed.DistributedTaskManager;
import pass.core.scheduling.local.LocalTaskManager;

/*
 * TODO: consider an IoC solution in the future to replace this
 */
public class Container
{

    private static final Logger LOGGER = Logger.getLogger(Container.class.getName());

    private static final Container instance = new Container();

    public static Container getInstance()
    {
        return instance;
    }

    public Mailer mailer;
    public TaskManager taskManager;
    public Configuration freemarkerConfig;

    private Container()
    {
    }

    public void initialize(ServletContext context)
    {
        try {
            //taskManager = new LocalTaskManager(WebConfig.getInstance().getConcurrentTasks());
            taskManager = new DistributedTaskManager();

            freemarkerConfig = new Configuration(Configuration.VERSION_2_3_25);
            freemarkerConfig.setDirectoryForTemplateLoading(
                    new File(context.getRealPath("/WEB-INF/templates")));
            freemarkerConfig.setDefaultEncoding("UTF-8");
            freemarkerConfig.setTemplateExceptionHandler(
                    TemplateExceptionHandler.RETHROW_HANDLER);
            freemarkerConfig.setLogTemplateExceptions(false);

            WebConfig webConfig = WebConfig.getInstance();
            Config config = Config.getInstance();
            mailer = new SecureMailer(webConfig.getEmailServer(),
                                      webConfig.getEmailUser(),
                                      webConfig.getEmailPassword(),
                                      webConfig.getServerEmailAddress(),
                                      config.getSiteTitle());
        }
        catch (IOException | NumberFormatException ex) {
            LOGGER.log(Level.SEVERE, "Error initializing: {0}", ex.toString());
        }
    }

    public void destroy(ServletContext context)
    {
        taskManager.shutdown();
    }

    public Mailer getMailer()
    {
        return mailer;
    }

    public TaskManager getTaskManager()
    {
        return taskManager;
    }

    public Template getTemplate(String path)
            throws TemplateNotFoundException,
                   MalformedTemplateNameException,
                   IOException
    {
        return freemarkerConfig.getTemplate(path);
    }
}
