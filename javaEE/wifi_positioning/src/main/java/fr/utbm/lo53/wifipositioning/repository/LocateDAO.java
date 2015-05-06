package fr.utbm.lo53.wifipositioning.repository;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import fr.utbm.lo53.wifipositioning.util.HibernateUtil;

/**
 * @Repository précise que c'est une classe de DAO, de requêtage de bdd.
 * @Transactionnal précise (sûrement) que la classe effectue des transactions
 *                 entre l'appli et la bdd (genre session.beginTransaction()
 *                 sûrement
 */
public class LocateDAO
{
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

	public synchronized void getPosition()
	{
		Session session = m_sessionFactory.getCurrentSession();
		try
		{
			session.beginTransaction();
			// session.saveOrUpdate(_measurement);
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
