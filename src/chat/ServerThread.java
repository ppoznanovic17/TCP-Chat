package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerThread implements Runnable
{

	private Socket socket;
	private Server server;

	public ServerThread(Socket socket, Server server)
	{
		this.socket = socket;
		this.server = server;
	}

	@Override
	public void run()
	{
		try
		{
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String input = null;
			input = in.readLine();

			server.dodajKorisnika(socket, input);

			while (true)
			{
				input = in.readLine();
				if (input == null)
					break;
				if (input.trim().equals("KRAJ."))
				{
					server.broadcast(socket, input);
					break;
				}
				server.broadcast(socket, input);
			}
			System.out.println("ServerThread zatvoren.");
			socket.close();
			in.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

}
