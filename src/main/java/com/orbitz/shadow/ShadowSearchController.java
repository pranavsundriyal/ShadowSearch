package com.orbitz.shadow;

import com.orbitz.shadow.expedia.ExpediaSearchServiceImpl;
import com.orbitz.shadow.model.Request;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShadowSearchController {
	
	@RequestMapping("/search")
	public String shadowSearch(@RequestParam(value="origin", required=true) String origin,
                               @RequestParam(value="dest", required=true) String destination,
                               @RequestParam(value="departure", required=true) String departureDate,
                               @RequestParam(value="arrival", required=false) String arrivalDate){

        Request request = null;
        if (arrivalDate == null) {
             request = new Request(departureDate, origin, destination);
        } else {
             request = new Request(arrivalDate,departureDate, origin, destination);
        }
        ExpediaSearchServiceImpl expediaSearchService = new ExpediaSearchServiceImpl();

        String response = expediaSearchService.getResponse(request);
        return response;
	}
	
	@RequestMapping("/voidSearch")
	public void voidSearch(){
		 
	}

}
