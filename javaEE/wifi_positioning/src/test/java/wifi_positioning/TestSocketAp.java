package wifi_positioning;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TestSocketAp
{
	public static void main(
			final String[] args) throws IOException, ClassNotFoundException
	{
		@SuppressWarnings("resource")
		ServerSocket serverSocket = new ServerSocket(3000);

		while (true)
		{
			System.out.println("waiting");
			Socket s = serverSocket.accept();

			System.out.println("server reading");

			ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
			System.out.println((String) ois.readObject());

			System.out.println("server writing");
			ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
			oos.writeObject("00:00:00:00:00:00;5;5;");

			s.close();
		}
	}
}
