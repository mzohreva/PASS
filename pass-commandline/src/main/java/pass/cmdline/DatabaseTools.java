package pass.cmdline;

import java.io.Console;
import java.io.IOException;
import java.util.EnumSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;
import pass.core.common.Config;
import pass.core.model.Project;
import pass.core.model.Submission;
import pass.core.model.User;
import pass.core.model.VerificationCode;
import pass.core.service.AccountService;
import pass.core.service.ServiceException;

public class DatabaseTools
{

    private static final Logger LOGGER = Logger.getLogger(DatabaseTools.class.getName());

    private void configure()
    {
        try {
            Config.getInstance().loadConfig();
        }
        catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void createAdminUser()
    {
        configure();
        System.out.println("Creating admin user");
        System.out.println("----------------------------------------");
        Console console = System.console();
        String password1 = new String(console.readPassword("Enter password: "));
        String password2 = new String(console.readPassword("Retype: "));
        if (!password1.equals(password2)) {
            System.out.println("Error: passwords do not match!");
            return;
        }
        String firstName = console.readLine("First Name: ");
        String lastName = console.readLine("Last Name: ");
        String studentId = console.readLine("Student Id: ");
        String email = console.readLine("Email: ");
        System.out.println("----------------------------------------");
        try {
            AccountService accountService = new AccountService(null);
            accountService.registerAdmin(firstName,
                                         lastName,
                                         password1,
                                         studentId,
                                         email);
            System.out.println("admin user created successfully.");
        }
        catch (ServiceException err) {
            System.out.println("Error: " + err.getErrorCode().getDescription());
        }
    }

    public void generateDatabaseScript(String outputFile)
    {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .applySetting("hibernate.dialect",
                              "org.hibernate.dialect.MySQLDialect")
                .build();

        Metadata metadata = new MetadataSources(registry)
                .addAnnotatedClass(User.class)
                .addAnnotatedClass(Project.class)
                .addAnnotatedClass(Submission.class)
                .addAnnotatedClass(VerificationCode.class)
                .buildMetadata();

        SchemaExport tool = new SchemaExport();
        tool.setOutputFile(outputFile);
        tool.setFormat(true);
        tool.setDelimiter(";");
        tool.createOnly(EnumSet.of(TargetType.SCRIPT), metadata);
    }
}
