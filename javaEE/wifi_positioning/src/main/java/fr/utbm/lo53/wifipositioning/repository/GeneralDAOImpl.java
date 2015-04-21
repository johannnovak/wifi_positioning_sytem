package fr.utbm.lo53.wifipositioning.repository;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import fr.utbm.lo53.wifipositioning.model.StrengthCoordinates;

/**
 * @Repository précise que c'est une classe de DAO, de requêtage de bdd.
 * @Transactionnal précise (sûrement) que la classe effectue des transactions
 *                 entre l'appli et la bdd (genre session.beginTransaction()
 *                 sûrement
 */
@Repository
@Transactional
public class GeneralDAOImpl {
	@Autowired
	private SessionFactory sessionFactory;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(final SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public StrengthCoordinates select(final int id) {
		Session session = getSessionFactory().getCurrentSession();
		session.beginTransaction();
		StrengthCoordinates persons = (StrengthCoordinates) session.get(
				StrengthCoordinates.class, id);
		session.getTransaction().commit();
		return persons;
	}

	public void clearTable() {
		Session session = getSessionFactory().getCurrentSession();
		session.beginTransaction();
		String hql = "DELETE FROM StrengthCoordinates";
		Query q = session.createQuery(hql);
		q.executeUpdate();
		session.getTransaction().commit();
	}

	public void insertInto(final StrengthCoordinates _strengthCoordinates) {
		Session session = getSessionFactory().getCurrentSession();
		session.beginTransaction();
		session.saveOrUpdate(_strengthCoordinates);
		session.getTransaction().commit();
	}
}
