package pass.core.hibernate;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import pass.core.common.HashedPassword;
import pass.core.model.User;

public class UsersRepository
{

    private static final Logger LOGGER = Logger.getLogger(UsersRepository.class.getName());

    private final Session session;

    public UsersRepository(HibernateSession hs)
    {
        session = hs.getSession();
    }

    public User addUser(String username,
                        String password,
                        String studentId,
                        String firstname,
                        String lastname,
                        String email,
                        boolean verified)
    {
        Transaction tx = null;
        try {
            HashedPassword hp = HashedPassword.generate(password);
            tx = session.beginTransaction();
            User user = new User(username, hp.getHash(), hp.getSalt(),
                                 studentId, firstname, lastname, email,
                                 verified);
            session.save(user);
            tx.commit();
            return user;
        }
        catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            LOGGER.log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public boolean deleteUser(String username)
    {
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Query<User> q = session.createQuery(
                    "from User u where u.username=:uname",
                    User.class);
            q.setParameter("uname", username);
            User user = q.uniqueResult();
            if (user != null) {
                session.delete(user);
            }
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

    public User findByUsername(String username)
    {
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Query<User> q = session.createQuery(
                    "from User u where u.username=:uname",
                    User.class);
            q.setParameter("uname", username);
            User user = q.uniqueResult();
            tx.commit();
            return user;
        }
        catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            LOGGER.log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public List<User> list()
    {
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Query<User> q = session.createQuery("from User u", User.class);
            List<User> list = q.list();
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

    public boolean updateUser(User user)
    {
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.update(user);
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
}
