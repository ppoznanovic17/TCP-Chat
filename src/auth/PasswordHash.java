package auth;

import java.util.Random;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;

/*
 * Pasword hash klasa
 */
public class PasswordHash {
    private final String salt;
    private final String hash;

    public PasswordHash(String salt, String hash) {
        this.hash = hash;
        this.salt = salt;
    }

    public PasswordHash(String password) {
        Random saltGenerator = new Random();
        this.salt = Long.toString(Math.abs(saltGenerator.nextLong()));
        this.hash = hash(salt + password);
    }

    public String getSalt() {
        return salt;
    }

    public String getHash() {
    	
    	return hash;
    }
    
    
    //hashovanje sifre
    public static String hash(String input) {
        return Hashing.sha256().hashString(input, Charsets.UTF_8).toString();
    }
}

