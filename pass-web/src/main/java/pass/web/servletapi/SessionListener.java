package pass.web.servletapi;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

@WebListener
public class SessionListener implements HttpSessionListener
{

    @Override
    public void sessionCreated(HttpSessionEvent se)
    {
        HttpSession session = se.getSession();
        SessionManager.getInstance().addSession(session);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se)
    {
        HttpSession session = se.getSession();
        SessionManager.getInstance().removeSession(session);
    }
}
