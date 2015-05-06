package wifi_positioning;

import java.io.IOException;
import java.net.Socket;

public class TestClient
{
	public static void main(
			final String[] args) throws IOException
	{
		System.out.println("client");

		Socket s = new Socket("127.0.0.1", 1111);
		System.out.println("fffffffffff");
		s.getOutputStream().write(
				"fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"
						.getBytes());
		s.close();

		s = new Socket("127.0.0.1", 1111);
		System.out.println("aaaaaaaaaaaaaaaaaaaa");
		s.getOutputStream().write(
				"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
						.getBytes());
		s.close();

		s = new Socket("127.0.0.1", 1111);
		System.out.println("zzzzzzzzzzzzzzzzzz");
		s.getOutputStream().write(
				"zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz"
						.getBytes());
		s.close();

		s = new Socket("127.0.0.1", 1111);
		System.out.println("xxxxxxxxxxxxxxxxxxx");
		s.getOutputStream().write(
				"xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
						.getBytes());
		s.close();
	}
}
