package chat;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import observer.Observable;
import observer.Observer;
import tree.ClientNode;
import tree.ServerNode;

public class Server implements Runnable, Observable
{

	private static Server serverInstance = null;
	private ArrayList<PrintWriter> korisniciPW;
	private ArrayList<Socket> soketiKorisnika;
	private ArrayList<Observer> observers;
	private static ArrayList<String> imenaKorisnika;
	private static ServerSocket soketServera;

	private Server() throws Exception
	{
		korisniciPW = new ArrayList<>();
		imenaKorisnika = new ArrayList<>();
		observers = new ArrayList<>();
		soketiKorisnika = new ArrayList<>();
		soketServera = new ServerSocket(8000);

		System.out.println("Port 8000 je otvoren.");

		Thread thread = new Thread(this);
		thread.start();
	}

	public void dodajKorisnika(Socket socket, String name)
	{
		try
		{
			PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
			korisniciPW.add(out);
			soketiKorisnika.add(socket);
			imenaKorisnika.add(name);
			//saljemo svima da je dosao novi korisnik u cet

			broadcast(null, "Korisnik : " + name + " se prikljucio caskanju.");
			// obavestavamo server da doda jos jednog korisnika u listu
			notify("Dodavanje: " + name);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void brisanjeKorisnika(String nickname)
	{
		try
		{
			int i;
			for (i = 0; i < imenaKorisnika.size(); i++)
			{
				if (imenaKorisnika.get(i).equals(nickname))
					break;
			}

			// kazemo da je korisnik napustio
			broadcast(null, "Korisnik : " + imenaKorisnika.get(i) + " je napustio caskanje.");
			korisniciPW.remove(i);
			soketiKorisnika.remove(i);
			imenaKorisnika.remove(i);
			// kazemo serveru da ga skloni sa liste
			notify("Brisanje: " + nickname);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void broadcast(Object sender, Object poruka)
	{
		int i = 0;
		if (!(poruka instanceof String))
			return;
		if (sender != null && !(sender instanceof Socket))
			return;


		// updejtovanje nodova kada korisnik pusti poruku
		if (sender != null)
			((ClientNode) (((ServerNode) (ServerFrame.getInstance().getTreeModel().getRoot()))
					.getChildByString(getImeKorisnika((String) poruka)))).dodajtekst((String) poruka);

		if (((String) poruka).equals("KRAJ."))
		{
			for (int j = 0; j < korisniciPW.size(); j++)
			{
				if ((Socket) sender == soketiKorisnika.get(j))
				{
					brisanjeKorisnika(imenaKorisnika.get(j));
					return;
				}
			}
			return;
		}

		// saljem svim korisnicima poruku koju je jedan od njig napisao
		// za onog ko je pisao stampamo <Ti>
		for (PrintWriter korisnikPW : korisniciPW)
		{
			if (sender == null)
			{
				korisnikPW.println((String) poruka);
				continue;
			}
			if ((Socket) sender == soketiKorisnika.get(i))
			{
				String pom = "<";
				if (!(poruka instanceof String))
					return;
				pom += getImeKorisnika((String) poruka) + ">";
				String[] split = ((String) poruka).split(pom);
				pom = "<Ti>";
				pom += split[1];
				korisnikPW.println(pom);
			}
			else
			{
				String pom = "<";
				if (!(poruka instanceof String))
					return;
				pom += getImeKorisnika((String) poruka) + ">";
				String[] split = ((String) poruka).split(pom);
				pom += split[1];
				korisnikPW.println(pom);
			}
			i++;
		}
	}

	public static Server getInstance()
	{
		if (serverInstance == null)
		{
			try
			{
				serverInstance = new Server();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return serverInstance;
	}

	public static boolean isPokrenut()
	{
		if (serverInstance == null)
			return false;
		return true;
	}

	public void stop()
	{
		try
		{
			if (soketServera != null)
				soketServera.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
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

	public static boolean ImePostoji(String name)
	{
		for (String s : imenaKorisnika)
		{
			if (s.equals(name))
				return false;
		}
		return true;
	}

	@Override
	public void run()
	{
		try
		{
			while (true)
			{
				Socket dolazeiSoket = soketServera.accept();
				Thread t = new Thread(new ServerThread(dolazeiSoket, this));
				t.start();
			}
		}
		catch (Exception e)
		{

		}
	}

	@Override
	public String toString()
	{
		String ip = "";
		try
		{
			ip = InetAddress.getLocalHost().toString();
		}
		catch (UnknownHostException e)
		{
			e.printStackTrace();
			return "";
		}
		if (ip.contains("/"))
		{
			String[] split = ip.split("/");
			ip = split[split.length - 1];
		}
		return ip;
	}

	@Override
	public void addObserver(Observer observer)
	{
		observers.add(observer);
	}

	@Override
	public void removeObserver(Observer observer)
	{
		observers.remove(observer);
	}

	@Override
	public void notify(Object o)
	{
		for (Observer ob : observers)
		{
			ob.update(o);
		}
	}
}
