package fr.utbm.lo53.wifipositioning.controller;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
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
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.utbm.lo53.wifipositioning.model.Measurement;
import fr.utbm.lo53.wifipositioning.util.ApQuerier;

/**
 * Abstract class.<br>
 * Class designed to create a {@link ServerSocketChannel} which will accept
 * {@link SocketChannel} via a {@link Selector} through a
 * specified port given in parameter by its child class (such as
 * {@link CalibrateController} or {@link LocateController})<br>
 * It contains only one public method which listens to any entering connections.
 * Then it will register a new {@link SocketChannel} in the {@link Selector}
 * with the {@link SelectionKey} specified as 'Readable'. <br>
 * All 'Readable' channels are handled the same way, they are first parsed, then
 * it asks the APs for RSSI values through a normal {@link Socket}, and it
 * finally access the database.<br>
 * The class is abstract because the way the request is parsed and the way the
 * database is accessed is different along with several properties.
 * 
 * @author jnovak
 *
 */
public abstract class SocketController
{
	/** Logger of the class */
	private final static Logger		s_logger						= LoggerFactory
																			.getLogger(SocketController.class);

	/** Constant value needed to write a response to the mobile phone. */
	protected final static String	s_channelResponseContentKey		= "channelResponseContent";

	/** Constant value needed to keep in memory the ID of the socket channel */
	protected final static String	s_channelIDKey					= "ChannelID";

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

	/** ID count of the number of channels. */
	protected AtomicInteger			m_channelIDCount;

	/* --------------------------------------------------------------------- */

