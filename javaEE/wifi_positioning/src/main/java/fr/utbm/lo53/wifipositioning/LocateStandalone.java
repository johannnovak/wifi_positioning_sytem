package fr.utbm.lo53.wifipositioning;

import fr.utbm.lo53.wifipositioning.controller.LocateController;

public class LocateStandalone
{
	public static void main(
			final String[] args)
	{
		LocateController locateController = new LocateController();
		locateController.listen();
	}
}
