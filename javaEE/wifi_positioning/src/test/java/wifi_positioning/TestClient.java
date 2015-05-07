package wifi_positioning;

import java.io.IOException;
import java.net.Socket;
import java.util.Random;

public class TestClient
{
	public static void main(
			final String[] args) throws IOException, InterruptedException
	{
		System.out.println("client");

		Socket s;
		Random r = new Random();
		int port;
		String str;
		while (true)
		{
			Thread.sleep(1000);
			str = "";
			if (r.nextBoolean())
				port = 1111;
			else
				port = 1112;

			s = new Socket("127.0.0.1", port);

			for (int i = 0; i < 50; ++i)
			{
				str += String.valueOf(Character.toChars((r.nextInt() % 26) + 'a'));
			}

			System.out.println(str);
			s.getOutputStream().write(str.getBytes());
			s.close();
		}
	}
}
