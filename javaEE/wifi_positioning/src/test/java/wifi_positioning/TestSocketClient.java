package wifi_positioning;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Random;

import org.apache.commons.io.IOUtils;

import fr.utbm.lo53.wifipositioning.util.MiscUtils;

public class TestSocketClient
{
	public static Random	r;
	public static int		port;
	public static String	ip;

	public static void main(
			final String[] args) throws IOException, InterruptedException, ClassNotFoundException
	{
		System.out.println("client");

		port = 3002;
		ip = "127.0.1.1";

		// r = new Random();
		// for (int i = 0; i < r.nextInt(1); ++i)
		// new Thread(new Runnable()
		// {
		//
		// @Override
		// public void run()
		// {
		// try
		// {
		Socket socket = new Socket();
		socket.connect(new InetSocketAddress(MiscUtils.getHostIP4Address(), port), 5000);

		Thread.sleep(r.nextInt(2000));

		System.out.println("client writing ...");
		socket.getOutputStream().write(new String("00:00:00:00:00:01;5;6;").getBytes());
		System.out.println("writing ok !");

		Thread.sleep(r.nextInt(2000));

		System.out.println("reading ....");
		String message = new String(IOUtils.toByteArray(socket.getInputStream()));
		System.out.println("reading over : " + message);

		socket.close();
		// } catch (IOException e)
		// {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (InterruptedException e)
		// {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
		// }).start();
	}
}
