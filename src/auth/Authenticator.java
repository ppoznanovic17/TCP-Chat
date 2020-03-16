package auth;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import auth.PasswordHash;

/*
 * Klasa za interakciju sa user profilima
 */
public class Authenticator {
	private Pattern login = Pattern.compile("(\\w+):(\\d+):(.+)");
	private Map<String, PasswordHash> users;

	private String file;
	private BufferedReader fileReader;
	private PrintWriter fileWriter;
	
	//konstruktor
	public Authenticator(String file) {
		
		this.file = file;
		try {
			fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			this.users = new HashMap<>();
			
			while (true) {
				String line = fileReader.readLine();
				
				if (line != null && !line.equals("")) {
					Matcher lineMatcher = login.matcher(line);
					lineMatcher.find();
					users.put(lineMatcher.group(1), new PasswordHash(lineMatcher.group(2), lineMatcher.group(3)));
				} 
				
				else break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Proveravam da li username ima nalog
	 */
	public boolean IsRegistered(String username) {
		return users.get(username) != null;
	}
	
	/*
	 * Funkcija za autentifikaciju. Uzimamo salt i sifru, hashujemo i poredimo sa heshom koji imamo
	 */
	public boolean authenticate(String descryptedUsername, String descryptedPassword) {
		String hashPassword = PasswordHash.hash(users.get(descryptedUsername).getSalt() + descryptedPassword);
		return hashPassword.equals(users.get(descryptedUsername).getHash());
	}
	
	/*
	 * Registracija korisnika
	 */
	public void registerUser(String korisnik, String pass) {
		
        try {
        	
        	//otovri fajl
            fileWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
            
            //hashuj sifru
            PasswordHash passHash = new PasswordHash(pass);
           
            //zapis
            fileWriter.println(korisnik + ":" + passHash.getSalt() + ":" + passHash.getHash());
            fileWriter.flush();
            
            //dodavanje u mapu
            users.put(korisnik, passHash);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
