package fr.utbm.lo53.wifipositioning.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.utbm.lo53.wifipositioning.model.Measurement;

/**
 * Class designed to query informations from AccessPoints through a Standard TCP
 * {@link Socket}.
 * 
 * @author jnovak
 *
 */
public class ApQuerier
{
	/** Logger of the class */
	private final static Logger	s_logger	= LoggerFactory.getLogger(ApQuerier.class);

	/** {@link Socket} used to connect to the other device. */
	private Socket				m_socket;

	/* --------------------------------------------------------------------- */

	/**
	 * Method used to create the {@link Socket} attribute from an IP and a Port.
	 * 
	 * @param _ip
	 *            IP of the {@link ServerSocket} to connect to.
	 * @param _port
	 *            Port listened by the {@link ServerSocket} to connect to.
	 */
	public ApQuerier(final String _ip, final int _port)
	{
		try
		{
			/* Creates a new socket. */
			m_socket = new Socket(_ip, _port);
		} catch (IOException e)
		{
			s_logger.error(
					String.format(
							"An error ocurred when creating the Socket in constructor, wrong IP(%s) and/or PORT(%d) /!\\.",
							_ip, _port), e);
		}
	}

	/* --------------------------------------------------------------------- */

	/**
	 * Method used to ask a measurement from the created AP linked at the other
	 * end of the newly created {@link Socket}.
	 * 
	 * @param _mobileMacAddress
	 *            MacAddress of the mobile needed to identify the signals.
	 * @return A {@link Measurement} containing the RSSI value retrieved from
	 *         the AP though the socket.
	 */
	public Measurement askMeasurement(
			final String _mobileMacAddress)
	{
		/* Tests if the socket is well connected. */
		if ((m_socket != null) && !m_socket.isClosed())
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

				s_logger.debug("Parsing successful.");
				return measurement;
			} catch (Exception e)
			{
				s_logger.error("An error occured when parsing the AP response data.", e);
			}
		}
		return null;
	}

	/* --------------------------------------------------------------------- */

	/**
	 * Method used to obtain the MacAddress of the device at the other end of
	 * the {@link Socket}.
	 * 
	 * @return MacAddress of the device at the other end of the {@link Socket}
	 */
	private String getForeignMacAddress()
	{
		try
		{
			/* Gets the MacAddress of the socket other end device. */
			byte[] bytes = NetworkInterface.getByInetAddress(m_socket.getLocalAddress())
					.getHardwareAddress();

			/* If null, it is localhost. */
			if (bytes == null)
			{
				NetworkInterface ni = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
				if (ni != null)
					bytes = ni.getHardwareAddress();
			}

			StringBuilder sb = new StringBuilder();

			if (bytes != null)
			{
				/* Formats the bytes into a readable MacAddress. */
				for (int i = 0; i < bytes.length; ++i)
					sb.append(String
							.format("%02X%s", bytes[i], (i < (bytes.length - 1)) ? ":" : ""));
			}
			return sb.toString();
		} catch (SocketException | UnknownHostException e)
		{
			s_logger.error("Could not find MacAddress for socket.", e);
		}
		return "";
	}

	/* --------------------------------------------------------------------- */

	/**
	 * Method used to close the {@link Socket}.
	 */
	public void terminate()
	{
		try
		{
			if ((m_socket != null) && !m_socket.isClosed())
				m_socket.close();
		} catch (IOException e)
		{
			s_logger.error("An error occured when closing the AP linked socket.", e);
		}
	}
}