package pass.web.common;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import pass.core.common.Config;
import pass.core.common.MasterKey;
import pass.core.common.Util;

public class WebConfig
{

    private static final Logger LOGGER = Logger.getLogger(WebConfig.class.getName());

    private static final WebConfig instance = new WebConfig();

    public static WebConfig getInstance()
    {
        return instance;
    }

    private String contextPath;
    private String serverEmailAddress;
    private String emailServer;
    private String emailUser;
    private String emailPassword;
    private int concurrentTasks;

    private WebConfig()
    {
    }

    public void initialize(String conextPath) throws IOException
    {
        Map<String, String> params = new HashMap<>();
        Util.readXmlPropertiesFile("web_settings.xml", params);
        try {
            this.contextPath = conextPath;
            serverEmailAddress = params.get("server_email_address");
            emailServer = params.get("email_server");
            emailUser = params.get("email_user");
            emailPassword = params.get("email_password");
            concurrentTasks = Integer.parseInt(params.get("concurrent_tasks"));
        }
        catch (NumberFormatException ex) {
            LOGGER.log(Level.SEVERE, "Error initializing: {0}", ex.toString());
        }
    }

    public String getContextPath()
    {
        return contextPath;
    }

    public String getServerEmailAddress()
    {
        return serverEmailAddress;
    }

    public String getEmailServer()
    {
        return emailServer;
    }

    public String getEmailUser()
    {
        return emailUser;
    }

    public String getEmailPassword()
    {
        return MasterKey.getInstance().decrypt(emailPassword);
    }

    public int getConcurrentTasks()
    {
        return concurrentTasks;
    }

    /*
     * Convenience method for FreeMarker templates to be able
     * to access Config values as needed
     */
    public String getSiteTitle()
    {
        return Config.getInstance().getSiteTitle();
    }

    public String getDefaultSubmissionInstructions()
    {
        return "- Do not use **space** in file names\n\n"
               + "- Only submit source code, no test cases, no executables\n\n"
               + "- Your program **should not open any files**\n\n"
               + "- Your code will be compiled on the server using `gcc` for C "
               + "  files and `g++` for C++ files\n\n"
               + "- Each source code file ( **.c**, **.cpp** or **.cc** ) "
               + "  is compiled separately and then all object files are "
               + "  linked together\n\n"
               + "- You can mix C and C++ source files, the server will "
               + "  compile and link them as explained above\n\n"
               + "- You can specify compiler options below\n\n";
    }

    private static final Map<String, String> viewPaths = new HashMap<>();

    static {
        viewPaths.put("edit_project", /*         */ "/admin/edit_project.do");
        viewPaths.put("manage_projects", /*      */ "/admin/manage_projects.do");
        viewPaths.put("manage_submissions", /*   */ "/admin/manage_submissions.do");
        viewPaths.put("manage_users", /*         */ "/admin/manage_users.do");
        viewPaths.put("manage_codes", /*         */ "/admin/manage_codes.do");
        viewPaths.put("manage_archives", /*      */ "/admin/manage_archives.do");
        viewPaths.put("new_project", /*          */ "/admin/new_project.do");
        viewPaths.put("retest", /*               */ "/admin/retest.do");
        viewPaths.put("server_status", /*        */ "/admin/server_status.do");
        viewPaths.put("tasks", /*                */ "/admin/tasks.do");
        viewPaths.put("view_submission_admin", /**/ "/admin/view_submission.do");
        viewPaths.put("view_user", /*            */ "/admin/view_user.do");
        viewPaths.put("search", /*               */ "/admin/search.do");

        viewPaths.put("download", /*             */ "/user/download.do");
        viewPaths.put("list_projects", /*        */ "/user/list_projects.do");
        viewPaths.put("list_submissions", /*     */ "/user/list_submissions.do");
        viewPaths.put("test", /*                 */ "/user/test.do");
        viewPaths.put("view_submission", /*      */ "/user/view_submission.do");

        viewPaths.put("recover_account", /*      */ "/recover_account.do");
        viewPaths.put("register", /*             */ "/register.do");
        viewPaths.put("reset_password", /*       */ "/reset_password.do");
        viewPaths.put("signin", /*               */ "/signin.do");
        viewPaths.put("signout", /*              */ "/signout.do");
        viewPaths.put("verify", /*               */ "/verify.do");
    }

    public String view(String name)
    {
        String path = viewPaths.get(name);
        return contextPath + (path == null ? "" : path);
    }
}
