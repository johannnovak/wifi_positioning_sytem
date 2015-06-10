package fr.utbm.lo53.wifipositioning.controller;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.utbm.lo53.wifipositioning.model.Measurement;
import fr.utbm.lo53.wifipositioning.model.Position;
import fr.utbm.lo53.wifipositioning.service.CalibrateService;

/**
 * Class extending from {@link SocketController}.<br>
 * Specialized class needed to construct the {@link SocketController}
 * constructor with specific parameters. It implements the necessary abstract
 * methods in order to calibrate the mobile device, that is : inserting a
 * position associated with a measurement inside the database.
 * 
 * @author jnovak
 *
 */
public class CalibrateController extends SocketController
{
	/** Logger of the class */
	private final static Logger		s_logger	= LoggerFactory
														.getLogger(CalibrateController.class);

	/** CalibrateService used to access the database */
	private final CalibrateService	m_calibrateService;

	/* --------------------------------------------------------------------- */

	/**
	 * Default Constructor.<br>
	 * Calls the {@link SocketController} constructor with the Property String
	 * "calibrate.port" and sets the 'runnableName' to this classes' simple
	 * name.
	 */
	public CalibrateController()
	{
		super("calibrate.port");

		m_controllerName = this.getClass().getSimpleName();
		m_calibrateService = CalibrateService.getInstance();

		s_logger.debug("CalibrateController created.");
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
	 *         - index 1 : x position;<br>
	 *         - index 2 : y position.
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

			/* Creates a ByteBuffer of 25 : MacAddress => 17, x => 21, y => 25 . */
			ByteBuffer buffer = ByteBuffer.allocate(25);

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

			String[] splitData = data.split(";");

			/* Adds the mobile MacAddress. */
			requestData.add(splitData[0]);

			/* Adds the x position. */
			requestData.add(Float.parseFloat(splitData[1]));

			/* Adds the y position. */
			requestData.add(Float.parseFloat(splitData[2]));

			s_logger.debug("Data parsed : {}.", Arrays.asList(splitData));

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

	/* --------------------------------------------------------------------- */

	/**
	 * Method used to insert informations in the database.
	 * 
	 * @param _mobileRequestData
	 *            Data containing the position of the mobile phone.
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
		/* Casts the coordinates */
		float x = (Float) _mobileRequestData.get(1);
		float y = (Float) _mobileRequestData.get(2);

		return calibrate(x, y, _rssiMeasurements);
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
		s_logger.debug("Inserting following Position : {}", position);

		if (m_calibrateService.insertSample(position))
		{
			m_clientResponse = s_httpOKCode;
			return true;
		} else
			return false;
	}
}