	/**
	 * Default constructor.<br>
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

			m_channelIDCount = new AtomicInteger();

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
	 * @return True if the method ended in a normal way.<br>
	 *         False otherwise.
	 */
	public boolean listen()
	{
		/* Creates a selector and registers the server socket channel. */
		Selector selector;
		try
		{
			/* Gets a new selector. */
			selector = Selector.open();

			/* Registers the serverSocketchannel on 'On_Accept'. */
			SelectionKey serverKey = m_serverSocketChannel.register(selector,
					SelectionKey.OP_ACCEPT);

			/* Sets the channel ID */
			addPropertyToKey(serverKey, s_channelIDKey, getNextChannelID());

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

		/* Start the loop where channels are handled. */
		channelSelectionLoop(selector);

		return true;
	}

	/* --------------------------------------------------------------------- */

	/**
	 * Method used to handle store entering socket connection through a system
	 * of {@link Selector} and {@link SocketChannel}. It follows 3 steps :<br>
	 * - When a {@link SocketChannel} is connecting to the server, a new
	 * {@link SocketChannel} is registered in the {@link Selector} as
	 * 'Readable';<br>
	 * - Any Readable {@link SocketChannel} have their inputstream parsed, then
	 * the server communicates with the aps and finally, the database is
	 * accessed. The socket is eventually registered in the selector as
	 * 'WRITABLE'<br>
	 * - Any WRITABLE {@link SocketChannel} have their outputstream filled with
	 * the associated response. The {@link SocketChannel} is then closed.
	 * 
	 * @param _selector
	 *            {@link Selector} to use in order to register
	 *            {@link SocketChannel}s.
	 */
	private void channelSelectionLoop(
			final Selector _selector)
	{
		/* If the selector is null, an error has occured earlier. */
		if (_selector != null)
		{
			try
			{
				/* Infinite loop listening to entering connections. */
				while (true)
				{
					/* Listens to any connections. */
					if (_selector.select() == 0)
						continue;

					Set<SelectionKey> selectedKeys = _selector.selectedKeys();
					Iterator<SelectionKey> keysIterator = selectedKeys.iterator();

					/*
					 * Iterates over all the channels stored inside the
					 * selector.
					 */
					while (keysIterator.hasNext())
					{
						/* Gets one channel from the selector. */
						SelectionKey key = keysIterator.next();

						s_logger.debug("Selected key ID : '{}'",
								getPropertyFromKey(key, s_channelIDKey));

						/* Checks if it is an entering connection. */
						if (key.isAcceptable())
						{
							handleNewConnection(_selector, key);

							/* Checks if it is a readable socketChannel. */
						} else if (key.isReadable())
						{
							handleReadableChannel(_selector, key);

							/* Checks if it is a writable socketChannel. */
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

	/**
	 * Method used to handle new entering connections. It accepts the
	 * {@link SocketChannel} and registers it inside the selector as READABLE.
	 * 
	 * @param _selector
	 *            {@link Selector} where the new {@link SocketChannel} is
	 *            registered.
	 * @param _key
	 *            {@link SelectionKey} referencing on the {@link SocketChannel}
	 *            that has requested a connection with the server.
	 */
	private void handleNewConnection(
			final Selector _selector,
			final SelectionKey _key)
	{
		String channelID = getPropertyFromKey(_key, s_channelIDKey);
		s_logger.debug("Channel-{} : Handling Connectable channel ({})...", channelID,
				m_controllerName);

		ServerSocketChannel serverChannel = (ServerSocketChannel) _key.channel();
		SocketChannel clientChannel = null;
		try
		{
			clientChannel = serverChannel.accept();
			clientChannel.configureBlocking(false);
		} catch (IOException e)
		{
			s_logger.error(
					String.format(
							"Channel-%s : An error occured when obtaining the connected client channel (%s).",
							channelID, m_controllerName), e);
		}

		/* Registers the clientChannel in the selector. */
		try
		{
			SelectionKey key;
			key = clientChannel.register(_selector, SelectionKey.OP_READ);
			addPropertyToKey(key, s_channelIDKey, getNextChannelID());

			s_logger.debug("A new client channel is ready to be read : {} !",
					getPropertyFromKey(key, s_channelIDKey));
		} catch (ClosedChannelException e)
		{
			s_logger.error(String.format(
					"Channel-%s : An error occured when registering socket channel for 'READING'.",
					channelID), e);
		}
	}

	/* --------------------------------------------------------------------- */

	/**
	 * Method used to handle READABLE {@link SocketChannel}s. It parses the
	 * request's inputstream, communicates with the APs to get the RSSI value
	 * associated with the mobile phone's MacAddress and finally access the
	 * database to query informations. It then registers the same
	 * {@link SocketChannel} as WRITABLE.
	 * 
	 * @param _selector
	 *            {@link Selector} where the WRITABLE {@link SocketChannel} is
	 *            registered.
	 * @param _key
	 *            {@link SelectionKey} referencing on the READABLE
	 *            {@link SocketChannel}.
	 */
	private void handleReadableChannel(
			final Selector _selector,
			final SelectionKey _key)
	{
		String channelID = getPropertyFromKey(_key, s_channelIDKey);
		s_logger.debug("Channel-{} : Handling Readable channel ({})...", channelID,
				m_controllerName);

		boolean errorOccured = false;
		Set<Measurement> measurements = null;

		/* Handle the data retrieved from the APs. */
		List<Object> mobileRequestData = parseMobileData(_key);
		if ((mobileRequestData == null) || mobileRequestData.isEmpty())
		{
			s_logger.error(
					"Channel-{} : An error occured when parsing the mobile's request data ({}).",
					channelID, m_controllerName);

			errorOccured = true;
		}

		/* Asks the RSSI to the APs. */
		if (!errorOccured)
		{
			String mobileMacAddress = (String) mobileRequestData.get(0);
			s_logger.debug("Channel-{} : Asking Rssi values to APs with mobile MacAddress '{}'...",
					channelID, mobileMacAddress);

			measurements = askRssiToAps(mobileMacAddress);

			if ((measurements == null) || measurements.isEmpty())
			{
				s_logger.error("Channel-{} : RSSI measurement set is empty or null.", channelID);
				errorOccured = true;
			}
		}

		/* Access the database to query informations. */
		if (!errorOccured)
		{
			s_logger.debug("Channel-{} : Accessing database ({})...", channelID, m_controllerName);
			if (!accessDatabase(mobileRequestData, measurements))
			{
				s_logger.error("Channel-{} : An error occured when accessing the database ({}).",
						channelID, m_controllerName);
				errorOccured = true;
			}
		}

		/* Registers the socketChannel in the selector as WRITABLE. */
		try
		{
			SelectionKey writableKey = _key.channel().register(_selector, SelectionKey.OP_WRITE);
			addPropertyToKey(writableKey, s_channelResponseContentKey,
					(errorOccured) ? s_httpInternalServerErrorCode : m_clientResponse);
			addPropertyToKey(writableKey, s_channelIDKey, channelID);

		} catch (ClosedChannelException e)
		{
			s_logger.error(String.format(
					"Channel-%s : An error occured when registering socket channel for 'WRITING'.",
					channelID), e);
		}
	}

	/* --------------------------------------------------------------------- */

	/**
	 * Method used to write a response back to the client as the
	 * {@link SocketChannel} is writable. It finally closes the {@link Socket}.
	 * 
	 * @param _key
	 *            {@link SelectionKey} referencing on the {@link SocketChannel}
	 *            whose ouputstream is filled with the associated client's
	 *            response.
	 */
	private void handleWritableChannel(
			final SelectionKey _key)
	{
		String channelID = getPropertyFromKey(_key, s_channelIDKey);
		s_logger.debug("Channel-{} : Handling Writable channel ({})...", channelID,
				m_controllerName);

		/* Writing back the message. */
		SocketChannel clientSocketChannel = (SocketChannel) _key.channel();

		/* Gets the message to send back to the client. */
		String msg = getPropertyFromKey(_key, s_channelResponseContentKey);

		/* Writes the response inside the socket. */
		try
		{
			CharBuffer buf = CharBuffer.wrap(msg);
			while (buf.hasRemaining())
				clientSocketChannel.write(Charset.defaultCharset().encode(buf));
		} catch (IOException e)
		{
			s_logger.error(String.format(
					"Channel-%s : An error occured when writing to the client socket channel (%s)",
					channelID, m_controllerName), e);
		}

		/* Closes the client socket channel. */
		try
		{
			clientSocketChannel.close();
		} catch (IOException e)
		{
			s_logger.error(String.format(
					"Channel-{} : An error occured when closing the client socket channel (%s)",
					channelID, m_controllerName), e);
		}

		s_logger.debug("Channel-{} : Handled, socket closed, response sent back : {}.", channelID,
				msg);
	}

	/* --------------------------------------------------------------------- */

	/**
	 * Abstract method used to be implemented by child classes. It parses the
	 * inputstream of a {@link SocketChannel} and return a List of Object
	 * containing the necessary informations.
	 * 
	 * @param _key
	 *            {@link SelectionKey} referencing on the {@link SocketChannel}
	 *            whose inputstream is read.
	 * @return List of object where necessary parsed informations are stored.
	 */
	protected abstract List<Object> parseMobileData(
			SelectionKey _key);

	/* --------------------------------------------------------------------- */

	/**
	 * Method used to ask the RSSI values to the different APs.
	 * 
	 * @param _mobileMacAddress
	 *            MacAddress of the mobile sent to the AP to help them identify
	 *            the good RSSI values.
	 * @return Set of {@link Measurement} associated with the MacAddress of the
	 *         mobile phone.
	 */
	private Set<Measurement> askRssiToAps(
			final String _mobileMacAddress)
	{
		int port = Integer.parseInt(System.getProperty("ap.port"));
		String[] ips = System.getProperty("ap.ips").split(";");
		Set<Measurement> rssiMeasurements = new HashSet<Measurement>();

		ApQuerier apQuerier = null;

		/* for each APs. */
		for (String ip : ips)
		{
			/* Creates a new APQuerier. */
			apQuerier = new ApQuerier(ip, port);

			/* Asks a measurement concerning this AP. */
			Measurement m = apQuerier.askMeasurement(_mobileMacAddress);

			/* Adds the measurement if it is not null. */
			if (m != null)
				rssiMeasurements.add(m);

			/* Terminate the APQuerier. */
			apQuerier.terminate();
		}

		s_logger.debug("Measurements retrieved : {}", Arrays.asList(rssiMeasurements));

		return rssiMeasurements;
	}

	/* --------------------------------------------------------------------- */

	/**
	 * Abstract Method used to query or insert informations in the database.
	 * 
	 * @param _mobileRequestData
	 *            Data that can be useful when accessing the database.
	 * @param _rssiMeasurement
	 *            Set of {@link Measurement} that helps defining the position of
	 *            the mobile phone.
	 * @return True if no errors have occurred when accessing the database.<br>
	 *         False otherwise.
	 */
	protected abstract boolean accessDatabase(
			final List<Object> _mobileRequestData,
			final Set<Measurement> _rssiMeasurement);

	/* --------------------------------------------------------------------- */

	/**
	 * Method used to add a property to a {@link SelectionKey}
	 * 
	 * @param _selectionKey
	 *            {@link SelectionKey} that is given a new property.
	 * @param _key
	 *            Key of the property to add.
	 * @param _value
	 *            Value of the property to add.
	 */
	@SuppressWarnings("unchecked")
	private void addPropertyToKey(
			final SelectionKey _selectionKey,
			final String _key,
			final String _value)
	{
		Map<String, String> clientproperties = new HashMap<String, String>();
		if (_selectionKey.attachment() != null)
			clientproperties.putAll(((Map<String, String>) _selectionKey.attachment()));
		clientproperties.put(_key, _value);
		_selectionKey.attach(clientproperties);
	}

	/* --------------------------------------------------------------------- */

	/**
	 * Method used to get a property from a {@link SelectionKey}.
	 * 
	 * @param _selectionKey
	 *            {@link SelectionKey} whose properties are needed.
	 * @param _key
	 *            Key of the properties needed.
	 * @return Value of the property for the key '_key'.
	 */
	@SuppressWarnings("unchecked")
	private String getPropertyFromKey(
			final SelectionKey _selectionKey,
			final String _key)
	{
		return ((Map<String, String>) _selectionKey.attachment()).get(_key);
	}

	/* --------------------------------------------------------------------- */

	/**
	 * @return An incremented ID of the channel.
	 */
	public String getNextChannelID()
	{
		return "" + m_channelIDCount.incrementAndGet();
	}
}