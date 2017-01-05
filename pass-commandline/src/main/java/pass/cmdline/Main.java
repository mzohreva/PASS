package pass.cmdline;

import java.io.File;
import java.util.logging.LogManager;

public class Main
{

    static void Usage()
    {
        String jar = Main.class.getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getPath();
        String prog = new File(jar).getName();
        System.out.println("Usage: " + prog + " COMMAND [ARGS]\n");
        System.out.println("COMMAND:\n");
        System.out.println("    key:    generate master key");
        System.out.println("    enc:    encrypt a password with master key");
        System.out.println("    admin:  register admin user in database");
        System.out.println("    db:");
        System.out.println("            generate database creation script");
        System.out.println("            args: <output_file>");
        System.out.println("    cfg:");
        System.out.println("            generate all configuration files");
        System.out.println("            args: <template_dir> <output_dir>");
        System.out.println("    cfg1:");
        System.out.println("            generate a single configuration file");
        System.out.println("            args: <template> <output>");
        System.out.println();
    }

    public static void main(String[] args)
    {
        if (args.length < 1) {
            Usage();
            System.exit(-1);
        }
        LogManager.getLogManager().reset();
        String command = args[0];
        switch (command) {
            case "key": {
                PasswordTools passwordTools = new PasswordTools();
                passwordTools.generateKey();
                break;
            }
            case "enc": {
                PasswordTools passwordTools = new PasswordTools();
                passwordTools.encryptPassword();
                break;
            }
            case "admin": {
                DatabaseTools dbTools = new DatabaseTools();
                dbTools.createAdminUser();
                break;
            }
            case "db": {
                if (args.length != 2) {
                    Usage();
                    System.exit(-1);
                }
                DatabaseTools dbTools = new DatabaseTools();
                dbTools.generateDatabaseScript(args[1]);
                break;
            }
            case "cfg": {
                if (args.length != 3) {
                    Usage();
                    System.exit(-1);
                }
                ConfigTools configTools = new ConfigTools();
                configTools.generateConfigFiles(args[1], args[2]);
                break;
            }
            case "cfg1": {
                if (args.length != 3) {
                    Usage();
                    System.exit(-1);
                }
                ConfigTools configTools = new ConfigTools();
                configTools.generateSingleConfigFile(args[1], args[2]);
                break;
            }
            default: {
                System.out.println("Unknown action " + command);
                System.exit(-1);
            }
        }
    }
}
