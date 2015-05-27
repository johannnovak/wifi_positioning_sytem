package fr.utbm.lo53.wifipositioning.controller;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.CharBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.utbm.lo53.wifipositioning.model.Measurement;
import fr.utbm.lo53.wifipositioning.util.ApQuerier;

/**
 * Abstract generic class. The generic class has to extend from
 * {@link SocketRunnable}.<br>
 * Class designed to create a {@link ServerSocket} which will listen to a
 * specified port given in parameter by its child class (such as
 * {@link CalibrateController} or {@link LocateController})<br>
 * It contains only one method that will be called after being constructed :
 * 'listen'. It will then listen to any entering connections to the socket. Then
 * it will create a new Instance of it generic class. It will finally start a
 * new thread taking as parameter the freshly created runnable and will listen
 * again to any entering connections.
 * 
 * @author jnovak
 *
 * @param <R>
 *            {@link SocketRunnable} extending class. {@link SocketRunnable}'s
 *            'run' method launched when a client has established a new
 *            connection to the server.
 */
public abstract class SocketController
{
	/** Logger of the class */
	private final static Logger		s_logger						= LoggerFactory
																			.getLogger(SocketController.class);

	/** Constant value needed to write a response to the mobile phone. */
	protected final static String	s_channelResponseContentKey		= "channelResponseContent";

	/** HTTP status codes written in the response. */
	protected final static String	s_httpOKCode					= "200";
	protected final static String	s_httpInternalServerErrorCode	= "500";

	/** String response to write in the client socket channel. */
	protected String				m_clientResponse;

	/**
	 * {@link ServerSocket} that will listen to entering connections through a
	 * specific port
	 */
	protected ServerSocketChannel	m_serverSocketChannel;

	/** Name of the controller. */
	protected String				m_controllerName;

	/* --------------------------------------------------------------------- */

	/**
	 * Default consstructor.<br>
	 * Mainly used just to create the {@link ServerSocket} attribute from the
	 * String property given in parameter.
	 * 
	 * @param _portSystemProperty
	 *            String System Property to use to get the associated integer
	 *            which is the port to listen to.
	 */
	public SocketController(final String _portSystemProperty)
	{
		/* Gets the port from the System's properties. */
		int port = Integer.parseInt(System.getProperty(_portSystemProperty));
		try
		{
			/* Creates a new ServerSocketChannel. */
			m_serverSocketChannel = ServerSocketChannel.open();
			m_serverSocketChannel.bind(new InetSocketAddress("localhost", port));
			m_serverSocketChannel.configureBlocking(false);

		} catch (IOException e)
		{
			s_logger.error(String.format(
					"An error occured when creating socket channel with port '%s'.", port), e);
		}
	}

	/* --------------------------------------------------------------------- */

	/**
	 * Method used to make the {@link ServerSocket} listen to any entering
	 * connections through its associated port. It is an infinite loop waiting
	 * for connections. When a client has established a connection, it creates a
	 * new Instance of Extending Class of {@link SocketRunnable} that will be
	 * run inside a new {@link Thread}. Finally it will listen again to any
	 * entering connections.
	 * 
	 * @return
	 */
	public boolean listen()
	{
		/* Creates a selector and registers the server socket channel. */
		Selector selector;
		try
		{
			selector = Selector.open();
			m_serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
			s_logger.info("Controller '{}' is listening on : '{}:{}'  ...", m_controllerName,
					m_serverSocketChannel.socket().getInetAddress(), m_serverSocketChannel.socket()
							.getLocalPort());
		} catch (IOException e)
		{
			s_logger.error(
					String.format("An error occured on controller '%s' when opening selector.",
							m_controllerName), e);
			return false;
		}

		channelSelectionLoop(selector);

		return true;
	}

	/* --------------------------------------------------------------------- */

	private void channelSelectionLoop(
			final Selector _selector)
	{
		if (_selector != null)
		{
			try
			{
				while (true)
				{
					if (_selector.select() == 0)
						continue;

					s_logger.debug("New channel found for controller '{}'.", m_controllerName);

					Set<SelectionKey> selectedKeys = _selector.selectedKeys();
					Iterator<SelectionKey> keysIterator = selectedKeys.iterator();

					while (keysIterator.hasNext())
					{
						SelectionKey key = keysIterator.next();
						if (key.isAcceptable())
						{
							handleNewConnection(_selector, key);
						} else if (key.isReadable())
						{
							handleReadableChannel(_selector, key);
						} else if (key.isWritable())
						{
							handleWritableChannel(key);
						}

						/* Removes the current key from the set. */
						keysIterator.remove();
					}
				}
			} catch (IOException e)
			{
				s_logger.error(String.format(
						"An error occured when acceting or registering a channel (%s).",
						m_controllerName), e);
			} finally
			{
				try
				{
					m_serverSocketChannel.close();
				} catch (IOException e)
				{
					s_logger.error(String.format(
							"Error when closing the server socket channel (%s)", m_controllerName),
							e);
				}
			}
		}
	}

