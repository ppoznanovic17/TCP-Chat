package client;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import auth.LoginState;
import sec.RSA;

/*
 * Klijent klasa
 */
public class Client {

	//output za slanje
	private static PrintWriter socket_out;

	//enkripcija za poruke
	private static RSA decryptor;
	private static RSA encrypt;
	
	//login view
	private static Login log;

	public static void main(String[] args) {

		try {

			//setup soketa
			Socket client = new Socket("localhost", 8081);

			//saljemo kljuceve servere
			socket_out = new PrintWriter(client.getOutputStream());
			decryptor = new RSA();
			socket_out.println(decryptor.getE());
			socket_out.println(decryptor.getN());
			socket_out.flush();

			//setup thread-a za poruke
			ClientInputModul cim = new ClientInputModul(client, decryptor);
			BigInteger e = new BigInteger(cim.readInput());
			BigInteger n = new BigInteger(cim.readInput());
			encrypt = new RSA(e, n);

			//init gui
			log = new Login(client, encrypt, decryptor, cim);

		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
