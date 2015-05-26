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

import fr.utbm.lo53.wifipositioning.controller.LocateController;
import fr.utbm.lo53.wifipositioning.model.Measurement;
import fr.utbm.lo53.wifipositioning.model.Position;
import fr.utbm.lo53.wifipositioning.service.LocateService;

/**
 * Class extending {@link SocketRunnable} that implements the interface
 * {@link Runnable}.<br>
 * Designed to have the 'run' method called when the {@link LocateController}
 * receives a socket connection. This class' role is to implement
 * {@link SocketRunnable}'s abstract methods that embodied the specialization of
 * the actions and parameters to perform.<br>
 * This class has to :<br>
 * - parse the mobile phone's socket 's InputStream;<br>
 * - retrieve from the database a position associated with the retrieved
 * measurements;
 * - send back the position in the response.
 * 
 * @author jnovak
 *
 */
public class LocateRunnable extends SocketRunnable
{

	/** Logger of the class */
	private final static Logger	s_logger	= LoggerFactory.getLogger(LocateRunnable.class);

	/** {@link LocateService} used to access the database */
	private final LocateService	m_locateService;

	/**
	 * Constant found in the server configuration file used to tell the degree
	 * of inaccuracy of a RSSI
	 */
	private final float			m_epsilon;

	/* --------------------------------------------------------------------- */

	/**
	 * Default Constructor.<br>
	 * Call {@link SocketRunnable} constructor and sets the different attributes
	 * and properties.
	 * 
	 * @param _clientSocket
	 *            Client's socket linked with the server.
	 */
	public LocateRunnable(final Socket _clientSocket)
	{
		super(_clientSocket);

		m_locateService = LocateService.getInstance();

		m_epsilon = Float.parseFloat(System.getProperty("locate.rssi.epsilon"));

		m_runnableName = this.getClass().getSimpleName();
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
		s_logger.debug("Thread-{} : Parsing mobile socket connection data for {}.", m_threadID,
				m_runnableName);

		try
		{
			/* Parses the data from the socket's InputStream. */
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

	/* --------------------------------------------------------------------- */

	/**
	 * Overriden method of {@link SocketRunnable}<br>
	 * Method used to parse the data from the socket's InputStream, test the
	 * data consistency, and add them in the returned list.
	 * 
	 * @return List<Object> containing at index : <br>
	 *         - 0 : the mobile's macAddress;
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

	/* --------------------------------------------------------------------- */

	/**
	 * Overriden method of {@link SocketRunnable}.<br>
	 * Method used handle all the data gathered by calling the 'locate'
	 * method.
	 * 
	 * @param _mobileRequestData
	 *            Not used.
	 * @param _measurements
	 *            Set of {@link Measurement} retrieved from the different APs.
	 * 
	 * @return True if the access to the database was successful and no errors
	 *         have occurred.<br>
	 *         False otherwise.
	 */
	@Override
	protected boolean accessDatabaseHandler(
			final List<Object> mobileRequestData)
	{
		return locate(m_rssiMeasurements);
	}

	/* --------------------------------------------------------------------- */

	/**
	 * Method used to call the {@link LocateService} class (form the Service
	 * layer) to retrieve a {@link Position} from a Set of {@link Measurement}
	 * from the database. If everything went fine, the mobileResponse is set to
	 * "200"
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
	public boolean locate(
			final Set<Measurement> _measurements)
	{
		/* Creates Position to be returned. */
		Position p = null;
		s_logger.debug("Thread-{} : Retrieving position from database.", m_threadID);
		if ((p = m_locateService.queryPositionFromMeasurements(_measurements, m_epsilon)) != null)
		{
			/*
			 * Sets the server's response to the mobile content with the
			 * position.
			 */
			m_mobileResponse = p.getX() + ";" + p.getY();
			return true;
		} else
			return false;
	}
}