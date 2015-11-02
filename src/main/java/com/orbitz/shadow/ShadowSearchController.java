package com.orbitz.shadow;

import com.orbitz.shadow.expedia.ExpediaSearchServiceImpl;
import com.orbitz.shadow.model.Modifiers;
import com.orbitz.shadow.model.Request;
import com.orbitz.shadow.model.RequestWrapper;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.orbitz.shadow.logging.KafkaLogger;

@RestController
public class ShadowSearchController {
	
	@RequestMapping(value = "/search", method = RequestMethod.GET)
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
        	KafkaLogger logger = new KafkaLogger();
        logger.log(response);
        
        return response;
	}
	
	/* sample json requests
{
    "request":{
        "arrivalDate": "2015-12-12",
        "departurteDate": "2015-12-16",
        "origin": "ORD",
        "destination": "DTW"
    },
    "modifiers":{
        "searchHost": "expedia",
        "transform": "next week",
        "somethingElse": "hello world"
    }
}
	 */
	
	@RequestMapping(value = "/voidSearch", method = RequestMethod.POST)
	public String voidSearch(@RequestBody RequestWrapper reqWrap){
		Request req = reqWrap.getRequest();
		Modifiers mods = reqWrap.getModifiers();
		
		System.out.println(req.getOrigin() + ", " + req.getDestination() + ", " + req.getArrivalDate() + ", " + req.getDeparturteDate());
		System.out.println(mods.getSearchHost() + ", " + mods.getTransform() + ", " + mods.getSomethingElse());
		 
		ExpediaSearchServiceImpl expediaSearchService = new ExpediaSearchServiceImpl();
        String response = expediaSearchService.getResponse(req);
		return response;
         
         
	}

}
