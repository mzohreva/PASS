package pass.core.hibernate;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import pass.core.model.Project;
import pass.core.model.UserProject;

public class ProjectsRepository
{

    private static final Logger LOGGER = Logger.getLogger(ProjectsRepository.class.getName());

    private final Session session;

    public ProjectsRepository(HibernateSession hs)
    {
        session = hs.getSession();
    }

    public Project findById(int id)
    {
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Query<Project> q = session.createQuery(
                    "from Project p where p.id=:projectId",
                    Project.class);
            q.setParameter("projectId", id);
            Project result = q.uniqueResult();
            tx.commit();
            return result;
        }
        catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            LOGGER.log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public List<Project> list(boolean onlyVisisble)
    {
        StringBuilder cmd = new StringBuilder();
        cmd.append("from Project p");
        if (onlyVisisble) {
            cmd.append(" where p.visible is true");
        }
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Query<Project> q = session.createQuery(cmd.toString(),
                                                   Project.class);
            List<Project> list = q.list();
            tx.commit();
            return list;
        }
        catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            LOGGER.log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public List<UserProject> listUserProjects(boolean onlyVisible,
                                              String username)
    {
        StringBuilder cmd = new StringBuilder();
        cmd.append("select new ").append(UserProject.class.getName());
        cmd.append("( u.username, p.id, p.title, p.assignDate, ");
        cmd.append("  p.dueDate, p.visible, ");
        cmd.append("  (select count(*) from Submission as s where ");
        cmd.append("   s.project=p.id and s.user=u.username)");
        cmd.append(") from Project as p, User as u ");
        cmd.append("where u.username=:username");
        if (onlyVisible) {
            cmd.append(" and p.visible is true");
        }
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Query<UserProject> q = session.createQuery(cmd.toString(),
                                                       UserProject.class);
            q.setParameter("username", username);
            List<UserProject> list = q.list();
            tx.commit();
            return list;
        }
        catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            LOGGER.log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public UserProject getUserProject(int projectId, String username)
    {
        StringBuilder cmd = new StringBuilder();
        cmd.append("select new ").append(UserProject.class.getName());
        cmd.append("( u.username, p.id, p.title, p.assignDate, ");
        cmd.append("  p.dueDate, p.visible, ");
        cmd.append("  (select count(*) from Submission as s where ");
        cmd.append("   s.project=p.id and s.user=u.username)");
        cmd.append(") from Project as p, User as u ");
        cmd.append("where u.username=:username and p.id=:projectId");
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Query<UserProject> q = session.createQuery(cmd.toString(),
                                                       UserProject.class);
            q.setParameter("username", username);
            q.setParameter("projectId", projectId);
            UserProject obj = q.uniqueResult();
            tx.commit();
            return obj;
        }
        catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            LOGGER.log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public int addProject(String title,
                          Date assigned,
                          Date due,
                          int gracePeriodHours,
                          boolean visible,
                          String submissionInstructions)
    {
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Project project = new Project(title, assigned, due,
                                          gracePeriodHours, visible,
                                          submissionInstructions,
                                          new HashSet<>());
            session.save(project);
            tx.commit();
            return project.getId();
        }
        catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            LOGGER.log(Level.SEVERE, null, ex);
            return -1;
        }
    }

    public boolean deleteProject(Project project)
    {
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.delete(project);
            tx.commit();
            return true;
        }
        catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            return false;
        }
    }

    public void updateProject(Project project)
    {
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.update(project);
            tx.commit();
        }
        catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
}
