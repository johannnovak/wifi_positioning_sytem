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

public class ApRssiRunnable implements Runnable
{
	/** Logger of the class */
	private final static Logger		s_logger	= LoggerFactory.getLogger(ApRssiRunnable.class);

	private final String			m_apIP;
	private List<Object>			m_mobileRequestData;
	private final int				m_apPort;

	private final SocketRunnable	m_socketRunnable;

	public ApRssiRunnable(final String _apIP, final int _apPort, final SocketRunnable socketRunnable)
	{
		m_apIP = _apIP;
		m_apPort = _apPort;
		m_socketRunnable = socketRunnable;
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
		ObjectOutputStream oos = new ObjectOutputStream(_socket.getOutputStream());
		oos.writeObject(m_mobileRequestData.get(0));
	}

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
