package com.acm.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@Controller
@RequestMapping({"/", "/home"})
public class HomeController {
	
	@RequestMapping(method=GET)
	public String home() {
		return "home";
	}
	
}
