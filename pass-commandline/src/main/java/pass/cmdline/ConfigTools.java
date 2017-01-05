package pass.cmdline;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Console;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import pass.core.common.MasterKey;

public class ConfigTools
{

    private String key;

    public ConfigTools()
    {
        this.key = null;
    }

    public String getKey()
    {
        if (key == null) {
            char[] input = System.console()
                    .readPassword("** Enter master key to encrypt passwords: ");
            key = new String(input);
        }
        return key;
    }

    private void processTemplate(Path template,
                                 Path output,
                                 Map<String, String> dict)
    {
        Charset charset = StandardCharsets.UTF_8;
        try (BufferedReader reader = Files.newBufferedReader(template, charset);
             BufferedWriter writer = Files.newBufferedWriter(output, charset)) {
            String line;
            while ((line = reader.readLine()) != null) {
                for (Map.Entry<String, String> kv : dict.entrySet()) {
                    String pattern = "${" + kv.getKey() + "}";
                    line = Pattern
                            .compile(pattern, Pattern.LITERAL)
                            .matcher(line)
                            .replaceAll(kv.getValue());
                }
                writer.write(line);
                writer.newLine();
            }
        }
        catch (IOException ex) {
            System.err.println("Error: " + ex.toString());
        }
    }

    private List<String> findTemplateVariables(Path template)
    {
        List<String> vars = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(template,
                                                    StandardCharsets.UTF_8);
            for (String line : lines) {
                Matcher matcher = Pattern
                        .compile("\\$\\{([a-zA-Z0-9_\\.]+)\\}")
                        .matcher(line);
                while (matcher.find()) {
                    String v = matcher.group(1);
                    if (!vars.contains(v)) {
                        vars.add(v);
                    }
                }
            }
        }
        catch (IOException ex) {
            System.err.println("Error: " + ex.toString());
        }
        return vars;
    }

    private void generateConfigFile(Path template, Path output)
    {
        System.out.println("----------------------------------------");
        System.out.println(template.toString() + " => " + output.toString());
        System.out.println("----------------------------------------");
        List<String> vars = findTemplateVariables(template);
        Map<String, String> dict = new HashMap<>();
        Console console = System.console();
        for (String var : vars) {
            String value = console.readLine("Enter the value for " + var + ": ");
            if (var.contains("password")) {
                value = MasterKey.encrypt(getKey(), value);
            }
            dict.put(var, value);
        }
        processTemplate(template, output, dict);
    }

    private boolean checkDir(Path dir)
    {
        if (!Files.exists(dir)) {
            System.err.println("Error: " + dir.toString() + " does not exist");
            return false;
        }
        if (!Files.isDirectory(dir)) {
            System.err.println("Error: " + dir.toString() + " is not a directory");
            return false;
        }
        return true;
    }

    public void generateConfigFiles(String templateFolder, String outputFolder)
    {
        Path templateDir = Paths.get(templateFolder);
        Path outputDir = Paths.get(outputFolder);
        if (!checkDir(templateDir) || !checkDir(outputDir)) {
            return;
        }
        List<Path> templates = new ArrayList<>();
        try (DirectoryStream<Path> stream
                = Files.newDirectoryStream(templateDir, "*.in")) {
            for (Path p : stream) {
                templates.add(p);
            }
        }
        catch (IOException ex) {
            System.err.println("Error: " + ex.toString());
        }
        Collections
                .sort(templates,
                      (p1, p2) -> p1.getFileName().compareTo(p2.getFileName()));
        for (Path template : templates) {
            String outputName = template.getFileName().toString();
            outputName = outputName.substring(0, outputName.lastIndexOf(".in"));
            Path output = outputDir.resolve(outputName);
            generateConfigFile(template, output);
        }
    }

    private boolean checkFile(Path file)
    {
        if (!Files.exists(file)) {
            System.err.println("Error: " + file.toString() + " does not exist");
            return false;
        }
        if (!Files.isRegularFile(file)) {
            System.err.println("Error: " + file.toString() + " is not a file");
            return false;
        }
        return true;
    }

    public void generateSingleConfigFile(String templateFile, String outputFile)
    {
        Path template = Paths.get(templateFile);
        Path output = Paths.get(outputFile);
        if (!checkFile(template)) {
            return;
        }
        generateConfigFile(template, output);
    }
}
