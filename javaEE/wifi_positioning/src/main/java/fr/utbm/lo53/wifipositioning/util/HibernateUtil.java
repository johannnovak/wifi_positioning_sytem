package fr.utbm.lo53.wifipositioning.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

/**
 * Static class designed to return a {@link SessionFactory}.
 * 
 * @author jnovak
 *
 */
public class HibernateUtil
{
	/** {@link SessionFactory} singleton that is created */
	private static final SessionFactory	sessionFactory	= buildSessionFactory();

	/* --------------------------------------------------------------------- */

	/**
	 * Creates a new SessionFactory from the Hibernate Configuration.
	 * 
	 * @return {@link SessionFactory} created from Hibernate configuration.
	 */
	private static SessionFactory buildSessionFactory()
	{
		Configuration configuration = new Configuration();
		configuration.configure();
		ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(
				configuration.getProperties()).buildServiceRegistry();
		return configuration.buildSessionFactory(serviceRegistry);
	}

	/* --------------------------------------------------------------------- */

	/**
	 * @return {@link SessionFactory} to use to access to database via
	 *         Hibernate. Synchronized method.
	 */
	public static synchronized SessionFactory getSessionfactory()
	{
		return sessionFactory;
	}
}
