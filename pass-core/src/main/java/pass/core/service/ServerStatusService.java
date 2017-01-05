package pass.core.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import pass.core.common.Util;
import pass.core.filesystem.FileRepository;

public class ServerStatusService
{

    private static final Logger LOGGER = Logger.getLogger(ServerStatusService.class.getName());

    public ServerStatusService()
    {
    }

    public boolean isUnderMaintenance()
    {
        Path file = FileRepository.getUnderMaintenance();
        return Files.exists(file);
    }

    public void toggleUnderMaintenance()
    {
        Path file = FileRepository.getUnderMaintenance();
        try {
            if (Files.exists(file)) {
                Files.delete(file);
            }
            else {
                Files.createFile(file);
            }
        }
        catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public String upTime()
    {
        try {
            return Util.runCommand("uptime");
        }
        catch (IOException | InterruptedException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return "?";
        }
    }

    public String diskUsage()
    {
        try {
            return Util.runCommand("df -h");
        }
        catch (IOException | InterruptedException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return "?";
        }
    }
}
