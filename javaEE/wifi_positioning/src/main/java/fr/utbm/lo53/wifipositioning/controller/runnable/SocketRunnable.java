package fr.utbm.lo53.wifipositioning.controller.runnable;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.utbm.lo53.wifipositioning.model.Measurement;

/**
 * Abstract class implementing {@link Runnable}.<br>
 * Class designed to define the global architecture and life cycle of sockets
 * communicating with the server. This class communicates with the mobile phones
 * and creates an {@link ApRunnable}: to communicate with the Access Points.
 * When its 'run' method is called, the following pattern is executed :<br>
 * - parse the mobile's request data and test the consistency of the data
 * obtained;
 * - for each AP, creates a new Thread with an {@link ApRunnable} that will
 * communicate with one AP;
 * - wait for each Thread to terminate;
 * - access the database to insert or retrieve informations;
 * - return a response to the mobile phone.
 * 
 * @author jnovak
 *
 */
public abstract class SocketRunnable implements Runnable
{
	/** Logger of the class */
	private final static Logger			s_logger			= LoggerFactory
																	.getLogger(SocketRunnable.class);
	/** Socket linking the server with the mobile phone */
	protected Socket					m_clientSocket;

	/**
	 * Array of {@link ApRunnable} needed to communicate afterwards with the APs
	 */
	private final ApRunnable[]			m_apRunnable;

	/**
	 * Array of {@link Thread} that will be started when the server is willing
	 * to communicate with the APs
	 */
	private final Thread[]				m_apSocketThreads;

	/** Retrieved RSSI {@link Measurement} from the APs' */
	protected final Set<Measurement>	m_rssiMeasurements;

	/** Response to send back to the mobile phone at the end */
	protected String					m_mobileResponse;

	/**
	 * ID of the thread launched. Each time a client is connecting to the
	 * {@link ServerSocket}, this value is incremented.
	 */
	protected final static AtomicLong	s_threadIDCounter	= new AtomicLong();

	/** ID of the thread (used with 's_threadIDCounter') */
	protected final String				m_threadID			= createID();

	/** Name of the runnable that is being runned */
	protected String					m_runnableName;

	/* --------------------------------------------------------------------- */

	/**
	 * Default Constructor.<br>
	 * Sets the Client Socket attribute and create beforehand all of the array (
	 * {@link Thread} and {@link ApRunnable}) with different properties
	 * retrieved from the System Properties.
	 * 
	 * @param _clientSocket
	 *            Socket linking the mobile phone and the server.
	 */
	public SocketRunnable(final Socket _clientSocket)
	{
		m_clientSocket = _clientSocket;

		/* Retrieves AP data */
		int apPort = Integer.parseInt(System.getProperty("ap.port"));
		String[] apIPs = System.getProperty("ap.ips").split(";");

		/* Creates the runnables and the associated threads for the APs */
		m_apSocketThreads = new Thread[apIPs.length];
		m_apRunnable = new ApRunnable[apIPs.length];
		m_rssiMeasurements = new HashSet<Measurement>();
		for (int i = 0; i < m_apSocketThreads.length; ++i)
		{
			m_apRunnable[i] = new ApRunnable(apIPs[i], apPort, this);
			m_apSocketThreads[i] = new Thread(m_apRunnable[i]);
		}
	}

	/* --------------------------------------------------------------------- */

	/**
	 * Overriden method from {@link Runnable}.<br>
	 * Overall lifecycle of every actions our {@link ServerSocket} does when
	 * accepting a outside socket connection. The following actions are done : <br>
	 * - parse the mobile's request data and test the consistency of the data
	 * obtained;
	 * - for each AP, creates a new Thread with an {@link ApRunnable} that will
	 * communicate with one AP;
	 * - wait for each Thread to terminate;
	 * - access the database to insert or retrieve informations;
	 * - return a response to the mobile phone.
	 */
	public void run()
	{
		s_logger.debug("Thread-{} : Running {}...", m_threadID, m_runnableName);

		try
		{
			/* Handle the data retrieved from the APs. */
			List<Object> mobileRequestData = parseMobileRequestHandler();
			if ((mobileRequestData == null) || mobileRequestData.isEmpty())
			{
				s_logger.error(
						"Thread-{} : An error occured when parsing the mobile's request data ({}).",
						m_threadID, m_runnableName);
				sendResponse(m_clientSocket, "500");
				return;
			}

			/* Connects to the APs to get RSSI values. */
			s_logger.debug("Thread-{} : Launching AP threads ({})...", m_threadID, m_runnableName);
			for (int i = 0; i < m_apRunnable.length; ++i)
			{
				m_apRunnable[i].setMobileRequestData(mobileRequestData);
				m_apSocketThreads[i].start();
			}

			/* Waits for the threads to finish. */
			s_logger.debug("Thread-{} : Waiting for join ({})...", m_threadID, m_runnableName);
			for (Thread t : m_apSocketThreads)
				t.join();

			/* Access the database and handle data (inserts or retrieves data). */
			if (m_rssiMeasurements.isEmpty())
			{
				sendResponse(m_clientSocket, "500");
				return;
			}

			s_logger.debug("Thread-{} : Accessing database ({})...", m_threadID, m_runnableName);
			if (!accessDatabaseHandler(mobileRequestData))
			{
				s_logger.error("Thread-{} : An error occured when accessing the database ({}).",
						m_threadID, m_runnableName);
				sendResponse(m_clientSocket, "500");
				return;
			}

			/* Response to the mobile */
			s_logger.debug("Thread-{} : Sending response ({})...", m_threadID, m_runnableName);
			sendResponse(m_clientSocket, m_mobileResponse);

		} catch (IOException e)
		{
			s_logger.error(String.format(
					"Thread-%d : An IO error occured when handling client socket '%s'.",
					m_threadID, m_runnableName), e);
		} catch (InterruptedException e)
		{
			s_logger.error(String.format(
					"Thread-%d : An error occured when joining ApSocketThreads (%s).", m_threadID,
					m_runnableName), e);
		} finally
		{
			if (m_clientSocket != null)
			{
				try
				{
					m_clientSocket.close();
				} catch (IOException e)
				{
					s_logger.error(String.format(
							"Thread-%s : Error when closing the client socket ({}).", m_threadID,
							m_runnableName), e);
				}
			}
			s_logger.debug("Thread-{} : over ({})", m_threadID, m_runnableName);
		}
	}

