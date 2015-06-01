package fr.utbm.lo53.wifipositioning.util;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

/**
 * Static class designed to be able to perform operations on files.
 * 
 * @author jnovak
 *
 */
public class FilesUtils
{

	/** Logger of the class */
	private final static Logger	s_logger	= LoggerFactory.getLogger(FilesUtils.class);

	/* --------------------------------------------------------------------- */

	/**
	 * Method used to initialize the log file.
	 * 
	 * @throws JoranException
	 */
	public static void initLogger() throws JoranException
	{
		JoranConfigurator configurator = new JoranConfigurator();
		LoggerContext c = (LoggerContext) LoggerFactory.getILoggerFactory();
		c.reset();
		configurator.setContext(c);

		URL logbackFileURL = FilesUtils.class.getClassLoader()
				.getResource("log/logback_server.xml");
		configurator.doConfigure(logbackFileURL);

		s_logger.debug("Log file initialized.");
	}
}