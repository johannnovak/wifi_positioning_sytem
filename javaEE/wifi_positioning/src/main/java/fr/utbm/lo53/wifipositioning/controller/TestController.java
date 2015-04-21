package fr.utbm.lo53.wifipositioning.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import fr.utbm.lo53.wifipositioning.model.StrengthCoordinates;
import fr.utbm.lo53.wifipositioning.service.GeneralService;

/*
 * @Controller précise que la classe est considérée comme un contrôleur dans l'architecture SOA
 */
@Controller
public class TestController{

	/*
	 * @Autowire précise que l'objet est directement instancié par le bean
	 * (dans mvc-dispatcher-servlet.xml) dont l'id est "testService"
	 */
	@Autowired
	private GeneralService	generalService;

	/**
	 * Test method used to < return a string. Endpoint is
	 * "http://localhost:8080/wifi_positioning/test"
	 * </br>
	 * 
	 * @Responsebody precise that an object is returned (not a web page)
	 */
	@RequestMapping("/test/{id}")
	@ResponseBody
	public String test(	@PathVariable("id") final int id)	{
		System.out.println(generalService.getStrengthCoordinatesFromRawID(id));
		return new String(generalService.getStrengthCoordinatesFromRawID(id).toString());
	}
	
	@RequestMapping("/insert")
	@ResponseBody
	public void fill (){
			for(int i = 0; i< 4 ; i++){
				for(int j = 0; j< 10 ; j++){
					for(int k = 0 ; k< 3 ; k++){
						StrengthCoordinates strength = new StrengthCoordinates("1","test"+k, i, j, (Math.random()*4)+1);
						generalService.insertInto(strength );
					}
				}
			}
	}
	
	@RequestMapping("/delete")
	@ResponseBody
	public void clearTable(){
		generalService.clearTable();
	}
}
