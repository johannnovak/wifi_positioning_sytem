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

import fr.utbm.lo53.wifipositioning.controller.CalibrateController;
import fr.utbm.lo53.wifipositioning.model.Measurement;
import fr.utbm.lo53.wifipositioning.model.Position;
import fr.utbm.lo53.wifipositioning.service.CalibrateService;

/**
 * Class extending {@link SocketRunnable} that implements the interface
 * {@link Runnable}.<br>
 * Designed to have the 'run' method called when the {@link CalibrateController}
 * receives a socket connection. This class' role is to implement
 * {@link SocketRunnable}'s abstract methods that embodied the specialization of
 * the actions and parameters to perform.<br>
 * This class has to :<br>
 * - parse the mobile phone's socket 's InputStream;<br>
 * - insert into the database the position associated with its measurements;
 * - send back the associated response.
 * 
 * @author jnovak
 *
 */
public class CalibrateRunnable extends SocketRunnable
{
	/** Logger of the class */
	private final static Logger		s_logger	= LoggerFactory.getLogger(CalibrateRunnable.class);

	/** CalibrateService used to access the database */
	private final CalibrateService	m_calibrateService;

	/* --------------------------------------------------------------------- */

	/**
	 * Default constructor. It calls the constructor of {@link SocketRunnable}
	 * and sets the runnable name.
	 * 
	 * @param _clientSocket
	 *            Client socket the mobile phone used to establish a link with
	 *            the server.
	 */
	public CalibrateRunnable(final Socket _clientSocket)
	{
		super(_clientSocket);

		m_calibrateService = CalibrateService.getInstance();

		m_runnableName = this.getClass().getSimpleName();

		s_logger.debug("CalibrateRunnable created.");
	}

	/* --------------------------------------------------------------------- */

	/**
	 * Overriden method of {@link SocketRunnable}.<br>
	 * Method used to parse the mobile request.
	 * 
	 * @return List<Object> containing the different parsed information from the
	 *         InputStream.
	 */
	@Override
	protected List<Object> parseMobileRequestHandler()
	{
		s_logger.debug("Thread-{} : Parsing mobile socket connection data for calibration.",
				m_threadID);

		try
		{
			/* Parses the data from the socket's InputStream. */
			List<Object> requestData = parseRequestData(m_clientSocket.getInputStream());
			if ((requestData == null) || requestData.isEmpty())
			{
				s_logger.error("Thread-{} : Error, empty request data list when parsing.",
						m_threadID);
				return null;
			}

			return requestData;

		} catch (Exception e)
		{
			s_logger.error(String.format(
					"Thread-%s : An error occured when parsing the mobile request data.",
					m_threadID), e);
		} finally
		{
			s_logger.debug("Thread-{} : Request parsing over.", m_threadID);
		}
		return null;
	}

	/* --------------------------------------------------------------------- */

	/**
	 * Overriden method of {@link SocketRunnable}<br>
	 * Method used to parse the data from the socket's InputStream, test the
	 * data consistency, and add them in the returned list.
	 * 
	 * @return List<Object> containing at index : <br>
	 *         - 0 : the mobile's macAddress;<br>
	 *         - 1 : the x coordinate;<br>
	 *         - 2 : the y coordinate.
	 * 
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@Override
	protected List<Object> parseRequestData(
			final InputStream _inputStream) throws IOException, ClassNotFoundException
	{
		s_logger.debug("Thread-{} : Parsing data from byte[]...", m_threadID);

		/* Gets the Strings from the InputStream */
		ArrayList<Object> list = new ArrayList<Object>();
		ObjectInputStream ois = new ObjectInputStream(_inputStream);
		String data = (String) ois.readObject();
		s_logger.debug("data : {}", data);

		String[] dataArray = data.split(";");

		/* Verify consistency. */
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

	/* --------------------------------------------------------------------- */

	/**
	 * Overriden method of {@link SocketRunnable}.<br>
	 * Method used handle all the data gathered by calling the 'calibrate'
	 * method.
	 * 
	 * @param _mobileRequestData
	 *            Verified data containing the mobile's x and y coordinate.
	 * 
	 * @return True if the access to the database was successful and no errors
	 *         have occurred.<br>
	 *         False otherwise.
	 */
	@Override
	protected boolean accessDatabaseHandler(
			final List<Object> _mobileRequestData)
	{
		/* Casts the coordinates */
		int x = Integer.parseInt((String) _mobileRequestData.get(1));
		int y = Integer.parseInt((String) _mobileRequestData.get(2));

		return calibrate(x, y, m_rssiMeasurements);
	}

	/* --------------------------------------------------------------------- */

	/**
	 * Method used to call the {@link CalibrateService} class (form the Service
	 * layer) to insert a new {@link Position} and new {@link Measurement}s to
	 * the database. If everything went fine, the mobileResponse is set to "200"
	 * meaning "OK".
	 * 
	 * @param _x
	 *            x coordinate of the mobile phone.
	 * @param _y
	 *            y coordinate of the mobile phone.
	 * @param _measurements
	 *            Set of {@link Measurement} from differents APs.
	 * @return True if the access to the database was so successful and no
	 *         errors occurred.<br>
	 *         False otherwise.
	 */
	public boolean calibrate(
			final float _x,
			final float _y,
			final Set<Measurement> _measurements)
	{
		/* Creates the new position to insert. */
		Position position = new Position(_x, _y, _measurements);

		/* For each Measurement, sets its Position attribute. */
		for (Measurement m : _measurements)
			m.setPosition(position);

		/* Inserting in the database. */
		s_logger.debug("Thread-{} : Inserting following Position : {}", m_threadID, position);
		if (m_calibrateService.insertSample(position))
		{
			m_mobileResponse = "200";
			return true;
		} else
			return false;
	}
}