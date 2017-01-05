package pass.core.common;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Config
{

    private static final Logger LOGGER = Logger.getLogger(Config.class.getName());

    private static final Config instance = new Config();

    public static Config getInstance()
    {
        return instance;
    }

    private String masterKeyPath;
    private String repositoryPath;
    private String siteTitle;
    private String serverUrl;
    private String brokerHost;
    private String brokerUser;
    private String brokerPassword;
    private String databaseUrl;
    private String databaseUsername;
    private String databasePassword;
    private int keepSubmissionsPerUserProject;
    private int maxTotalFileSizePerSubmission;
    private int maxNumberOfFilesPerSubmission;

    private Config()
    {
    }

    public void loadConfig() throws IOException
    {
        Map<String, String> map = new HashMap<>();
        Util.readXmlPropertiesFile("basic_settings.xml", map);
        Util.readXmlPropertiesFile("broker_settings.xml", map);
        Util.readXmlPropertiesFile("database_settings.xml", map);
        Util.readXmlPropertiesFile("filesystem_settings.xml", map);
        try {
            masterKeyPath = map.get("master_key_path");
            repositoryPath = map.get("repository_path");
            siteTitle = map.get("site_title");
            serverUrl = map.get("server_url");
            brokerHost = map.get("broker_host");
            brokerUser = map.get("broker_user");
            brokerPassword = map.get("broker_password");
            databaseUrl = map.get("database_url");
            databaseUsername = map.get("database_username");
            databasePassword = map.get("database_password");
            keepSubmissionsPerUserProject = Integer.parseInt(
                    map.get("keep_submissions_per_user_project"));
            maxTotalFileSizePerSubmission = Integer.parseInt(
                    map.get("max_total_file_size_per_submission"));
            maxNumberOfFilesPerSubmission = Integer.parseInt(
                    map.get("max_number_of_files_per_submission"));
        }
        catch (NumberFormatException ex) {
            LOGGER.log(Level.SEVERE, "Error initializing: {0}", ex.toString());
        }
    }

    public String getMasterKeyPath()
    {
        return masterKeyPath;
    }

    public String getRepositoryPath()
    {
        return repositoryPath;
    }

    public String getSiteTitle()
    {
        return siteTitle;
    }

    public String getServerUrl()
    {
        return serverUrl;
    }

    public String getBrokerHost()
    {
        return brokerHost;
    }

    public String getBrokerUser()
    {
        return brokerUser;
    }

    public String getBrokerPassword()
    {
        return MasterKey.getInstance().decrypt(brokerPassword);
    }

    public String getDatabaseUrl()
    {
        return databaseUrl;
    }

    public String getDatabaseUsername()
    {
        return databaseUsername;
    }

    public String getDatabasePassword()
    {
        return MasterKey.getInstance().decrypt(databasePassword);
    }

    public int getKeepSubmissionsPerUserProject()
    {
        return keepSubmissionsPerUserProject;
    }

    public int getMaxTotalFileSizePerSubmission()
    {
        return maxTotalFileSizePerSubmission;
    }

    public int getMaxNumberOfFilesPerSubmission()
    {
        return maxNumberOfFilesPerSubmission;
    }
}
