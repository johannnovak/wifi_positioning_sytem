package fr.utbm.lo53.wifipositioning.controller.runnable;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.utbm.lo53.wifipositioning.model.Measurement;

/**
 * Class designed to run in a different thread than the main one. Inside, the
 * server will communicate with one AccessPoint (whose IP and port is given in
 * parameter in the constructor) by : <br>
 * - requesting a rssi by giving the mobile macAddress;<br>
 * - receiving the response of the AccessPoint containing the Access Point's
 * macAddress and the mobile's associated rssi.<br>
 * The class will then add the measurement to the socket runnable (locate or
 * calibrate) given in parameter in the constructor.
 * 
 * @author jnovak
 *
 */
public class ApRunnable implements Runnable
{
	/** Logger of the class */
	private final static Logger		s_logger	= LoggerFactory.getLogger(ApRunnable.class);

	/** IP of the Access Point */
	private final String			m_apIP;

	/** Port that the Access Point is listening to */
	private final int				m_apPort;

	/** Data contained in the mobile request (contains the macAddress) */
	private List<Object>			m_mobileRequestData;

	/** Runnable that needs the Access Points informations */
	private final SocketRunnable	m_socketRunnable;

	/* --------------------------------------------------------------------- */

	/**
	 * Default constructor. It create a ApRunnable thanks to the AP's ip and
	 * port and the runnable that needs the informations.
	 * 
	 * @param _apIP
	 *            IP of the Access Point.
	 * @param _apPort
	 *            Port of the Access Point.
	 * @param socketRunnable
	 *            SocketRunnable that needs the RSSI and the Access Point's
	 *            macAddress.
	 */
	public ApRunnable(final String _apIP, final int _apPort, final SocketRunnable socketRunnable)
	{
		m_apIP = _apIP;
		m_apPort = _apPort;
		m_socketRunnable = socketRunnable;
	}

	/* --------------------------------------------------------------------- */

	/**
	 * @param m_mobileRequestData
	 *            Sets the associated attribute.
	 */
	public void setMobileRequestData(
			final List<Object> m_mobileRequestData)
	{
		this.m_mobileRequestData = m_mobileRequestData;
	}

	/* --------------------------------------------------------------------- */

	/**
	 * Overriden method of 'Runnable' interface.<br>
	 * It connects a socket to the Access Point through its ip and port. It then
	 * writes inside the socket, the phone macAddress, and reads the
	 * AP's macAddress and the RSSI. Finally it adds these informations to the
	 * runnable.
	 */
	public void run()
	{
		Socket clientSocket = null;

		try
		{
			/* Connects a socket to the AP. */
			clientSocket = new Socket(m_apIP, m_apPort);

			/* Writes the phone's macAddress in the socket. */
			writeSocket(clientSocket);

			/*
			 * Reads the AP's macAddress and the RSSI and adds them to the
			 * runnable.
			 */
			readSocket(clientSocket);

		} catch (IOException e)
		{
			s_logger.error("An error occured when opening socket to the AP.", e);
		} finally
		{
			/* Closes the socket. */
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

	/* --------------------------------------------------------------------- */

	/**
	 * Method used to write inside the socket linked to the Access Point.
	 * 
	 * @param _socket
	 *            Socket in which one can write.
	 * @throws IOException
	 */
	private void writeSocket(
			final Socket _socket) throws IOException
	{
		ObjectOutputStream oos = new ObjectOutputStream(_socket.getOutputStream());
		/* index 0 <=> phone macAddress */
		oos.writeObject(m_mobileRequestData.get(0));
	}

	/* --------------------------------------------------------------------- */

	/**
	 * Method used to read the content of the InputStream of a socket.
	 * 
	 * @param _socket
	 *            Socket whose inputStream one wants to read.
	 */
	private void readSocket(
			final Socket _socket)
	{
		try
		{
			/* Parse the AP response */
			s_logger.debug("Parsing AP response from byte...");
			List<Object> responseData = parseResponse(_socket.getInputStream());
			String apMacAddress = (String) responseData.get(0);
			float rssi = Float.parseFloat((String) responseData.get(1));

			/* Adds a new measurement to the set */
			m_socketRunnable.addApMeasurement(new Measurement(rssi, apMacAddress));
		} catch (Exception e)
		{
			s_logger.error("An error occured when parsing the AP response data.", e);
		}
	}

	/* --------------------------------------------------------------------- */

	/**
	 * Method used to parse the response into a List<Object> from an
	 * inputStream.
	 * 
	 * @param _inputStream
	 *            InputStream one wants to parse informations from.
	 * @return List<Object> containing at index : <br>
	 *         - 0 : macAddress;<br>
	 *         - 1 : rssi;<br>
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private List<Object> parseResponse(
			final InputStream _inputStream) throws IOException, ClassNotFoundException
	{
		ArrayList<Object> list = new ArrayList<Object>();

		ObjectInputStream ois = new ObjectInputStream(_inputStream);
		String data = (String) ois.readObject();
		s_logger.debug("data : {}", data);
		String[] dataArray = data.split(";");

		if (dataArray.length != 3)
		{
			s_logger.error("Can't parse AP response data, the number of parameter is not equal to 2 ! ");
			return null;
		}

		/* Adds the macAddress */
		list.add(dataArray[0]);

		/* Adds the rssi */
		list.add(dataArray[1]);

		/* Verify that the data sent are not null or empty */
		if (((String) list.get(0) == null) || ((String) list.get(0) == ""))
		{
			s_logger.error("The ap mac address is empty! ");
			return null;
		}

		return list;
	}
}