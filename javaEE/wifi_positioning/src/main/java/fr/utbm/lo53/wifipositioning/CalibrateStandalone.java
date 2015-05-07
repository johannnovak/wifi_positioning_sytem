package fr.utbm.lo53.wifipositioning;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.utbm.lo53.wifipositioning.controller.CalibrateController;

public class CalibrateStandalone
{
	/** Logger of the class */
	private final static Logger	s_logger	= LoggerFactory.getLogger(CalibrateStandalone.class);

	public static void main(
			final String[] args)
	{
		s_logger.info("Launching the CalibrateController...");
		CalibrateController calibrateController = new CalibrateController();
		calibrateController.listen();
	}
}
