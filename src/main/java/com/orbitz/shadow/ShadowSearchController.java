package com.orbitz.shadow;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShadowSearchController {
	
	@RequestMapping("/search")
	public String shadowsearch(@RequestParam String request){
		System.out.println("request="+request);
		return "Shadowed " + request;
	}
	
	@RequestMapping("/voidSearch")
	public void voidSearch(){
		 
	}

}