	/* --------------------------------------------------------------------- */

	private void handleNewConnection(
			final Selector _selector,
			final SelectionKey _key) throws ClosedChannelException
	{
		s_logger.debug("Handling Connectable channel ({})...", m_controllerName);

		ServerSocketChannel serverChannel = (ServerSocketChannel) _key.channel();
		SocketChannel clientChannel = null;
		try
		{
			clientChannel = serverChannel.accept();
			clientChannel.configureBlocking(false);
		} catch (IOException e)
		{
			s_logger.error(String.format(
					"An error occured when obtaining the connected client channel (%s).",
					m_controllerName), e);
		}

		s_logger.debug("A new client channel is ready to be read !");
		clientChannel.register(_selector, SelectionKey.OP_READ);
	}

	/* --------------------------------------------------------------------- */

	private void handleReadableChannel(
			final Selector _selector,
			final SelectionKey _key) throws ClosedChannelException
	{
		s_logger.debug("Handling Readable channel ({})...", m_controllerName);
		boolean errorOccured = false;
		Set<Measurement> measurements = null;
		/* Handle the data retrieved from the APs. */
		List<Object> mobileRequestData = parseMobileData(_key);
		if ((mobileRequestData == null) || mobileRequestData.isEmpty())
		{
			s_logger.error("An error occured when parsing the mobile's request data ({}).",
					m_controllerName);

			errorOccured = true;
		}

		if (!errorOccured)
		{
			String mobileMacAddress = (String) mobileRequestData.get(0);
			s_logger.debug("Asking Rssi values to APs with mobile MacAddress '{}'...",
					mobileMacAddress);

			measurements = askRssiToAps(mobileMacAddress);

			if ((measurements == null) || measurements.isEmpty())
			{
				s_logger.error("RSSI measurement set is empty or null.");
				errorOccured = true;
			}
		}

		if (!errorOccured)
		{
			s_logger.debug("Accessing database ({})...", m_controllerName);
			if (!accessDatabase(mobileRequestData, measurements))
			{
				s_logger.error("An error occured when accessing the database ({}).",
						m_controllerName);
				errorOccured = true;
			}
		}

		SelectionKey writableKey = _key.channel().register(_selector, SelectionKey.OP_WRITE);
		addPropertyToKey(writableKey, s_channelResponseContentKey,
				(errorOccured) ? s_httpInternalServerErrorCode : m_clientResponse);
	}

	/* --------------------------------------------------------------------- */

	private void handleWritableChannel(
			final SelectionKey _key)
	{
		s_logger.debug("Handling Writable channel ({})...", m_controllerName);

		/* Writing back the message. */
		SocketChannel clientSocketChannel = (SocketChannel) _key.channel();
		@SuppressWarnings("unchecked")
		String msg = ((Map<String, String>) _key.attachment()).get(s_channelResponseContentKey);
		CharBuffer buf = CharBuffer.wrap(msg);
		try
		{
			while (buf.hasRemaining())
				clientSocketChannel.write(Charset.defaultCharset().encode(buf));
		} catch (IOException e)
		{
			s_logger.error(String.format(
					"An error occured when writing to the client socket channel (%s)",
					m_controllerName), e);
		}

		/* Closing the client socket channel. */
		try
		{
			clientSocketChannel.close();
		} catch (IOException e)
		{
			s_logger.error(String.format(
					"An error occured when closing the client socket channel (%s)",
					m_controllerName), e);
		}

		s_logger.debug("Channel handled and socket closed, response sent back : {}.", msg);
	}

	/* --------------------------------------------------------------------- */

	protected abstract List<Object> parseMobileData(
			SelectionKey _key);

	/* --------------------------------------------------------------------- */

	private Set<Measurement> askRssiToAps(
			final String _mobileMacAddress)
	{
		int port = Integer.parseInt(System.getProperty("ap.port"));
		String[] ips = System.getProperty("ap.ips").split(";");
		Set<Measurement> rssiMeasurements = new HashSet<Measurement>();

		ApQuerier apQuerier = null;

		for (String ip : ips)
		{
			apQuerier = new ApQuerier(ip, port);
			Measurement m = apQuerier.askMeasurement(_mobileMacAddress);
			if (m != null)
				rssiMeasurements.add(m);
			apQuerier.terminate();
		}

		s_logger.debug("Measurements retrieved : {}", Arrays.asList(rssiMeasurements));

		return rssiMeasurements;
	}

	/* --------------------------------------------------------------------- */

	protected abstract boolean accessDatabase(
			final List<Object> _mobileRequestData,
			final Set<Measurement> _rssiMeasurement);

	/* --------------------------------------------------------------------- */

	private void addPropertyToKey(
			final SelectionKey _selectionKey,
			final String _key,
			final String _value)
	{

		Map<String, String> clientproperties = new HashMap<String, String>();
		clientproperties.put(_key, _value);
		_selectionKey.attach(clientproperties);
	}
}