package pass.web.view;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import pass.core.common.Tools;
import pass.core.service.ServerStatusService;
import pass.web.common.WebConfig;
import pass.web.common.WebUtil;

@WebServlet ("/admin/server_status.do")
public class ServerStatus extends HttpServlet
{

    private final static Logger LOGGER = Logger.getLogger(ServerStatus.class.getName());

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException
    {
        ServerStatusService sss = new ServerStatusService();
        Map<String, Object> data = new HashMap<>();
        data.put("underMaintenance", sss.isUnderMaintenance());
        data.put("upTime", Tools.escapeHtml(sss.upTime()));
        data.put("diskUsage", Tools.escapeHtml(sss.diskUsage()));
        WebUtil.renderTemplate(request, response, "private/admin/server_status.ftl", data);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException
    {
        String action = request.getParameter("action");
        if ("toggleUnderMaintenace".equals(action)) {
            ServerStatusService sss = new ServerStatusService();
            sss.toggleUnderMaintenance();
        }
        response.sendRedirect(WebConfig.getInstance().view("server_status"));
    }
}
