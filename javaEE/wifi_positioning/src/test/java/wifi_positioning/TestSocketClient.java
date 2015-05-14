package wifi_positioning;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class TestSocketClient
{
	public static void main(
			final String[] args) throws IOException, InterruptedException, ClassNotFoundException
	{
		System.out.println("client");

		Socket s;
		s = new Socket("127.0.0.1", 1111);

		System.out.println("client writing");
		ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
		oos.writeObject("00:00:00:00:00:01;5;6;");

		System.out.println("client reading");
		ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
		System.out.println((String) ois.readObject());
		s.close();
	}
}