	/* --------------------------------------------------------------------- */

	/**
	 * Abstract method used to define how the mobile's request Data has to be
	 * parsed and stored inside a list of Object.
	 * 
	 * @return List of Object containing the desired parsed Data from the mobile
	 *         request.
	 */
	protected abstract List<Object> parseMobileRequestHandler();

	/* --------------------------------------------------------------------- */

	/**
	 * Abstract method called inside 'parseMobileRequestHandler()'.<br>
	 * Parses the mobile's request data, verifies the consistency of the
	 * obtained data, and tests the values.
	 * 
	 * @param _inputStream
	 *            InputStream to parse data from.
	 * @return List of Object containing specific values at specific indexes.
	 * 
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	protected abstract List<Object> parseRequestData(
			final InputStream _inputStream) throws IOException, ClassNotFoundException;

	/* --------------------------------------------------------------------- */

	/**
	 * Method used to send back a response to the mobile phone. If it concerns
	 * an error, please write after the call, 'return x' in order to terminate
	 * the method where it is called.
	 * 
	 * @param _socket
	 *            {@link Socket} to write a message to.
	 * @param _msg
	 *            Message to write inside the OutputStream of the {@link Socket}
	 *            .
	 * @throws IOException
	 */
	protected void sendResponse(
			final Socket _socket,
			final Object _msg) throws IOException
	{
		ObjectOutputStream oos = new ObjectOutputStream(_socket.getOutputStream());
		oos.writeObject(_msg);
	}

	/* --------------------------------------------------------------------- */

	/**
	 * Synchronized method.<br>
	 * Used by {@link ApRunnable} when they received the RSSI from the APs. It
	 * adds a new {@link Measurement} to the Set 'm_rssiMeasurements'. It also
	 * verifies if the {@link Measurement} to add is not already contained
	 * inside the Set.
	 * 
	 * @param _measurement
	 *            {@link Measurement} to add to the attribute
	 *            'm_rssiMeasurements'.
	 * 
	 * @return True if the {@link Measurement} has been added.<br>
	 *         False otherwise.
	 */
	public synchronized boolean addApMeasurement(
			final Measurement _measurement)
	{
		/* Tests if the Measurement is already inide the Set. */
		Iterator<Measurement> iterator = m_rssiMeasurements.iterator();
		boolean containsMacAddress = false;
		while (iterator.hasNext())
		{
			Measurement testMeasurement = iterator.next();
			if (testMeasurement.equals(_measurement))
				containsMacAddress |= true;
			else
				containsMacAddress |= false;
		}

		/* If it is not inside, adds the Measurement. */
		if (!containsMacAddress)
			m_rssiMeasurements.add(_measurement);
		return !containsMacAddress;
	}

	/* --------------------------------------------------------------------- */

	/**
	 * Abstract method.<br>
	 * Method used to access the database and retrieve/insert informations from
	 * it.
	 * 
	 * @param mobileRequestData
	 *            Data parsed from the mobile's request. Some informations are
	 *            needed.
	 * @return True if not errors has been encountered when accessing the
	 *         database.<br>
	 *         False otherwise.
	 */
	protected abstract boolean accessDatabaseHandler(
			List<Object> mobileRequestData);

	/* --------------------------------------------------------------------- */

	/**
	 * Method used to get the ID of the IDCounter and then increments the
	 * IDCounter.
	 * 
	 * @return New ID of the Thread running this runnable.
	 */
	private synchronized static String createID()
	{
		return String.valueOf(s_threadIDCounter.getAndIncrement());
	}
}