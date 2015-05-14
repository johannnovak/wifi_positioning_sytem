package fr.utbm.lo53.wifipositioning.controller.runnable;

import java.io.IOException;
import java.io.PrintWriter;
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
	protected int						m_packetOffset;
	protected int						m_macAddressByteLength;
	protected int						m_positionByteLength;

	protected int						m_rssiByteLength;
	private final ApRssiRunnable[]		m_apRssiRunnable;
	private final Thread[]				m_apSocketThreads;
	private final Set<Measurement>		m_rssiMeasurements;

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
		m_apRssiRunnable = new ApRssiRunnable[apIPs.length];
		m_rssiMeasurements = new HashSet<Measurement>();
		for (int i = 0; i < m_apSocketThreads.length; ++i)
		{
			m_apRssiRunnable[i] = new ApRssiRunnable(apIPs[i], apPort, m_rssiMeasurements);
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
			for (int i = 0; i < m_apRssiRunnable.length; ++i)
			{
				m_apRssiRunnable[i].setMobileRequestData(mobileRequestData);
				m_apSocketThreads[i].start();
			}

			/* Waits for the thread to finish */
			for (Thread t : m_apSocketThreads)
				t.join();

			/* Access the database and handle data (inserts or retrieves data) */
			accessDatabaseHandler(mobileRequestData, m_rssiMeasurements);

		} catch (IOException e)
		{
			s_logger.error(String.format(
					"Thread-%d : An IO error occured when handling client socket '%s'.",
					m_threadID, m_runnableName), e);
		} catch (InterruptedException e)
		{
			s_logger.error(String.format(
					"Thread-%d : An error occured when joining ApSocketThreads.", m_threadID), e);
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
							"Thread-%s : Error when closing the client socket.", m_threadID), e);
				}
			}
			s_logger.debug("Thread-%s : over", m_threadID);
		}
	}

	protected abstract List<Object> parseMobileRequestHandler() throws IOException;

	protected abstract List<Object> parseRequestData(
			final byte[] _bytes,
			final int _offset);

	protected void handleResponse(
			final Socket _socket,
			final byte[] _msg) throws IOException
	{
		PrintWriter out = new PrintWriter(_socket.getOutputStream(), true);
		out.println(_msg);
	}

	protected abstract boolean accessDatabaseHandler(
			List<Object> mobileRequestData,
			Set<Measurement> _measurements);

	private synchronized static String createID()
	{
		return String.valueOf(s_threadIDCounter.getAndIncrement());
	}
}
