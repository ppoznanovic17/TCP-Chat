package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;
import java.util.Scanner;

import auth.Authenticator;
import sec.RSA;

/*
 * Klasa za instanciranje client konekcije, nema potrebe da koristimo thread ovde
 */
public class WebServerThread implements Runnable{

	private final Socket client;
	private final Map<String, ClientConnection> clients;
	private final RSA decryptor;
	private final Authenticator auth;

	public WebServerThread(Socket socket, Map<String, ClientConnection> clients, RSA decryptor, Authenticator auth) {
		this.client = socket;
		this.clients = clients;
		this.decryptor = decryptor;
		this.auth = auth;
	}
	/*
	 * Napravimo novu konekciju i prosledimo joj parametre
	 * Dodamo je u mapu konekcija
	 * Pokrenemo 
	 */
	@Override
	public void run(){
		try {
			ClientConnection connection = new ClientConnection(client, clients, decryptor, auth);
			clients.put(connection.username, connection);
			connection.listen();
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
