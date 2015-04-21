package fr.utbm.lo53.wifipositioning.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.utbm.lo53.wifipositioning.model.StrengthCoordinates;
import fr.utbm.lo53.wifipositioning.repository.GeneralDAOImpl;

/**
 * @Service précise que c'est une classe de Service.
 */
@Service
public class GeneralService{
	@Autowired
	private GeneralDAOImpl	generalDAO;

	public StrengthCoordinates getStrengthCoordinatesFromRawID(	final int id){
		return generalDAO.select(id);
	}

	public void insertInto(StrengthCoordinates strength) {
		generalDAO.insertInto(strength);
	}
}
