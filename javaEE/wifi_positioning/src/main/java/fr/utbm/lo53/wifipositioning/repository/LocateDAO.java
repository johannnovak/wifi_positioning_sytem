package fr.utbm.lo53.wifipositioning.repository;

import java.util.List;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.utbm.lo53.wifipositioning.model.Measurement;
import fr.utbm.lo53.wifipositioning.model.Position;
import fr.utbm.lo53.wifipositioning.service.LocateService;
import fr.utbm.lo53.wifipositioning.util.HibernateUtil;

/**
 * DAO Class.<br>
 * Class designed to communicate with the database. Only used by the
 * {@link LocateService}.
 * 
 * @author jnovak
 *
 */
public class LocateDAO
{
	/** Logger of the class */
	private final static Logger		s_logger	= LoggerFactory.getLogger(LocateDAO.class);

	/** Singleton of the class. */
	private static LocateDAO		s_locateDAO	= new LocateDAO();

	/** Singleton of the Hibernate's {@link SessionFactory}. */
	private final SessionFactory	m_sessionFactory;

	/* --------------------------------------------------------------------- */
	/**
	 * Default Constructor that instantiates the {@link SessionFactory}
	 * attribute.
	 */
	private LocateDAO()
	{
		m_sessionFactory = HibernateUtil.getSessionfactory();
	}

	/**
	 * @return Instance of the {@link LocateDAO} singleton.
	 */
	public synchronized static LocateDAO getInstance()
	{
		return s_locateDAO;
	}

	/* --------------------------------------------------------------------- */

	/**
	 * Method used to query a {@link Position} from the database from different
	 * {@link Measurement}.
	 * 
	 * @param _measurements
	 *            Set of {@link Measurement} that is used to retrieve a
	 *            {@link Position}
	 * @param _epsilon
	 *            Error threshold to take intoo consideration when testing RSSI.
	 * 
	 * @return A {@link Position} it has been found.<br>
	 *         Null otherwise.
	 */
	public synchronized Position queryPositionFromMeasurements(
			final Set<Measurement> _measurements,
			final float _epsilon)
	{
		/* Gets the session from the SessionFactory. */
		Session session = m_sessionFactory.getCurrentSession();
		try
		{
			/* Begins the transaction of data. */
			session.beginTransaction();

			/* Creates HQLQuery from the different Measurements. */
			String hqlQueryString = "SELECT p FROM Measurement m join m.position p where ";
			for (Measurement m : _measurements)
				hqlQueryString += "m.rssi + :epsilon > " + m.getRssi()
						+ " and m.rssi - :epsilon < " + m.getRssi() + " and ";
			/* to remove last 'and' */
			hqlQueryString = hqlQueryString.substring(0, hqlQueryString.length() - 4);
			Query hqlQuery = session.createQuery(hqlQueryString);
			hqlQuery.setParameter("epsilon", _epsilon);

			/* Gets the different obtained Positions. */
			@SuppressWarnings("unchecked")
			List<Position> matchingPositions = hqlQuery.list();

			/* If not only one Position has been found, error in threshold. */
			if (matchingPositions.size() > 1)
			{
				s_logger.error("Error when querying database for locate. Number of position got from query > 1.");
				return null;
			} else if (matchingPositions.isEmpty())
			{
				s_logger.error("Error when querying database for locate. No positions found.");
				return null;
			}
			/*
			 * No errors, we can safely commit our database
			 * updates.
			 */
			session.getTransaction().commit();

			return matchingPositions.get(0);
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
			if (session != null)
			{
				session.close();
			}
		}
		return null;
	}
}