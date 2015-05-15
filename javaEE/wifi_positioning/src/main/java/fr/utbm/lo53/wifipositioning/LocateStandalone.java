package fr.utbm.lo53.wifipositioning;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.utbm.lo53.wifipositioning.controller.LocateController;

/**
 * Class only designed to have its main launched. It creates a new
 * {@link LocateController} and makes it listen.
 * 
 * @author jnovak
 *
 */
public class LocateStandalone
{
	/** Logger of the class */
	private final static Logger	s_logger	= LoggerFactory.getLogger(LocateStandalone.class);

	/* --------------------------------------------------------------------- */

	/**
	 * Method used to create a new {@link LocateController} and to make it
	 * listen to any new entering connections.
	 * 
	 * @param args
	 *            No args.
	 */
	public static void main(
			final String[] args)
	{
		s_logger.info("Launching the LocateController...");
		LocateController locateController = new LocateController();
		locateController.listen();
	}
}