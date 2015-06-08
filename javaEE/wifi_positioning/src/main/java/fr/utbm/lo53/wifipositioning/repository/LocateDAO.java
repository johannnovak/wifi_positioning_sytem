package fr.utbm.lo53.wifipositioning.repository;

import java.util.ArrayList;
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

			List<Position> matchingPositionsList = new ArrayList<Position>();
			int maxLoop = 5;
			int currentLoop = 0;
			float epsilonTemp = _epsilon;

			while ((currentLoop > maxLoop) || matchingPositionsList.isEmpty()
					|| (matchingPositionsList.size() > 1))
			{
				for (Measurement m : _measurements)
				{
					String hqlQueryString = "SELECT p FROM Measurement m join m.position p where m.macAddress='"
							+ m.getMacAddress()
							+ "' and m.rssi + :epsilon > "
							+ m.getRssi()
							+ " and m.rssi - :epsilon < " + m.getRssi();

					Query hqlQuery = session.createQuery(hqlQueryString);
					hqlQuery.setParameter("epsilon", epsilonTemp);

					s_logger.debug("Executing query : '{}'.", hqlQuery.getQueryString());

					/* Gets the different obtained Positions. */
					@SuppressWarnings("unchecked")
					List<Position> resultList = hqlQuery.list();

					if (!matchingPositionsList.isEmpty())
						resultList.retainAll(matchingPositionsList);
					matchingPositionsList = resultList;
				}

				if (matchingPositionsList.isEmpty())
					epsilonTemp *= 1.10;
				else if (matchingPositionsList.size() > 1)
					epsilonTemp *= 0.9;
				++currentLoop;
			}

			/* If not only one Position has been found, error in threshold. */
			if (matchingPositionsList.isEmpty())
			{
				s_logger.error("Error when querying database for locate. No positions found.");
				return null;
			} else if (matchingPositionsList.size() != 1)
			{
				s_logger.error("Error when querying database for locate. Number of position got from query > 1.");
				s_logger.error("-> {}", matchingPositionsList);
				return null;
			}
			/*
			 * No errors, we can safely commit our database
			 * updates.
			 */
			session.getTransaction().commit();

			Position position = matchingPositionsList.get(0);

			s_logger.debug("Position match : {}", position);

			return position;
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
				session.close();
		}
		return null;
	}
}