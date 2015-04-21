package fr.utbm.lo53.wifipositioning.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LocateController {

	/**
	 * @param request
	 * @return
	 */
	@RequestMapping("/locate")
	@ResponseBody
	public boolean locate(HttpServletRequest request){
		return true;
	}
}
