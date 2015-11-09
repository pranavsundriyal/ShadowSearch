package com.orbitz.shadow;

import com.orbitz.shadow.expedia.ExpediaSearchServiceImpl;
import com.orbitz.shadow.model.Modifiers;
import com.orbitz.shadow.model.Request;
import com.orbitz.shadow.model.RequestWrapper;

import com.orbitz.shadow.transform.Transform;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.orbitz.shadow.logging.KafkaLogger;
import com.orbitz.shadow.logging.amazonSQS;

import java.util.List;
import java.util.function.Function;
import java.util.logging.Logger;

@RestController
public class ShadowSearchController {

    private Logger log = Logger.getLogger(ShadowSearchController.class.getName());

    @RequestMapping(value = "/search", method = RequestMethod.GET)
	public String shadowSearch(@RequestParam(value="origin", required=true) String origin,
                               @RequestParam(value="dest", required=true) String destination,
                               @RequestParam(value="departure", required=true) String departureDate,
                               @RequestParam(value="arrival", required=false) String arrivalDate,
                               @RequestParam(value="function", required=false) String function,
                               @RequestParam(value="param", required=false) String param){

        Request request;
        if (arrivalDate == null) {
             request = new Request(departureDate, origin, destination);
        } else {
             request = new Request(arrivalDate,departureDate, origin, destination);
        }

        List<Request> requestList = Transform.transformReq(request, function, param);

        return "Successfully fired function: "+function+" with params: "+param+" of size: "+requestList.size();
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
		String response = "WAKA WAKA";
		Request req = reqWrap.getRequest();
		Modifiers mods = reqWrap.getModifiers();
		
		System.out.println(req.getOrigin() + ", " + req.getDestination() + ", " + req.getArrivalDate() + ", " + req.getDeparturteDate());
		System.out.println(mods.getSearchHost() + ", " + mods.getTransform() + ", " + mods.getSomethingElse());

		amazonSQS sqs = new amazonSQS();
		
	    ExpediaSearchServiceImpl expediaSearchService = new ExpediaSearchServiceImpl(req);
        response = expediaSearchService.execute(req);
        
        sqs.sendMessage(response);
        
		return response; 
	}


}
