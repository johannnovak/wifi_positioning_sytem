package fr.utbm.lo53.wifipositioning.controller;

import fr.utbm.lo53.wifipositioning.controller.runnable.CalibrateRunnable;

/**
 * Class designed to control the information given as parameters in the browser
 * by the user<br>
 * There is one entry point : /calibrate <br>
 * After controlling the parameters, it sends the response "OK" if all the
 * informations are informed
 */
public class CalibrateController extends SocketController<CalibrateRunnable>
{
	public CalibrateController()
	{
		super("calibrate.port");

		m_controllerName = this.getClass().getSimpleName();
	}
}