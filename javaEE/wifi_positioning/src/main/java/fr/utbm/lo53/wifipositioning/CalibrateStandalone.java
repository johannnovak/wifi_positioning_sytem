package fr.utbm.lo53.wifipositioning;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.utbm.lo53.wifipositioning.controller.CalibrateController;

/**
 * Class only designed to have its main launched. It creates a new
 * {@link CalibrateController} and makes it listen.
 * 
 * @author jnovak
 *
 */
public class CalibrateStandalone
{
	/** Logger of the class */
	private final static Logger	s_logger	= LoggerFactory.getLogger(CalibrateStandalone.class);

	/* --------------------------------------------------------------------- */

	/**
	 * Method used to create a new {@link CalibrateController} and to make it
	 * listen to any new entering connections.
	 * 
	 * @param args
	 *            No args.
	 */
	public static void main(
			final String[] args)
	{
		s_logger.info("Launching the CalibrateController...");
		CalibrateController calibrateController = new CalibrateController();
		calibrateController.listen();
	}
}