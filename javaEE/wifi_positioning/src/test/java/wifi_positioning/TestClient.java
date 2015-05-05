package wifi_positioning;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class TestClient
{
	public static void main(
			final String[] args) throws IOException
	{
		System.out.println("client");
		Socket s = new Socket("127.0.0.1", 1111);

		Scanner scanner = new Scanner(System.in);
		String str = "";
		str = scanner.next();
		s.getOutputStream().write(str.getBytes());
		scanner.close();

	}
}
