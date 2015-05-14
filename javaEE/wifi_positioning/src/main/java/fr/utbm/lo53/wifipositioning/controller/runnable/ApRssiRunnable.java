package fr.utbm.lo53.wifipositioning.controller.runnable;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.jboss.logging.Logger;
import org.slf4j.LoggerFactory;

import fr.utbm.lo53.wifipositioning.model.Measurement;

public class ApRssiRunnable implements Runnable
{
	/** Logger of the class */
	private final static Logger		s_logger	= (Logger) LoggerFactory
														.getLogger(ApRssiRunnable.class);

	private final String			m_apIP;
	private List<Object>			m_mobileRequestData;
	private final int				m_apPort;

	private final int				m_offset;
	private final Set<Measurement>	m_measurements;
	private final int				m_macAddressByteLength;

	public ApRssiRunnable(final String _apIP, final int _apPort,
			final Set<Measurement> _measurements)
	{
		m_apIP = _apIP;
		m_apPort = _apPort;
		m_measurements = _measurements;
		m_offset = Integer.parseInt(System.getProperty("ap.response.offset"));
		m_macAddressByteLength = Integer.parseInt(System.getProperty("mac.address.byte.length"));
	}

	public void setMobileRequestData(
			final List<Object> m_mobileRequestData)
	{
		this.m_mobileRequestData = m_mobileRequestData;
	}

	@Override
	public void run()
	{
		Socket clientSocket = null;

		try
		{
			clientSocket = new Socket(m_apIP, m_apPort);

			writeSocket(clientSocket);

			readSocket(clientSocket);

		} catch (IOException e)
		{
			s_logger.error("An error occured when opening socket to the AP.", e);
		} finally
		{
			if ((clientSocket != null) && clientSocket.isConnected())
			{
				try
				{
					clientSocket.close();
				} catch (IOException e)
				{
					s_logger.error("An error occured when closing the connection", e);
				}
			}
		}

	}

	private void writeSocket(
			final Socket _socket) throws IOException
	{
		PrintWriter out = new PrintWriter(_socket.getOutputStream(), true);
		String mobileMacAddress = (String) m_mobileRequestData.get(0);
		out.println(mobileMacAddress);
	}

	private void readSocket(
			final Socket _socket)
	{
		try
		{
			/* Gets the bytes from the inputStream */
			byte[] apResponse = IOUtils.toByteArray(_socket.getInputStream());

			/* Parse the AP response */
			s_logger.debug("Parsing AP response from byte...");
			List<Object> responseData = parseResponse(apResponse, m_offset);
			float rssi = (float) responseData.get(0);
			String apMacAddress = (String) responseData.get(1);

			/* Adds a new measurement to the set */
			m_measurements.add(new Measurement(rssi, apMacAddress));
		} catch (Exception e)
		{
			s_logger.error("An error occured when parsinf the data.", e);
		}
	}

	private List<Object> parseResponse(
			final byte[] _apResponse,
			final int _offset)
	{
		int offset = _offset;
		ArrayList<Object> list = new ArrayList<Object>();

		try
		{
			/* Parses the byte array. */
			byte[] macAddressByteArray = Arrays.copyOfRange(_apResponse, offset, offset
					+ m_macAddressByteLength);
			list.add(new String(macAddressByteArray));
			offset += m_macAddressByteLength;

			byte[] rssiByteArray = Arrays.copyOfRange(_apResponse, offset, offset
					+ m_macAddressByteLength);
			list.add(new String(rssiByteArray));
			return list;
		} catch (Exception e)
		{
			s_logger.error("An error occured when parsing the AP response data.", e);
			return null;
		}
	}
}
