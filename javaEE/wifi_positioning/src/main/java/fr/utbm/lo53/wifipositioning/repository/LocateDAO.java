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
import fr.utbm.lo53.wifipositioning.util.HibernateUtil;

/**
 * @Repository précise que c'est une classe de DAO, de requêtage de bdd.
 * @Transactionnal précise (sûrement) que la classe effectue des transactions
 *                 entre l'appli et la bdd (genre session.beginTransaction()
 *                 sûrement
 */
public class LocateDAO
{
	/** Logger of the class */
	private final static Logger		s_logger	= LoggerFactory.getLogger(LocateDAO.class);

	private static LocateDAO		s_locateDAO;
	private final SessionFactory	m_sessionFactory;

	private LocateDAO()
	{
		m_sessionFactory = HibernateUtil.getSessionfactory();
	}

	public synchronized static LocateDAO getInstance()
	{
		return s_locateDAO;
	}

	public synchronized Position queryPositionFromMeasurements(
			final Set<Measurement> _measurements,
			final float _epsilon)
	{
		Session session = m_sessionFactory.getCurrentSession();
		try
		{
			session.beginTransaction();

			String hqlQueryString = "SELECT p FROM Measurement m join Position p where ";
			for (Measurement m : _measurements)
				hqlQueryString += "m.rssi + epsilon > " + m.getRssi() + " and m.rssi - epsilon < "
						+ m.getRssi() + " and ";
			hqlQueryString = hqlQueryString.substring(0, hqlQueryString.length() - 3);
			Query hqlQuery = session.createQuery(hqlQueryString);

			@SuppressWarnings("unchecked")
			List<Position> matchingPositions = hqlQuery.list();

			if (matchingPositions.size() > 1)
			{
				s_logger.error("Error when querying database for locate. Number of position got from query > 1.");
				return null;
			}

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
