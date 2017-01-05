package pass.core.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Util
{

    private final static Logger LOGGER = Logger.getLogger(Util.class.getName());

    public static void extractZipFile(String zipFilePath, String targetDir)
    {
        try {
            String[] cmd = new String[] {
                "unzip", "-j", zipFilePath, "-d", targetDir
            };
            Process unzipProc = Runtime.getRuntime().exec(cmd);
            unzipProc.waitFor();
        }
        catch (IOException | InterruptedException ex) {
            LOGGER.log(Level.SEVERE, "Failed to extract zip file", ex);
        }
    }

    /*
     * Removal is not recursive, only files and empty
     * sub-directories in dir will be removed.
     * IOException is thrown in case of a non-empty sub-directory
     */
    public static void removeDirectoryContents(Path dir) throws IOException
    {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path p : stream) {
                Files.deleteIfExists(p);
            }
        }
    }

    public static List<String> listDirectoryContents(Path dir)
            throws IOException
    {
        List<String> fileNames = new ArrayList<>();
        try (DirectoryStream<Path> s = Files.newDirectoryStream(dir)) {
            for (Path p : s) {
                fileNames.add(p.getFileName().toString());
            }
        }
        Collections.sort(fileNames);
        return fileNames;
    }

    public static List<String> listDirectoryContents(
            Path dir,
            DirectoryStream.Filter<Path> filter) throws IOException
    {
        List<String> fileNames = new ArrayList<>();
        try (DirectoryStream<Path> s = Files.newDirectoryStream(dir, filter)) {
            for (Path p : s) {
                fileNames.add(p.getFileName().toString());
            }
        }
        Collections.sort(fileNames);
        return fileNames;
    }

    private static void readStream(InputStream stream, List<String> output)
            throws IOException
    {
        InputStreamReader isr = new InputStreamReader(stream);
        try (BufferedReader reader = new BufferedReader(isr)) {
            while (reader.ready()) {
                String line = reader.readLine();
                if (output != null) {
                    output.add(line);
                }
            }
        }
    }

    private static String readStream(InputStream stream) throws IOException
    {
        StringBuilder sb = new StringBuilder();
        InputStreamReader isr = new InputStreamReader(stream);
        try (BufferedReader reader = new BufferedReader(isr)) {
            while (reader.ready()) {
                String line = reader.readLine();
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }

    public static void runCommand(String[] cmdArray,
                                  List<String> stdout,
                                  List<String> stderr)
            throws IOException, InterruptedException
    {
        Process proc = Runtime.getRuntime().exec(cmdArray);
        proc.waitFor();
        InputStream procOutput = proc.getInputStream();
        InputStream procError = proc.getErrorStream();
        readStream(procOutput, stdout);
        readStream(procError, stderr);
    }

    public static String runCommand(String cmd)
            throws IOException, InterruptedException
    {
        Process proc = Runtime.getRuntime().exec(cmd);
        proc.waitFor();
        InputStream procOutput = proc.getInputStream();
        return readStream(procOutput);
    }

    public static String runCommand(String[] cmdArray)
            throws IOException, InterruptedException
    {
        Process proc = Runtime.getRuntime().exec(cmdArray);
        proc.waitFor();
        InputStream procOutput = proc.getInputStream();
        return readStream(procOutput);
    }

    public static String generateRandomPassword(int length)
    {
        final String CHARSET = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ+-";
        SecureRandom rand = new SecureRandom();
        char[] pass = new char[length];
        for (int i = 0; i < length; i++) {
            int index = rand.nextInt(CHARSET.length());
            pass[i] = CHARSET.charAt(index);
        }
        return new String(pass);
    }

    public static Path tempDirectory() throws IOException
    {
        Path path = Paths.get("/tmp/pass");
        Files.createDirectories(path);
        return path;
    }

    public static String makeScratchDirectory(int id) throws IOException
    {
        UUID uniqeSuffix = UUID.randomUUID();
        String name = id + "_" + uniqeSuffix.toString();
        Path path = tempDirectory().resolve(name);
        if (Files.exists(path)) {
            throw new FileAlreadyExistsException(path.toString());
        }
        Files.createDirectories(path);
        return path.toString();
    }

    public enum DateDifferencePrecision
    {
        SECONDS,
        MINUTES,
        HOURS,
    }

    public static String dateDifferenceHumanReadable(
            Date start, Date end,
            DateDifferencePrecision precision,
            boolean shortUnits)
    {
        if (end.before(start)) {
            throw new IllegalArgumentException("end cannot be before start!");
        }
        long diff = (end.getTime() - start.getTime()) / 1000;   // seconds
        long seconds = (diff >= 60 ? diff % 60 : diff);
        diff = diff / 60;
        long minutes = (diff >= 60 ? diff % 60 : diff);
        diff = diff / 60;
        long hours = (diff >= 24 ? diff % 24 : diff);
        long days = diff / 24;

        if (shortUnits) {
            switch (precision) {
                case SECONDS:
                    return String.format("%03d d %02d h %02d m %02d s",
                                         days, hours, minutes, seconds);
                case MINUTES:
                    return String.format("%03d d %02d h %02d m",
                                         days, hours, minutes);
                case HOURS:
                    return String.format("%03d d %02d h",
                                         days, hours);
                default:
                    throw new IllegalArgumentException("Invalid precision");
            }
        }
        else {
            switch (precision) {
                case SECONDS:
                    return String.format("%d day%s, %d hour%s, %d minute%s, %d second%s",
                                         days, days > 1 ? "s" : "",
                                         hours, hours > 1 ? "s" : "",
                                         minutes, minutes > 1 ? "s" : "",
                                         seconds, seconds > 1 ? "s" : "");
                case MINUTES:
                    return String.format("%d day%s, %d hour%s, %d minute%s",
                                         days, days > 1 ? "s" : "",
                                         hours, hours > 1 ? "s" : "",
                                         minutes, minutes > 1 ? "s" : "");
                case HOURS:
                    return String.format("%d day%s, %d hour%s",
                                         days, days > 1 ? "s" : "",
                                         hours, hours > 1 ? "s" : "");
                default:
                    throw new IllegalArgumentException("Invalid precision");
            }
        }
    }

    public static String fileSizeHumanReadable(long size)
    {
        if (size < 1024) {
            return size + " bytes";
        }
        else if (size < 1024 * 1024) {
            return String.format("%.0f KB", size * 1.0 / 1024);
        }
        else if (size < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", size * 1.0 / 1024 / 1024);
        }
        else {
            return String.format("%.2f GB", size * 1.0 / 1024 / 1024 / 1024);
        }
    }

    public static void readXmlPropertiesFile(String resourceName,
                                             Map<String, String> output)
            throws IOException
    {
        LOGGER.log(Level.INFO, "Reading {0}", resourceName);
        Properties prop = new Properties();
        InputStream stream = Util.class
                .getClassLoader()
                .getResourceAsStream(resourceName);

        prop.loadFromXML(stream);
        prop.stringPropertyNames()
                .forEach((name) -> {
                    String value = prop.getProperty(name);
                    output.put(name, value);
                    LOGGER.log(Level.INFO, "  {0} = {1}",
                               new Object[] {name, value});
                });
    }
}
