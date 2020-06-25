package chat;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

import observer.Observer;

public class ClientFrame extends JFrame implements Observer
{

	private Client korisnik;
	private JTextArea chatBox;
	private JTextField messageBox;
	private JButton posaljiPoruku;

	public ClientFrame(String nickname)
	{
		setTitle(nickname + " caskanje");
		setSize(470, 300);
		setLocationRelativeTo(null);

		JPanel southPanel = new JPanel();
		southPanel.setLayout(new GridBagLayout());

		messageBox = new JTextField(30);
		messageBox.addFocusListener(new MyFocusListener());
		messageBox.setText("Unesite poruku...");
		messageBox.addActionListener(new MyActionListener());

		chatBox = new JTextArea();
		chatBox.setEditable(false);
		chatBox.setLineWrap(true);
		chatBox.setBorder(new EmptyBorder(5, 5, 5, 5));
		DefaultCaret caret = (DefaultCaret) chatBox.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		posaljiPoruku = new JButton("Posalji poruku");
		posaljiPoruku.addActionListener(new MyActionListener());

		this.add(new JScrollPane(chatBox), BorderLayout.CENTER);

		GridBagConstraints levi = new GridBagConstraints();
		levi.anchor = GridBagConstraints.LINE_START;
		levi.fill = GridBagConstraints.HORIZONTAL;
		levi.weightx = 512;
		levi.weighty = 1;

		GridBagConstraints desni = new GridBagConstraints();
		desni.insets = new Insets(0, 10, 0, 0);
		desni.anchor = GridBagConstraints.LINE_END;
		desni.fill = GridBagConstraints.NONE;
		desni.weightx = 1;
		desni.weighty = 1;

		messageBox.setBorder(new EmptyBorder(0, 10, 0, 0));

		southPanel.add(messageBox, levi);
		southPanel.add(posaljiPoruku, desni);

		this.add(BorderLayout.SOUTH, southPanel);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		try
		{
			korisnik = new Client(nickname);
			korisnik.addObserver(this);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return;
		}

		this.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent windowEvent)
			{
				korisnik.getOutSocket().println("KRAJ.");
			}
		});
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}


	class MyFocusListener implements FocusListener
	{
		@Override
		public void focusGained(FocusEvent e)
		{
			if (messageBox.getText().equals("Unesite poruku..."))
			{
				messageBox.setText("");
			}
		}

		@Override
		public void focusLost(FocusEvent e)
		{
			if (posaljiPoruku.isFocusOwner())
			{
				messageBox.setText("Unesite poruku...");
			}
			if (messageBox.getText().equals(""))
			{
				messageBox.setText("Unesite poruku...");
			}
		}
	}

	class MyActionListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (messageBox.getText().length() < 1 || messageBox.getText().equals("Unesite poruku..."))
			{
				return;
			}
			else if (messageBox.getText().equals("/cls"))
			{
				chatBox.setText("Obrisi sve poruke\n");
				Timer t = new Timer(1500, new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						chatBox.setText("");
					}
				});
				t.setRepeats(false);
				t.start();
				messageBox.setText("Unesite poruku...");
			}
			else
			{
				korisnik.getOutSocket().println("<" + korisnik.getNick() + ">:  " + messageBox.getText());
				messageBox.setText("Unesite poruku...");
			}
			if (KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner() == messageBox)
			{
				messageBox.setText("");
			}
		}
	}

	@Override
	public void update(Object o)
	{
		if (o == null)
			return;
		if (!(o instanceof String))
			return;
		if (((String) o).equals("KRAJ."))
		{
			setVisible(false);
			dispose();
			return;
		}
		String s = (String) o;
		chatBox.append(s + "\n");
	}
}
