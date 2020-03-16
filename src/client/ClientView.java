package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import sec.RSA;

/*
 * Klasa za GUI klijenta
 */
@SuppressWarnings("serial")
public class ClientView extends JFrame{
	
	private static JTextPane ta;
	private static JTextField tf;

	private static JPanel p1;
	private static JPanel p2;
	private static JScrollPane jsp;
	
	private Socket client;
	private	RSA encrypt;
	private RSA decryptor;
	private ClientInputModul cim;
	private PrintWriter socket_out;
	
	public ClientView(Socket client, RSA encrypt, RSA decryptor, ClientInputModul cim) throws IOException {
		// TODO Auto-generated constructor stub
		this.client = client;
		this.encrypt = encrypt;
		this.decryptor = decryptor;
		this.cim = cim;
		socket_out = new PrintWriter(client.getOutputStream());
		
		ta = new JTextPane();
		ta.setBackground(Color.BLACK);
		cim.setTf(ta);
		ta.setMinimumSize(new Dimension(400, 400));
		appendToPane(ta, "Dobrodosao na Cope Poznanovic chat. Chat pod RSA enkripcijom\n", Color.BLUE);

		tf = new JTextField(20);
		tf.addKeyListener(new KeyAdapter() {
	        @Override
	        public void keyPressed(KeyEvent e) {
	            if(e.getKeyCode() == KeyEvent.VK_ENTER){
	            	if (!tf.getText().equals("")) {
						socket_out.println(encrypt.encryptString(tf.getText()));
					} 
					
	            	appendToPane(ta, "YOU: ", Color.MAGENTA);
					appendToPane(ta, tf.getText() + "\n", Color.GREEN);
					socket_out.flush();
					tf.setText("");
					tf.requestFocusInWindow();
	            }
	        }

	    });
		p1 = new JPanel();
		p1.add(tf);
		p1.setBackground(Color.BLACK);
		
		jsp = new JScrollPane(ta);
		jsp.setBackground(Color.BLACK);
		
		this.add(jsp);
		this.add(BorderLayout.SOUTH,p1);
		this.setSize(400,400);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		
		Color white = new Color(101, 255, 35);
		JButton b1 = new JButton("Send");
		b1.setBackground(white);
		b1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!tf.getText().equals("")) {
					socket_out.println(encrypt.encryptString(tf.getText()));
				} 
				
				appendToPane(ta, "YOU: ", Color.MAGENTA);
				appendToPane(ta, tf.getText() + "\n", Color.GREEN);
				socket_out.flush();
				tf.setText("");
				tf.requestFocusInWindow();
			}
		});
		p1.add(b1);
		//pack();
		new Thread(cim).start();
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
