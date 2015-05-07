package fr.utbm.lo53.wifipositioning.controller;

import fr.utbm.lo53.wifipositioning.controller.runnable.LocateRunnable;

public class LocateController extends SocketController<LocateRunnable>
{

	public LocateController()
	{
		super("locate.port");

		m_controllerName = this.getClass().getSimpleName();
	}
}
