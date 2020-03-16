package server;

import java.io.*;
import java.net.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import auth.Authenticator;

import sec.RSA;
import server.ClientConnection;

public class WebServer{

	//port setup
	private static int PORT=8081;
	private static ServerSocket server;

	public static void main(String[] args) throws IOException{
		
		//Pravimo rsa za kljuceve koje prosledjujemo koriscnicima
		RSA decryptor = new RSA();

		server = null;

		server = new ServerSocket(PORT);
		System.out.println("Server pokrenut na portu " + PORT);
		
		Map<String, ClientConnection> clients = new ConcurrentHashMap<>();
		Authenticator auth = new Authenticator("users.txt");

		new Thread(new Console(clients)).start();

		while(server != null) {
			Socket client = server.accept();
			WebServerThread initConnection = new WebServerThread(client, clients, decryptor, auth);
			new Thread(initConnection).start();
		}
	}
}
