package pass.web.servletapi;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpSession;
import pass.core.service.AuthenticatedUser;

public class SessionManager
{

    private static final SessionManager instance = new SessionManager();

    public static SessionManager getInstance()
    {
        return instance;
    }

    private final Map<HttpSession, Boolean> activeSessions;

    private SessionManager()
    {
        this.activeSessions = new ConcurrentHashMap<>();
    }

    public void addSession(HttpSession session)
    {
        activeSessions.put(session, Boolean.TRUE);
    }

    public void removeSession(HttpSession session)
    {
        activeSessions.remove(session);
    }

    /*
     * Maps session ids to authenticated users
     */
    public Map<String, AuthenticatedUser> listActiveUsers()
    {
        Map<String, AuthenticatedUser> activeUsers = new HashMap<>();
        activeSessions.keySet().forEach((s) -> {
            try {
                AuthenticatedUser webUser = SessionStore.getAuthenticatedUser(s);
                if (webUser != null) {
                    activeUsers.put(s.getId(), webUser);
                }
            }
            catch (IllegalStateException ex) {
                // The session is already invalidated, so ignore it
            }
        });
        return activeUsers;
    }

    public void endSession(String sessionId)
    {
        activeSessions.keySet().forEach((s) -> {
            try {
                if (s.getId().equals(sessionId)) {
                    s.invalidate();
                }
            }
            catch (IllegalStateException ex) {
                // The session is already invalidated, so ignore it
            }
        });
    }
}
