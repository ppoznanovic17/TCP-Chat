package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import auth.Authenticator;
import auth.LoginState;
import sec.RSA;

/*
 * Klasa za hendlovanje konekcije
 * Ovde cemo vrsiti:
 * 	-chat komande
 * 	-slanje informacija o korisnicima
 * 	-slanje informacija o konekciji
 * 	-slanje privatnih poruka
 * 	-zatvaranje veze
 */

public class ClientConnection {
	//paterni
	private static final Pattern PRIVATE_MESSAGE_NICKNAME_PATTERN = Pattern.compile("(\\w+) (.*)");
	private static final Pattern NICKNAME_RULES = Pattern.compile("\\w+");

	protected final String username;
	private final PrintWriter socketWriter;
	private final Map<String, ClientConnection> clients;
	private final RSA decryptor;
	private final RSA encryptor;
	private final BufferedReader socketReader;
	private final Authenticator authenticator;
	protected final Socket clientSocket;
	
	/*
	 *Kontruktor
	 *@param clientSocket - soket 
	 *@param clients - povezani korisnici
	 *@param decryptor - rsa dekripcija odakle cemo vadii generisanje kljuceve
	 *@param authenticator - rad sa klijentima..logovanje registracija itd..
	 */
	ClientConnection(Socket clientSocket, Map<String, ClientConnection> clients, RSA decryptor, Authenticator authenticator) throws IOException {
		this.socketReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		this.socketWriter = new PrintWriter(clientSocket.getOutputStream());
		this.clients = clients;
		this.decryptor = decryptor;
		this.clientSocket = clientSocket;

		sendEncryptionKeys();
		this.authenticator = authenticator;
		encryptor = makeEncryptor();
		this.username = authenticateUser();
	}
	
	/*
	 * Glavna funkcija za interakciju sa sobom
	 */
	public void listen() {
		
		//notifikacija
		broadcast("SERVER: " + username + " se pridruzio sobi.");
		System.out.println("SERVER: " + username + " se pridruzio sobi.");
		try {
			while (true) {
				
				//hvatamo data
				String clientData = socketReader.readLine();
				
				//greska
				if (clientData == null) {
					disconnect();
					break;
				}
				
				//hvatanje poruke i dekripcija
				String message = decryptor.decryptString(clientData);
				
				
				//komanda za listing svih povezanih korisnika
				if (message.equals(":clients")) sendClientList();
				
				
				
				//komanda za prikaz poruke celoj sobi
				else broadcast(username + ": " + message);
				
			}
		} catch (IOException e) {
			disconnect();
		}
	}
	
	/*
	 * Funckiaj za slanje alerta neakrivnim korisnicima
	 */
	private void sendAlert(String message) {
		String tokens[] = message.split(" ");
		for (ClientConnection cC : clients.values()) 
			if (cC.username.equals(tokens[1])) cC.sendEncrypeted("beep:");
	}

	/*
	 * Funkcija za slanje enkriptovanih poruka
	 * @param message - poruka za salnje
	 */
	protected void sendEncrypeted(String message) {
		send(encryptor.encryptString(message));
	}
	
	/*
	 * Funkcija za slanje kljuceva za enkripciju
	 */
	private void sendEncryptionKeys() {
		send(decryptor.getE().toString());
		send(decryptor.getN().toString());
	}
	
	/*
	 * Univerzalna funkcija za slanje podataka preko soketa
	 */
	private void send(String data) {
		try {
			socketWriter.println(data);
			socketWriter.flush();
		} catch (Exception e) {
			disconnect();
		}
	}
	
	/*
	 * Funkcija za listovanje korisnika u sobi
	 */
	private void sendClientList() {
		String message = "SERVER: \n";
		for (String nickname : clients.keySet()) {
			message = message + "\t" + nickname + "\n";
		}
		message += "\n";
		sendEncrypeted(message);
	}
	
	/*
	 *Funkcija za slanje privatnih poruka
	 *@param privateMessageMatcher - promenljiva iz koje parsiramo poruku i zeljenog korisnika 
	 */
	private void sendPrivateMessage(Matcher privateMessageMatcher) {
		String to = privateMessageMatcher.group(1);
		String messageText = privateMessageMatcher.group(2);
		if (clients.keySet().contains(to)) {
			clients.get(to).sendEncrypeted("PRIVATE " + username + ": " + messageText);
		} else {
			sendEncrypeted("SERVER: WRONG NICKNAME");
		}
	}
	
	/*
	 * Funkcija za hendlovanje prijavljivanje korisnika
	 */
	private String authenticateUser() throws IOException {
		while (true) {
			String encryptedUsername = socketReader.readLine();
			String decryptedUsername = decryptor.decryptString(encryptedUsername);
			String encryptedPassword = socketReader.readLine();
			String decryptedPassword = decryptor.decryptString(encryptedPassword);

			if (NICKNAME_RULES.matcher(decryptedUsername).matches()) {
				if (!clients.containsKey(decryptedUsername)) {
					if (authenticator.IsRegistered(decryptedUsername)) {
						if (authenticator.authenticate(decryptedUsername, decryptedPassword)) {
							sendEncrypeted(LoginState.LOGIN_ACCPETED.name());
							return decryptedUsername;
						} 
						else sendEncrypeted(LoginState.PASSWORD_MISMATCH.name());
					} 
					else {
						authenticator.registerUser(decryptedUsername, decryptedPassword);
						sendEncrypeted(LoginState.REGISTERED.name());
						return decryptedUsername;
					}
				} 
				else sendEncrypeted(LoginState.IN_USE.name());
			} 
			else sendEncrypeted(LoginState.INVALID_USERNAME.name());
		}
	}
	
	/*
	 * Podesavanje enkripcije uz pomoc prosledjenih parametara
	 */
	private RSA makeEncryptor() throws IOException {
		BigInteger e = new BigInteger(socketReader.readLine());
		BigInteger n = new BigInteger(socketReader.readLine());
		return new RSA(e, n);
	}
	
	/*
	 * Funkcija za gasenje konekcije
	 */
	private void disconnect() {
		clients.remove(username);
		broadcast(username + " nas je napustio.");
	}
	
	/*
	 * Funkcija za slanje poruka svim klijentima u sobi
	 */
	private void broadcast(String message) {
		for (ClientConnection cC : clients.values()) 
			if (cC != this) cC.sendEncrypeted(message);
	}
}
