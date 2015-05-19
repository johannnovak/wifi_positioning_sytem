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

		Socket s;
		s = new Socket("192.168.2.12", 3000);

		System.out.println("client writing");
		s.getOutputStream().write("00:00:00:00:00:01;5;6;".getBytes());

		System.out.println("client reading");
		System.out.println(new String(IOUtils.toByteArray(s.getInputStream())));
		s.close();
	}
}
