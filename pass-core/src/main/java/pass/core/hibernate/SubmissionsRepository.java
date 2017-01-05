package pass.core.hibernate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import pass.core.model.Project;
import pass.core.model.Submission;
import pass.core.model.User;
import pass.core.service.CompileOption;

public class SubmissionsRepository
{

    private static final Logger LOGGER = Logger.getLogger(SubmissionsRepository.class.getName());

    private final Session session;

    public SubmissionsRepository(HibernateSession hs)
    {
        session = hs.getSession();
    }

    public Submission addSubmission(int projectId,
                                    String username,
                                    List<CompileOption> compileOptions)
    {
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            User user = session.get(User.class, username);
            Project project = session.get(Project.class, projectId);
            if (user == null || project == null) {
                tx.rollback();
                return null;
            }
            Submission submission = new Submission(user, project, new Date());
            submission.setCompileOptions(compileOptions);
            submission.setCompileResults(null, false);
            submission.setTestResult(null);
            session.save(submission);
            tx.commit();
            return submission;
        }
        catch (Exception error) {
            if (tx != null) {
                tx.rollback();
            }
            LOGGER.log(Level.SEVERE, null, error);
            return null;
        }
    }

    public Submission findById(int submissionId)
    {
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Query<Submission> q = session.createQuery(
                    "from Submission s where s.id=:submissionId",
                    Submission.class);
            q.setParameter("submissionId", submissionId);
            Submission result = q.uniqueResult();
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

    public boolean updateSubmission(Submission submission)
    {
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.update(submission);
            tx.commit();
            return true;
        }
        catch (Exception error) {
            if (tx != null) {
                tx.rollback();
            }
            LOGGER.log(Level.SEVERE,
                       "Failed to update submission " + submission.getId(),
                       error);
            return false;
        }
    }

    public List<Submission> listSubmissionsFor(String username)
    {
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Query<Submission> q = session.createQuery(
                    "from Submission s where"
                    + " s.user.username=:username"
                    + " order by s.project.id, "
                    + "          s.submissionDate desc",
                    Submission.class);
            q.setParameter("username", username);
            List<Submission> result = q.list();
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

    public List<Submission> listSubmissionsFor(String username, int projectId)
    {
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Query<Submission> q = session.createQuery(
                    "from Submission s where"
                    + " s.user.username=:username"
                    + " and s.project.id=:projectId"
                    + " order by s.submissionDate desc",
                    Submission.class);
            q.setParameter("username", username);
            q.setParameter("projectId", projectId);
            List<Submission> result = q.list();
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

    public List<Submission> listOldSubmissionsFor(String username,
                                                  int projectId,
                                                  int keeping)
    {
        if (keeping < 1) {
            throw new IllegalArgumentException("keeping < 1 ?!");
        }
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Query<Submission> q = session.createQuery(
                    "from Submission s where"
                    + " s.user.username=:username"
                    + " and s.project.id=:projectId"
                    + " order by s.submissionDate desc",
                    Submission.class);
            q.setParameter("username", username);
            q.setParameter("projectId", projectId);
            List<Submission> result = q.list();
            tx.commit();
            List<Submission> oldSubmissions = new ArrayList<>();
            for (int i = keeping; i < result.size(); i++) {
                Submission s = result.get(i);
                oldSubmissions.add(s);
            }
            return oldSubmissions;
        }
        catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            LOGGER.log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public boolean deleteSubmission(Submission submission)
    {
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.delete(submission);
            tx.commit();
            return true;
        }
        catch (Exception error) {
            if (tx != null) {
                tx.rollback();
            }
            LOGGER.log(Level.SEVERE,
                       "Failed to delete submission " + submission.getId(),
                       error);
            return false;
        }
    }
}
