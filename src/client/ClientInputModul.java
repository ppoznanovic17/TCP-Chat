package client;

import java.awt.Color;
import java.awt.Window;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import sec.RSA;

/*
 * Klasa za prikaz poruka
 */
public class ClientInputModul implements Runnable{
	
	private BufferedReader reader;
	private RSA decryptor;
	private JTextPane tf;

	public ClientInputModul(Socket input, RSA decryptor) throws IOException {
		this.decryptor = decryptor;
		this.reader = new BufferedReader(new InputStreamReader(input.getInputStream()));
	}
	
	@Override
	public void run() {
		try {
			while (true) {
				String line = reader.readLine();
				if (line != null) {
					if (tf.getText().toLowerCase().startsWith("server:")) appendToPane(tf, decryptor.decryptString(line) + "\n", Color.BLUE);
					
					if (tf.getText().toLowerCase().equals("beep:")) java.awt.Toolkit.getDefaultToolkit().beep();
					
					else appendToPane(tf, decryptor.decryptString(line) + "\n", Color.GREEN);
					
				}
				 else {
					appendToPane(tf, "SERVER DISCONNECTED", Color.RED);
					System.exit(0);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String readInput() throws IOException {
		return reader.readLine();
	}
	
	public void setTf(JTextPane tf) {
		this.tf = tf;
	}

	private void appendToPane(JTextPane tp, String msg, Color c){
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

		aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

		int len = tp.getDocument().getLength();
		tp.setCaretPosition(len);
		tp.setCharacterAttributes(aset, false);
		tp.replaceSelection(msg);
	}
}
