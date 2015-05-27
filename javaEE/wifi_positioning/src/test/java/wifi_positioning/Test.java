package wifi_positioning;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Test
{
	public static String	channelType		= "channelType";
	public static String	serverChannel	= "serverChannel";
	public static String	clientChannel	= "clientChannel";

	public static void main(
			final String[] args) throws IOException
	{
		int port = 3000;
		String ip = "127.0.0.1";

		ServerSocketChannel channel = ServerSocketChannel.open();
		channel.bind(new InetSocketAddress(port));
		channel.configureBlocking(false);

		Selector selector = Selector.open();

		channel.register(selector, SelectionKey.OP_ACCEPT);

		System.out.println("Starting server socket ...");
		while (true)
		{
			System.out.println("Selecting channel...");
			int selectedChannel;
			if ((selectedChannel = selector.select()) == 0)
				continue;

			System.out.println("Channel " + selectedChannel + " selected !");

			Set<SelectionKey> selectedKeys = selector.selectedKeys();
			Iterator<SelectionKey> keysIterator = selectedKeys.iterator();

			System.out.println("Iterating through " + Arrays.asList(selectedKeys));

			while (keysIterator.hasNext())
			{
				SelectionKey key = keysIterator.next();
				System.out.println("Key : " + key);
				if (key.isAcceptable())
				{
					handleServerSocket(key, selector);
				} else if (key.isReadable())
				{

					ByteBuffer buffer = ByteBuffer.allocate(22);
					SocketChannel clientSocketChannel = (SocketChannel) key.channel();
					if (clientSocketChannel.read(buffer) > 0)
					{
						buffer.flip();
						System.out.println(Charset.defaultCharset().decode(buffer));
						buffer.clear();
					}

					clientSocketChannel.register(selector, SelectionKey.OP_WRITE);
				} else if (key.isWritable())
				{
					SocketChannel clientSocketChannel = (SocketChannel) key.channel();
					CharBuffer buf = CharBuffer.wrap("200");
					while (buf.hasRemaining())
						clientSocketChannel.write(Charset.defaultCharset().encode(buf));

					clientSocketChannel.close();
				}
				keysIterator.remove();
				System.out.println("Key removed\n\n");
			}
		}
	}

	private static void handleClientSocket(
			final SelectionKey key) throws IOException
	{
		System.out.println("Handling client socket channel...");
		// data is available for read
		// buffer for reading
		ByteBuffer buffer = ByteBuffer.allocate(22);
		SocketChannel clientSocketChannel = (SocketChannel) key.channel();
		int bytesRead = 0;
		if (key.isReadable())
		{
			// the channel is non blocking so keep it open till the
			// count is >=0
			if ((bytesRead = clientSocketChannel.read(buffer)) > 0)
			{
				buffer.flip();
				System.out.println(Charset.defaultCharset().decode(buffer));
				buffer.clear();
			}
		}

		if (key.isWritable())
		{
			CharBuffer buf = CharBuffer.wrap("200");
			while (buf.hasRemaining())
				clientSocketChannel.write(Charset.defaultCharset().encode(buf));

		}
		if (bytesRead < 0)
		{
			// the key is automatically invalidated once the
			// channel is closed
			clientSocketChannel.close();
		}
	}

	private static void handleServerSocket(
			final SelectionKey key,
			final Selector selector) throws IOException
	{
		System.out.println("handling server socket channel ...");

		ServerSocketChannel channel = (ServerSocketChannel) key.channel();

		SocketChannel clientSocketChannel = channel.accept();

		if (clientSocketChannel != null)
		{
			clientSocketChannel.configureBlocking(false);
			SelectionKey clientKey = clientSocketChannel.register(selector, SelectionKey.OP_READ);

			Map<String, String> clientproperties = new HashMap<String, String>();
			clientproperties.put(channelType, clientChannel);
			clientKey.attach(clientproperties);
		}
	}
}
