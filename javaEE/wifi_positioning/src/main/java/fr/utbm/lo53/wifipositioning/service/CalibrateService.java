package fr.utbm.lo53.wifipositioning.service;

import fr.utbm.lo53.wifipositioning.model.Position;
import fr.utbm.lo53.wifipositioning.repository.CalibrateDAO;

public class CalibrateService
{

	private final static CalibrateService	s_calibrateService	= new CalibrateService();

	private final CalibrateDAO				m_CalibrateDAO;

	private CalibrateService()
	{
		m_CalibrateDAO = CalibrateDAO.getInstance();
	}

	public synchronized static CalibrateService getInstance()
	{
		return s_calibrateService;
	}

	public synchronized boolean insertSample(
			final Position _position)
	{
		return m_CalibrateDAO.insertSample(_position);
	}

}
