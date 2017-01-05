package pass.cmdline;

import java.io.Console;
import pass.core.common.MasterKey;
import pass.core.common.Util;

public class PasswordTools
{

    void generateKey()
    {
        String newKey = Util.generateRandomPassword(22);
        System.out.println("----------------------------------------");
        System.out.println("New key: " + newKey);
        System.out.println("----------------------------------------");
        System.out.println("Keep this key in a safe place.");
        System.out.println("Use this key to encrypt passwords in config files");
    }

    public void encryptPassword()
    {
        System.out.println("Encrypt a password with master key");
        System.out.println("----------------------------------------");
        Console console = System.console();
        String key = new String(console.readPassword("Enter master key: "));
        String password = new String(console.readPassword("Enter password: "));
        System.out.println("----------------------------------------");
        String encrypted = MasterKey.encrypt(key, password);
        System.out.println("Encrypted password: " + encrypted);
    }
}
