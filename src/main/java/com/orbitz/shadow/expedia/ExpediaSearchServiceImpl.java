package com.orbitz.shadow.expedia;

import com.orbitz.shadow.SearchService;
import com.orbitz.shadow.model.Request;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ExpediaSearchServiceImpl implements Runnable, SearchService{


    private static String API = "https://www.expedia.com/api/flight/search";
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private Request request;
    private String repsonse;

    public ExpediaSearchServiceImpl(Request request) {
        this.request = request;
    }

    @Override
    public String execute(Request request) {
        String url = API + getParams(request);
        StringBuffer responseBuffer = new StringBuffer();
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        HttpGet getRequest = new HttpGet(url);
        getRequest.addHeader("Accept", "application/json");
        getRequest.addHeader("Content-Encoding", "application/json");
        getRequest.addHeader("Accept­Encoding", "application/json");
        getRequest.addHeader("User-Agent", "Mozilla/5.0");

        try {
            log.info("url is "+url);
            CloseableHttpResponse httpResponse = httpClient.execute(getRequest);
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(httpResponse.getEntity().getContent()));

            String line;
            while ((line = rd.readLine()) != null) {
                responseBuffer.append(line);
            }
        } catch (IOException e) {
            log.error("failed reading the IO stream");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        log.info("Size of the response returned for url "+url+"\nin bytes: "+Integer.toString(responseBuffer.toString().length()));
        return responseBuffer.toString();
    }

    public String getParams(Request request) {
        StringBuilder params = new StringBuilder("?");

        String departureDate = request.getDeparturteDate();

        params.append("departureDate=").append(departureDate).append("&departureAirport=")
                .append(request.getOrigin()).append("&arrivalAirport=")
                .append(request.getDestination());

        if (null != request.getArrivalDate()) {
            params.append("&returnDate=").append(request.getArrivalDate());
        }

        params.append("&maxOfferCount=" + request.getMaxOffer());
        params.append("&eapid=128087");
        return params.toString();
    }

    @Override
    public void run() {
        this.setRepsonse(execute(this.request));
    }

    /*
    //visibleForTesting
    protected String buildTravelerParameters(List<SearchPaxSpec> searchPaxSpecs) {
        Map<TravelerType, Integer> travelerTypeMap = new HashMap<TravelerType, Integer>();

        for (SearchPaxSpec spec : searchPaxSpecs) {
            if (travelerTypeMap.containsKey(spec.getTravelerType())) {
                travelerTypeMap.put(spec.getTravelerType(), travelerTypeMap.get(spec.getTravelerType()) + 1);
            } else {
                travelerTypeMap.put(spec.getTravelerType(), 1);
            }
        }

        //First check for INS and INF in the same search which is unsupported by the EXPE api
        if (travelerTypeMap.containsKey(refDataFactory.findByCode(TravelerType.class, "INF")) &&
                travelerTypeMap.containsKey(refDataFactory.findByCode(TravelerType.class, "INS"))) {
            throw new IllegalSearchCriteriaException(
                    "EXPEDIA search provider does not support INF and INS in the same search.");
        }

        Set<TravelerType> childTravelerTypes = new HashSet<TravelerType>();
        childTravelerTypes.add(refDataFactory.findByCode(TravelerType.class, "CHD"));
        childTravelerTypes.add(refDataFactory.findByCode(TravelerType.class, "INF"));
        childTravelerTypes.add(refDataFactory.findByCode(TravelerType.class, "INS"));

        List<Integer> childAgeList = new ArrayList<Integer>();

        for (SearchPaxSpec searchPaxSpec : searchPaxSpecs) {
            if (childTravelerTypes.contains(searchPaxSpec.getTravelerType())) {
                if (searchPaxSpec.getTravelerAge().getNullable() == null) {
                    childAgeList.add(0);
                } else {
                    childAgeList.add(searchPaxSpec.getTravelerAge().getNullable());
                }
            }
        }

        int numAdultsAndSeniors = 0;

        if (travelerTypeMap.containsKey(refDataFactory.findByCode(TravelerType.class, "ADT"))) {
            numAdultsAndSeniors += travelerTypeMap.get(refDataFactory.findByCode(TravelerType.class, "ADT"));
        }

        if (travelerTypeMap.containsKey(refDataFactory.findByCode(TravelerType.class, "SRC"))) {
            numAdultsAndSeniors += travelerTypeMap.get(refDataFactory.findByCode(TravelerType.class, "SRC"));
        }

        StringBuilder travelerParamString = new StringBuilder();

        if (numAdultsAndSeniors > 0) {
            travelerParamString.append("&numberOfAdultTravelers=").append(numAdultsAndSeniors);
        }

        for (Integer childAge : childAgeList) {
            if (childAge != null) {
                travelerParamString.append("&childTravelerAge=").append(childAge.toString());
            }
        }

        if (travelerTypeMap.containsKey(refDataFactory.findByCode(TravelerType.class, "INF"))) {
            travelerParamString.append("&infantSeatingInLap=true");
        }

        return travelerParamString.toString();
    }

    //Pull the proxy configuration from the environment if it exists.  Always pull https as thats what we use to
    // connect to expedia here.
    protected HostConfiguration configureProxy() {
        String proxy = System.getProperty("https.proxyHost", null);
        Integer proxyPort = Integer.parseInt(System.getProperty("https.proxyPort", "-1"));
        HostConfiguration hostConfig = null;

        if (proxy != null) {
            hostConfig = new HostConfiguration();
            hostConfig.setProxy(proxy, proxyPort);
            LOG.debug("Configuring HttpClient proxy to " + proxy + ":" + proxyPort);
        } else {
            hostConfig = new HostConfiguration();
        }

        return hostConfig;
    }
    */

    public Request getRequest() {
        return request;
    }

    @Override
    public String getRepsonse() {
        return repsonse;
    }

    public void setRepsonse(String repsonse) {
        this.repsonse = repsonse;
    }
}
