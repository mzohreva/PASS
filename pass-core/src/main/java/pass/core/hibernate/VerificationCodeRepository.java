package pass.core.hibernate;

import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import pass.core.model.VerificationCode;

public class VerificationCodeRepository
{

    private static final Logger LOGGER = Logger.getLogger(VerificationCodeRepository.class.getName());

    private final Session session;

    public VerificationCodeRepository(HibernateSession hs)
    {
        session = hs.getSession();
    }

    public boolean store(VerificationCode vc)
    {
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.save(vc);
            tx.commit();
            return true;
        }
        catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            LOGGER.log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public VerificationCode find(UUID code)
    {
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            VerificationCode vc = session.get(VerificationCode.class, code);
            tx.commit();
            return vc;
        }
        catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            LOGGER.log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public boolean remove(VerificationCode vc)
    {
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.delete(vc);
            tx.commit();
            return true;
        }
        catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            LOGGER.log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public List<VerificationCode> listAll()
    {
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Query<VerificationCode> q = session.createQuery(
                    "from VerificationCode vc",
                    VerificationCode.class);
            List<VerificationCode> list = q.list();
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

    public List<VerificationCode> listBy(String username,
                                         VerificationCode.Reason reason)
    {
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Query<VerificationCode> q;
            q = session.createQuery("from VerificationCode vc"
                                    + " where vc.user.username=:username"
                                    + " and vc.reason=:reason",
                                    VerificationCode.class);
            q.setParameter("username", username);
            q.setParameter("reason", reason);
            List<VerificationCode> list = q.list();
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
}
