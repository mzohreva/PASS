package pass.web.view;

import java.io.IOException;
import java.net.URLEncoder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import pass.web.common.WebConfig;

@WebServlet ("/admin/search.do")
public class Search extends HttpServlet
{

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        String query = request.getParameter("query");
        String queryUrlEncoded = URLEncoder.encode(query, "UTF-8");
        try {
            int submissionId = Integer.parseInt(query);
            String vsa = WebConfig.getInstance().view("view_submission_admin");
            response.sendRedirect(vsa + "?id=" + queryUrlEncoded);
        }
        catch (NumberFormatException err) {
            String vu = WebConfig.getInstance().view("view_user");
            response.sendRedirect(vu + "?user=" + queryUrlEncoded);
        }
    }
}
