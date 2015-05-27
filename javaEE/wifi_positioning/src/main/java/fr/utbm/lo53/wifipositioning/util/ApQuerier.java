package fr.utbm.lo53.wifipositioning.util;

import java.io.IOException;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.utbm.lo53.wifipositioning.model.Measurement;

public class ApQuerier
{
	/** Logger of the class */
	private final static Logger	s_logger	= LoggerFactory.getLogger(ApQuerier.class);

	private Socket				m_socket;

	public ApQuerier(final String _ip, final int _port)
	{
		try
		{
			m_socket = new Socket(_ip, _port);
		} catch (IOException e)
		{
			s_logger.error("An error ocurred when creating the Socket in constructor.", e);
		}
	}

	public Measurement askMeasurement(
			final String _mobileMacAddress)
	{
		try
		{
			/* Writes in the socket to ask the rssi values. */
			m_socket.getOutputStream().write(_mobileMacAddress.getBytes());

			/* Parse the AP response. */
			s_logger.debug("Parsing AP response from byte...");
			String dataStream = new String(IOUtils.toByteArray(m_socket.getInputStream()));
			String[] dataParsed = dataStream.split(";");

			float rssi;
			if (dataParsed.length > 1)
				rssi = Float.parseFloat(dataParsed[1]);
			else
				return null;

			String apMacAddress = getForeignMacAddress();

			/* Create new measurement from values. */
			Measurement measurement = new Measurement(rssi, apMacAddress);

			return measurement;
		} catch (Exception e)
		{
			s_logger.error("An error occured when parsing the AP response data.", e);
		}
		return null;
	}

	private String getForeignMacAddress() throws SocketException
	{
		byte[] bytes = NetworkInterface.getByInetAddress(m_socket.getLocalAddress())
				.getHardwareAddress();
		StringBuilder sb = new StringBuilder();
		if (bytes != null)
		{
			for (int i = 0; i < bytes.length; ++i)
				sb.append(String.format("%02X%s", bytes[i], (i < (bytes.length - 1)) ? ":" : ""));
		}
		return sb.toString();
	}

	public void terminate()
	{
		try
		{
			m_socket.close();
		} catch (IOException e)
		{
			s_logger.error("An error occured when closing the AP linked socket.", e);
		}
	}
}