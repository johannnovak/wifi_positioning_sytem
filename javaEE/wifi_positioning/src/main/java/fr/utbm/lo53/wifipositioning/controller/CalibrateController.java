package fr.utbm.lo53.wifipositioning.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.utbm.lo53.wifipositioning.controller.runnable.CalibrateRunnable;

/**
 * Class extending from {@link SocketController}.<br>
 * Specialized class needed to construct the {@link SocketController}
 * constructor with specific parameters.
 * 
 * @author jnovak
 *
 */
public class CalibrateController extends SocketController<CalibrateRunnable>
{
	/** Logger of the class */
	private final static Logger	s_logger	= LoggerFactory.getLogger(CalibrateController.class);

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

		s_logger.debug("CalibrateController created.");
	}
}