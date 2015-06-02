package fr.utbm.lo53.wifipositioning.repository;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.utbm.lo53.wifipositioning.model.Measurement;
import fr.utbm.lo53.wifipositioning.model.Position;
import fr.utbm.lo53.wifipositioning.service.CalibrateService;
import fr.utbm.lo53.wifipositioning.util.HibernateUtil;

/**
 * DAO Class.<br>
 * Class designed to communicate with the database. Only used by the
 * {@link CalibrateService}.
 * 
 * @author jnovak
 *
 */
public class CalibrateDAO
{
	/** Logger of the class */
	private final static Logger		s_logger		= LoggerFactory.getLogger(CalibrateDAO.class);

	/** Singleton of the class. */
	private static CalibrateDAO		s_calibrateDAO	= new CalibrateDAO();

	/** Singleton of the Hibernate's {@link SessionFactory}. */
	private final SessionFactory	m_sessionFactory;

	/* --------------------------------------------------------------------- */

	/**
	 * Default Constructor that instantiates the {@link SessionFactory}
	 * attribute.
	 */
	private CalibrateDAO()
	{
		m_sessionFactory = HibernateUtil.getSessionfactory();
	}

	/* --------------------------------------------------------------------- */

	/**
	 * @return Instance of the {@link CalibrateDAO} singleton.
	 */
	public synchronized static CalibrateDAO getInstance()
	{
		return s_calibrateDAO;
	}

	/* --------------------------------------------------------------------- */

	/**
	 * Synchronized method used to insert a new Position inside the database
	 * (alongside its associated set of {@link Measurement}).
	 * 
	 * @param _position
	 *            Position to insert inside the Database.
	 * @return True if no errors have been encountered.<br>
	 *         False otherwise.
	 */
	public synchronized boolean insertSample(
			final Position _position)
	{
		s_logger.debug("CalibrateDAO inserting new Measurement-Position...");

		/* Gets the session from the SessionFactory. */
		Session session = m_sessionFactory.getCurrentSession();
		try
		{
			/* Begins the transaction of data. */
			session.beginTransaction();

			/* Query to see if the position already exists. */
			Query hqlQuery = session.createQuery("FROM Position where x = :x and y = :y");
			hqlQuery.setParameter("x", _position.getX());
			hqlQuery.setParameter("y", _position.getY());

			@SuppressWarnings("unchecked")
			List<Object> posList = hqlQuery.list();

			/*
			 * If it doesn't exist, we save the new position and its associated
			 * measurement.
			 */
			if (posList.isEmpty())
			{
				s_logger.debug("The coordinates (x,y) have not been found in the database.");
				session.save(_position);

				/*
				 * The position already exists, so we only want to add/update
				 * the measurement in the database.
				 */
			} else
			{
				s_logger.debug("The coordinates (x,y) have been found in the database.");

				hqlQuery = session
						.createQuery("SELECT m FROM Measurement m join m.position p where p.x = :x and p.y = :y");
				hqlQuery.setParameter("x", _position.getX());
				hqlQuery.setParameter("y", _position.getY());

				@SuppressWarnings("unchecked")
				List<Object> rssiList = hqlQuery.list();
				Measurement requestMeasurement = _position.getMeasurements().iterator().next();

				/*
				 * If there is no mac address associated with the (x,y) => add
				 * new measurement
				 */
				if (rssiList.isEmpty())
				{
					requestMeasurement.setPosition((Position) posList.get(0));
					session.save(requestMeasurement);

					/*
					 * If there is already a macAddress for the (x,y) => update
					 * rssi
					 */
				} else
				{
					Measurement m = (Measurement) rssiList.get(0);
					m.setRssi(requestMeasurement.getRssi());
					session.update(m);
				}
			}

			/*
			 * There hasn' been any errors, we can safely commit our database
			 * updates.
			 */
			session.getTransaction().commit();

			s_logger.debug("Insertion in the database successful.");
			return true;
		} catch (HibernateException he)
		{
			he.printStackTrace();
			if (session.getTransaction() != null)
			{
				try
				{
					session.getTransaction().rollback();
				} catch (HibernateException he2)
				{
					he2.printStackTrace();
				}
			}
		} finally
		{
			if ((session != null) && session.isOpen())
			{
				session.close();
			}
		}
		return false;
	}
}