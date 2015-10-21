package com.orbitz.shadow;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.apache.commons.httpclient.HttpClient;

@RestController
public class ShadowSearchController {
	
	@RequestMapping("/search")
	public String shadowSearch(@RequestParam String request){
		return "Shadowed " + request;
	}
	
	@RequestMapping("/voidSearch")
	public void voidSearch(){
		httpClient 
	}

}
