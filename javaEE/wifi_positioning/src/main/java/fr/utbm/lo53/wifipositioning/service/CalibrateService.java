package fr.utbm.lo53.wifipositioning.service;

import fr.utbm.lo53.wifipositioning.model.Measurement;
import fr.utbm.lo53.wifipositioning.repository.CalibrateDAO;

public class CalibrateService
{

	private final static CalibrateService	s_calibrateService	= new CalibrateService();

	private final CalibrateDAO			m_generalDaoImpl;

	private CalibrateService()
	{
		m_generalDaoImpl = CalibrateDAO.getInstance();
	}

	public synchronized static CalibrateService getInstance()
	{
		return s_calibrateService;
	}

	public synchronized void insertMeasurement(
			final Measurement _measurement)
	{
		m_generalDaoImpl.insertMeasurement(_measurement);
	}

}
