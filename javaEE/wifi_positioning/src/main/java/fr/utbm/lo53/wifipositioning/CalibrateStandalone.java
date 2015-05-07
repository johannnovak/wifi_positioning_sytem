package fr.utbm.lo53.wifipositioning;

import fr.utbm.lo53.wifipositioning.controller.CalibrateController;

public class CalibrateStandalone
{
	public static void main(
			final String[] args)
	{
		CalibrateController calibrateController = new CalibrateController();
		calibrateController.listen();
	}
}
