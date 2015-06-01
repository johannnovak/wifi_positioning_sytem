package wifi_positioning;

import java.io.IOException;
import java.net.Socket;

import org.apache.commons.io.IOUtils;

public class TestSocketClient
{
	public static void main(
			final String[] args) throws IOException, InterruptedException, ClassNotFoundException
	{
		System.out.println("client");

		int port = 3001;
		String ip = "127.0.0.1";

		Socket socke = new Socket(ip, port);

		System.out.println("client writing ...");
		socke.getOutputStream().write(new String("00:00:00:00:00:01;5;6;").getBytes());
		System.out.println("writing ok !");

		System.out.println("reading ....");
		String message = new String(IOUtils.toByteArray(socke.getInputStream()));
		System.out.println("reading over : " + message);
	}
}
