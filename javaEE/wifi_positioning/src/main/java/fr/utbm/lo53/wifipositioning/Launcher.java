package fr.utbm.lo53.wifipositioning;

import java.io.IOException;
import java.util.Properties;

import fr.utbm.lo53.wifipositioning.controller.CalibrateController;

public class Launcher
{
	public static void main(
			final String[] args) throws ClassNotFoundException, IOException
	{
		/* Loading of properties */
		Properties properties = new Properties();
		properties.load(Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("server.properties"));

		/* Retrieves the overall properties */
		int packetOffset = Integer.parseInt(properties.getProperty("calibrate.packet.offset"));
		int macAddressByteLength = Integer.parseInt(properties
				.getProperty("mac.address.byte.length"));
		int positionByteLength = Integer
				.parseInt(properties.getProperty("mac.address.byte.length"));
		int rssiByteLength = Integer.parseInt(properties.getProperty("mac.address.byte.length"));

		/* Retrieves the properties concerning the calibration */
		int calibratePort = Integer.parseInt(properties.getProperty("calibrate.port"));

		/* Retrieves the properties concerning the location */

		/* Creates the two controllers */
		CalibrateController calibrateController = new CalibrateController(calibratePort,
				packetOffset, macAddressByteLength, positionByteLength, rssiByteLength);
		// LocateController locateController = new LocateController(locatePort)

		/* Controllers starts to listen to their port */
		calibrateController.listen();
	}
}
