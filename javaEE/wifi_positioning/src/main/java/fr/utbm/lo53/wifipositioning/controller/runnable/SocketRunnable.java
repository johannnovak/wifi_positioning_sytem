package fr.utbm.lo53.wifipositioning.controller.runnable;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.utbm.lo53.wifipositioning.model.Measurement;

public abstract class SocketRunnable implements Runnable
{
	/** Logger of the class */
	private final static Logger			s_logger			= LoggerFactory
																	.getLogger(SocketRunnable.class);

	protected Socket					m_clientSocket;
	protected int						m_macAddressByteLength;
	protected int						m_positionByteLength;

	protected int						m_rssiByteLength;
	private final ApRunnable[]		m_apRssiRunnable;
	private final Thread[]				m_apSocketThreads;
	private final Set<Measurement>		m_rssiMeasurements;

	protected String					m_mobileResponse;

	/* Attributes concerning the print for the thread */
	protected final static AtomicLong	s_threadIDCounter	= new AtomicLong();
	protected final String				m_threadID			= createID();
	protected String					m_runnableName;

	public SocketRunnable(final Socket _clientSocket)
	{
		m_clientSocket = _clientSocket;

		/* Overall data */
		m_macAddressByteLength = Integer.parseInt(System.getProperty("mac.address.byte.length"));
		m_positionByteLength = Integer.parseInt(System.getProperty("position.byte.length"));
		m_rssiByteLength = Integer.parseInt(System.getProperty("rssi.byte.length"));

		/* AP data */
		int apPort = Integer.parseInt(System.getProperty("ap.port"));
		String[] apIPs = System.getProperty("ap.ips").split(";");

		/* Creation of the runnables and the associated threads for the APs */
		m_apSocketThreads = new Thread[apIPs.length];
		m_apRssiRunnable = new ApRunnable[apIPs.length];
		m_rssiMeasurements = new HashSet<Measurement>();
		for (int i = 0; i < m_apSocketThreads.length; ++i)
		{
			m_apRssiRunnable[i] = new ApRunnable(apIPs[i], apPort, this);
			m_apSocketThreads[i] = new Thread(m_apRssiRunnable[i]);
		}
	}

	@Override
	public void run()
	{
		s_logger.debug("Thread-{} : Running {}...", m_threadID, m_runnableName);

		try
		{
			/* Handle the data retrieved from the APs */
			List<Object> mobileRequestData = parseMobileRequestHandler();

			/* Connects to the APs to get RSSI value */
			s_logger.debug("Thread-{} : Launching AP threads ({})...", m_threadID, m_runnableName);
			for (int i = 0; i < m_apRssiRunnable.length; ++i)
			{
				m_apRssiRunnable[i].setMobileRequestData(mobileRequestData);
				m_apSocketThreads[i].start();
			}

			/* Waits for the thread to finish */
			s_logger.debug("Thread-{} : Waiting for join ({})...", m_threadID, m_runnableName);
			for (Thread t : m_apSocketThreads)
				t.join();

			/* Access the database and handle data (inserts or retrieves data) */
			if (m_rssiMeasurements.isEmpty())
			{
				sendResponse(m_clientSocket, "500");
				return;
			}

			s_logger.debug("Thread-{} : Accessing database ({})...", m_threadID, m_runnableName);
			if (!accessDatabaseHandler(mobileRequestData, m_rssiMeasurements))
			{
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

	protected abstract List<Object> parseMobileRequestHandler();

	protected abstract List<Object> parseRequestData(
			final InputStream _inputStream) throws IOException, ClassNotFoundException;

	protected void sendResponse(
			final Socket _socket,
			final Object _msg) throws IOException
	{
		ObjectOutputStream oos = new ObjectOutputStream(_socket.getOutputStream());
		oos.writeObject(_msg);
	}

	public synchronized boolean addApMeasurement(
			final Measurement _measurement)
	{
		m_rssiMeasurements.add(_measurement);

		return true;
	}

	protected abstract boolean accessDatabaseHandler(
			List<Object> mobileRequestData,
			Set<Measurement> _measurements);

	private synchronized static String createID()
	{
		return String.valueOf(s_threadIDCounter.getAndIncrement());
	}
}
