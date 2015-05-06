package fr.utbm.lo53.wifipositioning.repository;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import fr.utbm.lo53.wifipositioning.model.Measurement;
import fr.utbm.lo53.wifipositioning.util.HibernateUtil;

/**
 * @Repository précise que c'est une classe de DAO, de requêtage de bdd.
 * @Transactionnal précise (sûrement) que la classe effectue des transactions
 *                 entre l'appli et la bdd (genre session.beginTransaction()
 *                 sûrement
 */
public class CalibrateDAO
{
	private static CalibrateDAO	s_calibrateDAO;
	private final SessionFactory	m_sessionFactory;

	private CalibrateDAO()
	{
		m_sessionFactory = HibernateUtil.getSessionfactory();
	}

	public synchronized static CalibrateDAO getInstance()
	{
		return s_calibrateDAO;
	}

	public synchronized void insertMeasurement(
			final Measurement _measurement)
	{
		Session session = m_sessionFactory.getCurrentSession();
		try
		{
			session.beginTransaction();
			session.saveOrUpdate(_measurement);
			session.getTransaction().commit();
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
	}

}
