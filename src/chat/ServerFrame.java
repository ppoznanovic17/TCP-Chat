package chat;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;
import javax.swing.tree.TreeNode;

import observer.Observer;
import tree.ClientNode;
import tree.ServerNode;
import tree.Tree;
import tree.TreeModel;
import tree.TreePanel;

public class ServerFrame extends JFrame implements Observer
{

	private Server server;
	private JTextArea chatBox;
	private static ServerFrame instance = null;
	private Tree tree;
	private TreeModel treeModel;
	private String trenutni;

	private ServerFrame()
	{

	}

	public void initFrame()
	{
		try
		{
			trenutni = "";
			server = Server.getInstance();
			server.addObserver(this);

			setTitle("Server : " + server);
			setSize(550, 350);
			setLocationRelativeTo(null);

			chatBox = new JTextArea();
			chatBox.setEditable(false);
			chatBox.setLineWrap(true);
			chatBox.setBorder(new EmptyBorder(5, 5, 5, 5));
			chatBox.setText("");
			DefaultCaret caret = (DefaultCaret) chatBox.getCaret();
			caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

			TreePanel panLeft = new TreePanel();
			JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panLeft, new JScrollPane(chatBox));
			splitPane.setDividerLocation(160);

			this.add(splitPane, BorderLayout.CENTER);

			this.addWindowListener(new WindowAdapter()
			{
				@Override
				public void windowClosing(WindowEvent windowEvent)
				{
					if (Server.isPokrenut())
						server.stop();
				}
			});
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void initTree()
	{
		tree = new Tree();
		treeModel = new TreeModel();
		tree.setModel(treeModel);
	}

	public static ServerFrame getInstance()
	{
		if (instance == null)
		{
			instance = new ServerFrame();
			instance.setVisible(true);
		}
		return instance;
	}

	public Server getServer()
	{
		return server;
	}

	private String getImeKorisnika(String nick)
	{
		String pom = "";
		int i = 1;
		while (nick.charAt(i) != '>')
		{
			pom += nick.charAt(i);
			i++;
		}
		return pom;
	}

	@Override
	public void update(Object o)
	{
		if (o instanceof String)
		{
			String str = (String) o;
			if (str.startsWith("Prvi: "))
			{
				String split[] = str.split("Prvi: ");
				String userNick = getImeKorisnika(split[1]);
				if (split.length == 2)
					chatBox.setText(userNick + "istorija ceta.\n");
			}
			else if (str.startsWith("Brisanje: "))
			{
				String split[] = str.split("Brsanje: ");

				((ServerNode) treeModel.getRoot())
						.removeChild(((ServerNode) treeModel.getRoot()).getChildByString(split[1]));
				SwingUtilities.updateComponentTreeUI(tree);
				if (((ServerNode) treeModel.getRoot()).getChildCount() == 0)
					chatBox.setText("");
				if (split[1].equals(trenutni))
				{
					if (((ServerNode) treeModel.getRoot()).getChildCount() != 0)
					{
						trenutni = ((ClientNode) ((ServerNode) treeModel.getRoot()).getChildAt(0)).getName();
						update("Menjanje: " + ((ClientNode) ((ServerNode) treeModel.getRoot()).getChildAt(0)).getText());
					}
					else
					{
						trenutni = "";
					}
				}
			}
			else if (str.startsWith("Dodavanje: "))
			{
				String split[] = str.split("Dodavanje: ");

				ClientNode node = new ClientNode(split[1], (TreeNode) treeModel.getRoot());
				node.addObserver(this);
				((ServerNode) treeModel.getRoot()).addChild(node);
				videtiSveCvorove(tree);
				SwingUtilities.updateComponentTreeUI(tree);
			}
			else if (str.startsWith("Promenjeno: "))
			{
				String split[] = str.split("Promenjeno: ");
				String userNick = getImeKorisnika(split[1]);
				if (!(userNick.equals(trenutni)))
					return;
				if (split.length == 2)
				{
					chatBox.setText(userNick + " istorija caskanja.\n");
					chatBox.append(split[1]);
				}
				else
					chatBox.setText("");
			}
		}

	}

	private void videtiSveCvorove(Tree tree)
	{
		int j = tree.getRowCount();
		int i = 0;
		while (i < j)
		{
			tree.expandRow(i);
			i += 1;
			j = tree.getRowCount();
		}
	}

	public String getTrenutni()
	{
		return trenutni;
	}

	public void setTrenutni(String trenutni)
	{
		this.trenutni = trenutni;
	}

	public Tree getTree()
	{
		return tree;
	}

	public TreeModel getTreeModel()
	{
		return treeModel;
	}
}
