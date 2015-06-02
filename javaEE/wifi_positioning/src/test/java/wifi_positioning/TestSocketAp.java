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
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

public class TestSocketAp
{
	public static void main(
			final String[] args) throws IOException, ClassNotFoundException
	{
		int apCount = 0;
		float x = 0, y = 0;
		String[] macAdresses = { "00:00:00:00:00:00", "11:11:11:11:11:11", "22:22:22:22:22:22" };

		Scanner sc = new Scanner(System.in);

		@SuppressWarnings("resource")
		ServerSocketChannel m_serverSocketChannel = ServerSocketChannel.open();
		m_serverSocketChannel.bind(new InetSocketAddress("127.0.0.1", 3000));
		m_serverSocketChannel.configureBlocking(false);

		Selector selector = Selector.open();
		m_serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

		while (true)
		{
			System.out.println("waiting");
			if (selector.select() == 0)
				continue;

			Set<SelectionKey> selectedKeys = selector.selectedKeys();
			Iterator<SelectionKey> keysIterator = selectedKeys.iterator();

			while (keysIterator.hasNext())
			{
				SelectionKey key = keysIterator.next();
				if (key.isAcceptable())
				{
					ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
					SocketChannel clientChannel = null;
					clientChannel = serverChannel.accept();
					clientChannel.configureBlocking(false);
					clientChannel.register(selector, SelectionKey.OP_READ);

				} else if (key.isReadable())
				{
					System.out.println("server reading");

					/*
					 * Creates a ByteBuffer of 25 : MacAddress => 17, x => 21, y
					 * => 25 .
					 */
					ByteBuffer buffer = ByteBuffer.allocate(25);

					/* Gets back the client channel. */
					SocketChannel clientSocketChannel = (SocketChannel) key.channel();

					String data = "";
					/* Reads the channel inputStream. */
					if (clientSocketChannel.read(buffer) > 0)
					{
						/* Flip in order to decode the buffer. */
						buffer.flip();
						data += Charset.defaultCharset().decode(buffer).toString();
						buffer.clear();
					}

					System.out.println("Read : " + data);
					x = sc.nextFloat();
					y = sc.nextFloat();
					key.channel().register(selector, SelectionKey.OP_WRITE);

				} else if (key.isWritable())
				{
					System.out.println("server writing");

					/* Writing back the message. */
					SocketChannel clientSocketChannel = (SocketChannel) key.channel();
					CharBuffer buf = CharBuffer.wrap("00:00:00:00:22:21;20046");
					while (buf.hasRemaining())
						clientSocketChannel.write(Charset.defaultCharset().encode(buf));
					clientSocketChannel.close();
					apCount++;
				}

				/* Removes the current key from the set. */
				keysIterator.remove();
			}
		}
	}
}
