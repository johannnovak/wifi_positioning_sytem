package wifi_positioning;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.commons.io.IOUtils;

public class TestServer
{
	public static void main(
			final String[] args) throws IOException
	{
		System.out.println("server");
		ServerSocket listener = new ServerSocket(1111);

		try
		{
			while (true)
			{
				Socket socket = listener.accept();
				try
				{
					String sent = new String(IOUtils.toByteArray(socket.getInputStream()));
					System.out.println("message : " + sent);
					PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
					out.println(sent);
				} finally
				{
					socket.close();
				}
			}
		} finally
		{
			listener.close();
		}
	}
}
