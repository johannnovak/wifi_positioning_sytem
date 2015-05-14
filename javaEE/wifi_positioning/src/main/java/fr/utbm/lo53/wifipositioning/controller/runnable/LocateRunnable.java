package fr.utbm.lo53.wifipositioning.controller.runnable;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.utbm.lo53.wifipositioning.model.Measurement;
import fr.utbm.lo53.wifipositioning.model.Position;
import fr.utbm.lo53.wifipositioning.service.LocateService;

public class LocateRunnable extends SocketRunnable
{

	/** Logger of the class */
	private final static Logger	s_logger	= LoggerFactory.getLogger(LocateRunnable.class);

	private final LocateService	m_locateService;

	private final float			m_epsilon;

	public LocateRunnable(final Socket _clientSocket)
	{
		super(_clientSocket);

		m_locateService = LocateService.getInstance();

		m_epsilon = Float.parseFloat(System.getProperty("locate.rssi.epsilon"));

		m_runnableName = this.getClass().getSimpleName();
	}

	@Override
	protected List<Object> parseMobileRequestHandler()
	{
		s_logger.debug("Thread-{} : Parsing mobile socket connection data for {}.", m_threadID,
				m_runnableName);

		try
		{
			List<Object> data = parseRequestData(m_clientSocket.getInputStream());
			if ((data == null) || data.isEmpty())
			{
				s_logger.error("Thread-{} : Error, empty data list when parsing ({}).", m_threadID,
						m_runnableName);
				return null;
			}
			return data;
		} catch (Exception e)
		{
			s_logger.error(String.format(
					"Thread-%s : An error occured when parsing the mobile request data.",
					m_threadID), e);
		} finally
		{
			s_logger.debug("Thread-{} : Request Data Parsing over ({}).", m_threadID,
					m_runnableName);
		}
		return null;
	}

	@Override
	protected List<Object> parseRequestData(
			final InputStream _inputStream) throws IOException, ClassNotFoundException
	{
		s_logger.debug("Thread-{} : Parsing data from byte[]...", m_threadID);

		ArrayList<Object> list = new ArrayList<Object>();
		ObjectInputStream ois = new ObjectInputStream(_inputStream);
		String data = (String) ois.readObject();
		s_logger.debug("data : {}", data);

		String[] dataArray = data.split(";");

		if (dataArray.length != 1)
		{
			s_logger.error(
					"Thread-{} : Can't parse data, the number of parameter is not equal to 1 ! ",
					m_threadID);
			return null;
		}
		/* Adds the macAddress */
		list.add(dataArray[0]);

		/* Verify that the data sent are not null or empty */
		if (((String) list.get(0) == null) || ((String) list.get(0) == ""))
		{
			s_logger.error("Thread-{} : The mobile mac address is empty! ", m_threadID);
			return null;
		}

		return list;
	}

	@Override
	protected boolean accessDatabaseHandler(
			final List<Object> mobileRequestData,
			final Set<Measurement> _measurements)
	{
		return locate(_measurements);
	}

	/**
	 * Allows you to
	 * 
	 * @param request
	 * @return "OK" if all the parameters are informed else it returns a
	 *         exception
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	public boolean locate(
			final Set<Measurement> _measurements)
	{
		Position p = null;
		s_logger.debug("Thread-{} : Retrieving position from database.", m_threadID);
		if ((p = m_locateService.queryPositionFromMeasurements(_measurements, m_epsilon)) != null)
		{
			m_mobileResponse = "x:" + p.getX() + ";y:" + p.getY();
			return true;
		} else
			return false;
	}
}