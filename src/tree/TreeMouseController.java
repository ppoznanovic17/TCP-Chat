package tree;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import chat.ServerFrame;

public class TreeMouseController implements MouseListener
{

	@Override
	public void mouseClicked(MouseEvent e)
	{
		if (e.getClickCount() == 2
				&& ServerFrame.getInstance().getTree().getLastSelectedPathComponent() instanceof ClientNode)
		{
			ClientNode node = (ClientNode) ServerFrame.getInstance().getTree().getLastSelectedPathComponent();
			if (node == null)
				return;
			ServerFrame.getInstance().setTrenutni(node.getName());
			if (node.getText().equals(""))
				ServerFrame.getInstance().update("Prvi: <" + node.getName() + ">");
			else
				ServerFrame.getInstance().update("Promenjeno: " + node.getText());
		}
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{

	}

	@Override
	public void mouseExited(MouseEvent e)
	{

	}

	@Override
	public void mousePressed(MouseEvent e)
	{

	}

	@Override
	public void mouseReleased(MouseEvent e)
	{

	}

}
