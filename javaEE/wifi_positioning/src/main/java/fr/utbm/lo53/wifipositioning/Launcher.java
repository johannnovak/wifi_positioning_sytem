package fr.utbm.lo53.wifipositioning;

import java.io.IOException;
import java.util.Properties;

import fr.utbm.lo53.wifipositioning.controller.CalibrateController;

public class Launcher
{
	public static void main(
			final String[] args) throws ClassNotFoundException, IOException
	{
		Properties properties = new Properties();
		properties.load(Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("server.properties"));

		int packetOffset = Integer.parseInt(properties.getProperty("calibrate.packet.offset"));
		int macAddressByteLength = Integer.parseInt(properties
				.getProperty("mac.address.byte.length"));
		int positionByteLength = Integer
				.parseInt(properties.getProperty("mac.address.byte.length"));
		int rssiByteLength = Integer.parseInt(properties.getProperty("mac.address.byte.length"));

		int calibratePort = Integer.parseInt(properties.getProperty("calibrate.port"));

		CalibrateController calibrateController = new CalibrateController(calibratePort,
				packetOffset, macAddressByteLength, positionByteLength, rssiByteLength);
		calibrateController.listen();
	}
}
