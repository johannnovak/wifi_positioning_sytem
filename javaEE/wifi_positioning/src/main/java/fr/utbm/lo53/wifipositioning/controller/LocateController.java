package fr.utbm.lo53.wifipositioning.controller;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.utbm.lo53.wifipositioning.model.Measurement;
import fr.utbm.lo53.wifipositioning.model.Position;
import fr.utbm.lo53.wifipositioning.service.LocateService;

/**
 * Class extending from {@link SocketController}.<br>
 * Specialized class needed to construct the {@link SocketController}
 * constructor with specific parameters.
 * 
 * @author jnovak
 *
 */
public class LocateController extends SocketController
{
	/** Logger of the class */
	private final static Logger	s_logger	= LoggerFactory.getLogger(LocateController.class);

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
	 * Calls the {@link SocketController} constructor with the Property String
	 * "locate.port" and sets the 'runnableName' to this classes' simple
	 * name.
	 */
	public LocateController()
	{
		super("locate.port");

		m_controllerName = this.getClass().getSimpleName();
		m_locateService = LocateService.getInstance();
		m_epsilon = Float.parseFloat(System.getProperty("locate.rssi.epsilon"));

		s_logger.debug("LocateController created.");
	}

	/* --------------------------------------------------------------------- */

	/**
	 * Method used to parse the
	 * inputstream of a {@link SocketChannel} and return a List of Object
	 * containing the necessary informations.
	 * 
	 * @param _key
	 *            {@link SelectionKey} referencing on the {@link SocketChannel}
	 *            whose inputstream is read.
	 * @return List of object where necessary parsed informations are stored :<br>
	 *         - index 0 : Mobile phone's MacAddress;<br>
	 */
	@Override
	protected List<Object> parseMobileData(
			final SelectionKey _key)
	{
		s_logger.debug("Parsing mobile socket connection data for calibration.");

		try
		{
			String data = "";
			List<Object> requestData = new ArrayList<Object>();

			/* Creates a ByteBuffer of 25 : MacAddress => 17. */
			ByteBuffer buffer = ByteBuffer.allocate(17);

			/* Gets back the client channel. */
			SocketChannel clientSocketChannel = (SocketChannel) _key.channel();

			/* Reads the channel inputStream. */
			if (clientSocketChannel.read(buffer) > 0)
			{
				/* Flip in order to decode the buffer. */
				buffer.flip();
				data += Charset.defaultCharset().decode(buffer).toString();
				buffer.clear();
			}

			/* Adds the mobile MacAddress. */
			requestData.add(data);

			s_logger.debug("Data parsed : {}.", data);

			return requestData;

		} catch (Exception e)
		{
			s_logger.error("An error occured when parsing the mobile request data.", e);
		} finally
		{
			s_logger.debug("Request parsing over.");
		}
		return null;
	}

	/**
	 * Method used to query a {@link Position} from the database.
	 * 
	 * @param _mobileRequestData
	 *            Not used, can be set to null.
	 * @param _rssiMeasurement
	 *            Set of {@link Measurement} associated with a coordinate (x,y).
	 * @return True if no errors have occurred when accessing the database.<br>
	 *         False otherwise.
	 */
	@Override
	protected boolean accessDatabase(
			final List<Object> _mobileRequestData,
			final Set<Measurement> _rssiMeasurements)
	{
		return locate(_rssiMeasurements);
	}

	/* --------------------------------------------------------------------- */

	/**
	 * Method used to call the {@link LocateService} class (form the Service
	 * layer) to retrieve a {@link Position} from a Set of {@link Measurement}
	 * from the database. If everything went fine, the mobileResponse is set
	 * with the String "{x};{y}" position.
	 * 
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
		s_logger.debug("Retrieving position from database.");
		if ((p = m_locateService.queryPositionFromMeasurements(_measurements, m_epsilon)) != null)
		{
			/*
			 * Sets the server's response to the mobile content with the
			 * position.
			 */
			m_clientResponse = p.getX() + ";" + p.getY();
			return true;
		} else
			return false;
	}
}