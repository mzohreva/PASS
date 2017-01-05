package pass.core.hibernate;

import java.util.HashMap;
import java.util.Map;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import pass.core.common.Config;
import pass.core.model.Project;
import pass.core.model.Submission;
import pass.core.model.User;
import pass.core.model.VerificationCode;

public class HibernateUtil
{

    private static final SessionFactory sessionFactory;

    static {
        final Map<String, String> settings = new HashMap<>();
        settings.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        settings.put("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");
        settings.put("hibernate.connection.url", Config.getInstance().getDatabaseUrl());
        settings.put("hibernate.connection.username", Config.getInstance().getDatabaseUsername());
        settings.put("hibernate.connection.password", Config.getInstance().getDatabasePassword());
        settings.put("hibernate.current_session_context_class", "thread");
        settings.put("hibernate.connection.provider_class", "org.hibernate.connection.C3P0ConnectionProvider");
        settings.put("hibernate.c3p0.min_size", "2");
        settings.put("hibernate.c3p0.max_size", "100");
        settings.put("hibernate.c3p0.acquire_increment", "1");
        settings.put("hibernate.c3p0.idle_test_period", "600");
        settings.put("hibernate.c3p0.timeout", "1800");
        settings.put("hibernate.c3p0.max_statements", "0");

        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .applySettings(settings)
                .build();

        Metadata metadata = new MetadataSources(registry)
                .addAnnotatedClass(User.class)
                .addAnnotatedClass(Project.class)
                .addAnnotatedClass(Submission.class)
                .addAnnotatedClass(VerificationCode.class)
                .buildMetadata();

        sessionFactory = metadata.buildSessionFactory();
    }

    public static SessionFactory getSessionFactory()
    {
        return sessionFactory;
    }
}
