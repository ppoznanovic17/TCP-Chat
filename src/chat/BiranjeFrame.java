package chat;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class BiranjeFrame extends JFrame
{
	JButton btnKlijent;

	public BiranjeFrame()
	{
		setSize(450, 250);
		JLabel lblKlijent = new JLabel("Napravi novog korisnika : ");
		JLabel lblServer = new JLabel("Napravi server : ");
		btnKlijent = new JButton("Korisnik");
		btnKlijent.setEnabled(false);
		JButton btnServer = new JButton("Server");
		JPanel panel = new JPanel(new GridBagLayout());

		btnKlijent.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				OtvoriClientFrame.open();
			}
		});

		btnServer.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				if (!Server.isPokrenut())
				{
					ServerFrame.getInstance();
					ServerFrame.getInstance().initTree();
					ServerFrame.getInstance().initFrame();
				}
				btnKlijent.setEnabled(true);
				btnServer.setEnabled(false);
			}
		});

		GridBagConstraints c = getConstraints(0, 0);

		panel.add(lblKlijent, c);
		c = getConstraints(1, 0);
		btnKlijent.setPreferredSize(new Dimension(200, 35));
		panel.add(btnKlijent, c);

		c = getConstraints(0, 1);
		panel.add(lblServer, c);
		c = getConstraints(1, 1);
		btnServer.setPreferredSize(new Dimension(200, 35));
		panel.add(btnServer, c);

		this.add(BorderLayout.CENTER, panel);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle("Izaberi");
	}

	public static void open()
	{
		BiranjeFrame chooserFrame = new BiranjeFrame();
		chooserFrame.setVisible(true);
		chooserFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private GridBagConstraints getConstraints(int x, int y)
	{
		GridBagConstraints constrain = new GridBagConstraints();
		constrain.gridx = x;
		constrain.gridy = y;
		constrain.insets = new Insets(20, 20, 0, 0);
		constrain.anchor = GridBagConstraints.WEST;
		return constrain;
	}
}
