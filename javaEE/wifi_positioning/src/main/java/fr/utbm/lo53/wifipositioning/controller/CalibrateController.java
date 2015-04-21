package fr.utbm.lo53.wifipositioning.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Class designed to control the information given as parameters in the browser by the user<br>
 * There is one entry point : /calibrate <br>
 * After controlling the parameters, it sends the response "OK" if all the informations are informed
 */
@Controller
public class CalibrateController {

	/**
	 * Allows you to 
	 * @param request
	 * @return "OK" if all the parameters are informed else it returns a exception
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	@RequestMapping("/calibrate")
	@ResponseBody
	public String calibrate(HttpServletRequest request) throws IOException, IllegalArgumentException {
		String ap_id = request.getParameter("ap_id");
		String tel_id = request.getParameter("tel_id");
		
		//Verify that the data sent are not null or empty
		if(ap_id == null || ap_id.isEmpty()) {
			throw new IllegalArgumentException("ID's Access Point is invalid! ");
		}
		if(tel_id == null || tel_id.isEmpty()) {
			throw new IllegalArgumentException("ID's mobile phone is invalid! ");
		}
		if(request.getParameter("strength") == null) {
			throw new IllegalArgumentException("The strength of the signal is invalid! ");
		}
		if(request.getParameter("x") == null || request.getParameter("x").isEmpty()) {
			throw new IllegalArgumentException("The cordonates x is invalid! ");
		}
		if(request.getParameter("y") == null || request.getParameter("y").isEmpty()) {
			throw new IllegalArgumentException("The cordonates y is invalid! ");
		}
		
		double strength = Double.parseDouble(request.getParameter("strength"));
		int x = -1, y = -1;
		try {
			x = Integer.parseInt(request.getParameter("x"));
			y = Integer.parseInt(request.getParameter("y"));
		} catch (NumberFormatException nfe) {
			System.out.println("Wrong parameters. You must use x and y as integer parameters to this request!");
		}
		System.out.println("ID's AP : "+ap_id + "\tID's mobile phone : "+ tel_id + "\tStrength : "+strength + "\tX = "+x + "\tY = "+y);
//		String s = request.getHeaderNames().nextElement();
//		request.getHeader(s);
		return "OK"; 
	}
	
	@ResponseBody
	@ExceptionHandler(Exception.class)
	public String handleAllException(Exception ex) {
		String error = new String("Exception : " + ex.getMessage());
		return error;
	}
}
