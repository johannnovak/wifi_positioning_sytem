package fr.utbm.lo53.wifipositioning.service;

import fr.utbm.lo53.wifipositioning.controller.CalibrateController;
import fr.utbm.lo53.wifipositioning.model.Position;
import fr.utbm.lo53.wifipositioning.repository.CalibrateDAO;

/**
 * Service Class.<br>
 * Class designed to communicate with the {@link CalibrateDAO}. Only used by the
 * {@link CalibrateController}.
 * 
 * @author jnovak
 *
 */
public class CalibrateService
{
	/** Singleton of the class. */
	private final static CalibrateService	s_calibrateService	= new CalibrateService();

	/**
	 * {@link CalibrateDAO} to use when method are called by the
	 * {@link CalibrateController}
	 */
	private final CalibrateDAO				m_CalibrateDAO;

	/* --------------------------------------------------------------------- */

	/**
	 * Default constructor that assign a new Instance to the 's_calibrateDAO'
	 * attribute.
	 */
	private CalibrateService()
	{
		m_CalibrateDAO = CalibrateDAO.getInstance();
	}

	/* --------------------------------------------------------------------- */

	/**
	 * @return Instance of the {@link CalibrateService}.
	 */
	public synchronized static CalibrateService getInstance()
	{
		return s_calibrateService;
	}

	/* --------------------------------------------------------------------- */

	/**
	 * Asks the {@link CalibrateDAO} to insert a new Sample inside the database.
	 * 
	 * @param _position
	 *            {@link Position} to insert inside the database.
	 * @return True if no errors have occurred when accessing the database.<br>
	 *         False otherwise.
	 */
	public synchronized boolean insertSample(
			final Position _position)
	{
		return m_CalibrateDAO.insertSample(_position);
	}

}