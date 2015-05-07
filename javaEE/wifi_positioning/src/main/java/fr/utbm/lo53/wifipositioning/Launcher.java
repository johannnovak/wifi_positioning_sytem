package fr.utbm.lo53.wifipositioning;

import java.io.IOException;
import java.util.Properties;

public class Launcher
{
	public static void main(
			final String[] args) throws IOException
	{
		/* Loading of properties */
		Properties properties = new Properties();
		properties.load(Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("server.properties"));

		/* Retrieves the overall properties */
		System.setProperty("mac.address.byte.length",
				properties.getProperty("mac.address.byte.length"));
		System.setProperty("position.byte.length", properties.getProperty("position.byte.length"));
		System.setProperty("rssi.byte.length", properties.getProperty("rssi.byte.length"));

		/* Retrieves the properties concerning the calibration */
		System.setProperty("calibrate.port", properties.getProperty("calibrate.port"));
		System.setProperty("calibrate.packet.offset",
				properties.getProperty("calibrate.packet.offset"));

		/* Retrieves the properties concerning the location */
		System.setProperty("locate.port", properties.getProperty("locate.port"));
		System.setProperty("locate.packet.offset", properties.getProperty("locate.packet.offset"));

		/* Runs standalone controllers */
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				CalibrateStandalone.main(null);
			}
		}).start();

		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				LocateStandalone.main(null);
			}
		}).start();
	}
}
