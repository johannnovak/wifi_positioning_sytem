package fr.utbm.lo53.wifipositioning.service;

import java.util.Set;

import fr.utbm.lo53.wifipositioning.controller.LocateController;
import fr.utbm.lo53.wifipositioning.model.Measurement;
import fr.utbm.lo53.wifipositioning.model.Position;
import fr.utbm.lo53.wifipositioning.repository.LocateDAO;

/**
 * Service Class.<br>
 * Class designed to communicate with the {@link LocateDAO}. Only used by the
 * {@link LocateController}.
 * 
 * @author jnovak
 *
 */
public class LocateService
{
	/** Instance of the singleton */
	private final static LocateService	s_locateService	= new LocateService();

	/**
	 * {@link LocateDAO} to use when method are called by the
	 * {@link LocateController}
	 */
	private final LocateDAO				m_locateDAO;

	/* --------------------------------------------------------------------- */

	/** Default private constructor called only once */
	private LocateService()
	{
		m_locateDAO = LocateDAO.getInstance();
	}

	/* --------------------------------------------------------------------- */

	/**
	 * @return Instance of the singleton.
	 */
	public static LocateService getInstance()
	{
		return s_locateService;
	}

	/* --------------------------------------------------------------------- */

	/**
	 * Asks the {@link LocateDAO} to get a {@link Position} from a Set of
	 * {@link Measurement} from the database.
	 * 
	 * @param _measurements
	 *            Set of {@link Measurement} needed to get a {@link Position}.
	 * @param _epsilon
	 *            Threshold needed to evaluate the RSSIs.
	 * @return A {@link Position} if one have been found.<br>
	 *         Null otherwise.
	 */
	public Position queryPositionFromMeasurements(
			final Set<Measurement> _measurements,
			final float _epsilon)
	{
		return m_locateDAO.queryPositionFromMeasurements(_measurements, _epsilon);
	}
}