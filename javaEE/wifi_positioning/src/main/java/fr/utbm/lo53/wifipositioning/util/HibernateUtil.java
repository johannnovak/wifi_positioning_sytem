package fr.utbm.lo53.wifipositioning.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil
{
	private static final SessionFactory	sessionFactory	= buildSessionFactory();

	private static SessionFactory buildSessionFactory()
	{
		return new Configuration().configure().buildSessionFactory();
	}

	public static synchronized SessionFactory getSessionfactory()
	{
		return sessionFactory;
	}
}
