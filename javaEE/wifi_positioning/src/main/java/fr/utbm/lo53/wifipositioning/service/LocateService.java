package fr.utbm.lo53.wifipositioning.service;

import fr.utbm.lo53.wifipositioning.repository.LocateDAO;

public class LocateService
{
	/** Instance of the singleton */
	private final static LocateService	s_locateService	= new LocateService();

	private final LocateDAO				m_locateDAO;

	/** Default private constructor called only once */
	private LocateService()
	{
		m_locateDAO = LocateDAO.getInstance();
	}

	/**
	 * @return Instance of the singleton.
	 */
	public static LocateService getInstance()
	{
		return s_locateService;
	}

	public void getPosition()
	{
		m_locateDAO.getPosition();
	}
}
