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
import fr.utbm.lo53.wifipositioning.service.CalibrateService;

public class CalibrateRunnable extends SocketRunnable
{
	/** Logger of the class */
	private final static Logger		s_logger	= LoggerFactory.getLogger(CalibrateRunnable.class);

	private final CalibrateService	m_calibrateService;

	public CalibrateRunnable(final Socket _clientSocket)
	{
		super(_clientSocket);

		m_calibrateService = CalibrateService.getInstance();

		m_runnableName = this.getClass().getSimpleName();

		s_logger.debug("CalibrateRunnable created.");
	}

	@Override
	protected List<Object> parseMobileRequestHandler()
	{
		s_logger.debug("Thread-{} : Parsing mobile socket connection data for calibration.",
				m_threadID);

		try
		{
			List<Object> requestData = parseRequestData(m_clientSocket.getInputStream());
			if ((requestData == null) || requestData.isEmpty())
			{
				s_logger.error("Thread-{} : Error, empty request data list when parsing.",
						m_threadID);
				sendResponse(m_clientSocket, "500");
			}
			return requestData;
		} catch (Exception e)
		{
			s_logger.error(String.format(
					"Thread-%s : An error occured when parsing the mobile request data.",
					m_threadID), e);
		} finally
		{
			s_logger.debug("Thread-{} : Request parsed.", m_threadID);
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

		if (dataArray.length != 3)
		{
			s_logger.error(
					"Thread-{} : Can't parse data, the number of parameter is not equal to 3 ! ",
					m_threadID);
			return null;
		}
		/* Adds the macAddress */
		list.add(dataArray[0]);

		/* Adds the x coordinate */
		list.add(dataArray[1]);

		/* Adds the y coordinate */
		list.add(dataArray[2]);

		/* Verify that the data sent are not null or empty */
		if (((String) list.get(0) == null) || ((String) list.get(0) == ""))
		{
			s_logger.error("Thread-{} : The mobile mac address is empty! ", m_threadID);
			return null;
		}
		if (((String) list.get(1) == null) || ((String) list.get(1) == "")
				|| (Integer.parseInt((String) list.get(1)) < 0))
		{
			s_logger.error("Thread-{} : The coordonate x is invalid! ", m_threadID);
			return null;
		}
		if (((String) list.get(2) == null) || ((String) list.get(2) == "")
				|| (Integer.parseInt((String) list.get(1)) < 0))
		{
			s_logger.error("Thread-{} : The coordonate y is invalid! ", m_threadID);
			return null;
		}

		return list;
	}

	@Override
	protected boolean accessDatabaseHandler(
			final List<Object> _mobileRequestData,
			final Set<Measurement> _measurements)
	{
		int x = Integer.parseInt((String) _mobileRequestData.get(1));
		int y = Integer.parseInt((String) _mobileRequestData.get(2));

		return calibrate(x, y, _measurements);
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
	public boolean calibrate(
			final float _x,
			final float _y,
			final Set<Measurement> _measurements)
	{

		Position position = new Position(_x, _y, _measurements);

		List<Measurement> tempMeasurements = new ArrayList<Measurement>();
		for (Measurement m : _measurements)
		{
			m.setPosition(position);
			tempMeasurements.add(m);
		}
		for (int i = 1; i < tempMeasurements.size(); ++i)
		{
			if (tempMeasurements.get(i).equals(tempMeasurements.get(i - 1)))
			{
				_measurements.remove(tempMeasurements.get(i));
				tempMeasurements.remove(i);
				--i;
			}
		}
		tempMeasurements.clear();

		s_logger.debug("Thread-{} : Inserting following Position : {}", m_threadID, position);
		if (m_calibrateService.insertSample(position))
		{
			m_mobileResponse = "200";
			return true;
		} else
			return false;
	}
}
