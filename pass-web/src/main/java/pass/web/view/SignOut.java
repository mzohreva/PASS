package pass.web.view;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import pass.web.common.WebConfig;
import pass.web.servletapi.SessionStore;

@WebServlet ("/signout.do")
public class SignOut extends HttpServlet
{

    protected void processRequest(HttpServletRequest request,
                                  HttpServletResponse response)
            throws ServletException, IOException
    {
        // Remove the authentication information from session storage
        SessionStore.removeAuthenticatedUser(request.getSession());
        response.sendRedirect(WebConfig.getInstance().view("signin"));
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException
    {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException
    {
        processRequest(request, response);
    }
}
