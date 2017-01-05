package pass.core.hibernate;

import org.hibernate.Session;

public class HibernateSession implements AutoCloseable
{

    private final Session session;

    public HibernateSession()
    {
        session = HibernateUtil.getSessionFactory().openSession();
    }

    public Session getSession()
    {
        return session;
    }

    @Override
    public void close()
    {
        session.close();
    }
}
