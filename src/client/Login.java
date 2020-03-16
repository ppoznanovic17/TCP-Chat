package client;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import auth.LoginState;
import sec.RSA;

@SuppressWarnings("serial")
public class Login  extends JDialog{
	private JLabel nameLabel = new JLabel("Korisnicko ime: ");
	private JLabel passwordLabel = new JLabel("Sifra: ");

	private JTextField nameField = new JTextField();
	private JTextField passwordField = new JTextField();

	private JButton okButton = new JButton("OK");
	private JButton cancelButton = new JButton("Cancel");
	
	private PrintWriter output;
	private RSA encrypt;
	private ClientInputModul cim;
	private RSA decryptor;
	private Socket client;
	
	private static ClientView cv;
	
	private LoginState state;

	public Login(Socket client, RSA encrypt, RSA decryptor, ClientInputModul cim) throws IOException {
		
		this.output = new PrintWriter(client.getOutputStream());
		this.encrypt = encrypt;
		this.cim = cim;
		this.decryptor = decryptor;
		this.client = client;
		setupUI();

		setUpListeners();

	}

	public void setupUI() {

		this.setTitle("Cope Poznanovic chat");

		JPanel topPanel = new JPanel(new GridBagLayout());
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.insets = new Insets(4, 4, 4, 4);

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0;
		topPanel.add(nameLabel, gbc);

		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		topPanel.add(nameField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		topPanel.add(passwordLabel, gbc);

		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.weightx = 1;
		topPanel.add(passwordField, gbc);

		this.add(topPanel);

		this.add(buttonPanel, BorderLayout.SOUTH);
		this.setSize(300, 150);
		this.setVisible(true);
		this.setLocationRelativeTo(null);

	}

	private void setUpListeners() {

		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					login();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Login.this.setVisible(false);
			}
		});
	}

	private void login() throws IOException {
		LoginState tempstate = null;
	
		output.println(encrypt.encryptString(nameField.getText()));
		System.out.println(encrypt.encryptString(nameField.getText()));
		output.println(encrypt.encryptString(passwordField.getText()));
		System.out.println(encrypt.encryptString(passwordField.getText()));
		System.out.println(passwordField.getText());
		output.flush();
			
		tempstate = LoginState.valueOf(decryptor.decryptString(cim.readInput()));
		System.out.println(tempstate.name());
			
		if(tempstate == LoginState.LOGIN_ACCPETED || tempstate == LoginState.REGISTERED) {
			cv = new ClientView(client, encrypt, decryptor, cim);
			
			Login.this.setVisible(false);
		}
		
		else return;
	}
}
