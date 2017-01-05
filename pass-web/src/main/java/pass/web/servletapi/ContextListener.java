package pass.web.servletapi;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import pass.core.common.Config;
import pass.web.common.Container;
import pass.web.common.WebConfig;

@WebListener
public class ContextListener implements ServletContextListener
{

    private final static Logger LOGGER = Logger.getLogger(ContextListener.class.getName());

    private void configureLogging()
    {
        try {
            LoggerSettings.Setup();
        }
        catch (IOException ex) {
            System.err.println("Failed to setup logging settings" + ex.toString());
        }
    }

    private void configureConfig()
    {
        try {
            Config.getInstance().loadConfig();
        }
        catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Failed to load config", ex);
        }
    }

    private void configureWebConfig(String contextPath)
    {
        try {
            WebConfig.getInstance().initialize(contextPath);
        }
        catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Failed to initialize web config", ex);
        }
    }

    @Override
    public void contextInitialized(ServletContextEvent sce)
    {
        ServletContext context = sce.getServletContext();
        configureLogging();
        configureConfig();
        configureWebConfig(context.getContextPath());
        Container.getInstance().initialize(context);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce)
    {
        ServletContext context = sce.getServletContext();
        Container.getInstance().destroy(context);
        LogManager.getLogManager().reset();
    }
}
