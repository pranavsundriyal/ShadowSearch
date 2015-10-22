package com.orbitz.shadow.expedia;

public class ORBModelMapper {
    /*
    private static final String AMOUNT = "amount";
    private static final String CURRENCY_CODE = "currencyCode";
    private static final String TOTAL_PRICE = "totalPrice";
    private static final String BASE_PRICE = "basePrice";
    private static final String TAXES_PRICE = "taxesPrice";
    public static final String BOOKING_CODE = "bookingCode";
    public static final String DEPARTURE_AIRPORT_CODE = "departureAirportCode";
    public static final String DEPARTURE_TIME_RAW = "departureTimeRaw";
    public static final String ARRIVAL_AIRPORT_CODE = "arrivalAirportCode";
    public static final String ARRIVAL_TIME_RAW = "arrivalTimeRaw";
    public static final String AIRLINE_CODE = "airlineCode";
    public static final String FLIGHT_NUMBER = "flightNumber";
    public static final String STOPS = "stops";
    public static final String OPERATING_AIRLINE_CODE = "operatingAirlineCode";
    public static final String DEFAULT_FARE_TYPE = "PUBLISHED";
    public static final String OFFERS = "offers";
    public static final String LEGS = "legs";
    public static final String FARE_TYPE = "fareType";
    public static final String PRICE_PER_PASSENGER_CATEGORY = "pricePerPassengerCategory";
    private CabinClass cabin;
    private String bookingCode;
    private Location origin;
    private DateTime departureTime;
    private Location destination;
    private DateTime arrivalTime;
    private Optional<Carrier> marketingCarrier;
    private String marketingFlightNumber;
    private Optional<Carrier> operatingCarrier;
    private String operatingFlightNumber;
    private int stops;
    private AirportLookupService airportLookupService;
    private List<AirPricingSolution> airPricingSolutions = Lists.newArrayList();
    private ReferenceDataFactory referenceDataFactory;
    public static final String SEGMENT_ATTRIBUTES = "segmentAttributes";
    public static final String LEG_IDS = "legIds";
    public static final String SEGMENTS = "segments";

    private static final Map<String, FareType> FARE_TYPES = new HashMap<String, FareType>();
    private static final Logger LOG = LoggerFactory
            .getLogger(LowFareCollectorFactory.class);

    private final Map<String, String> expeToOrbPtcMapping = new HashMap<String, String>() { {
            put("ADULT", "ADT");
            put("ADULT_CHILD", "CNN");
            put("CHILD", "CNN");
            put("INFANT_IN_LAP", "INF");
            put("INFANT_IN_SEAT", "INS");
    } };

    private final Map<String, String> expeToOrbTravelerTypeMapping = new HashMap<String, String>() { {
        put("ADULT", "ADT");
        put("ADULT_CHILD", "CHD");
        put("CHILD", "CHD");
        put("INFANT_IN_LAP", "INF");
        put("INFANT_IN_SEAT", "INS");
    } };

    public ORBModelMapper(ReferenceDataFactory referenceDataFactory,
            AirportLookupService airportLookupService) {
        this.referenceDataFactory = referenceDataFactory;
        this.airportLookupService = airportLookupService;
        fareTypeMap();
    }

    public List<AirPricingSolution> mapOfferToAirPricingSolution(String jsonResponse) throws IOException {
        JSONObject responseObject = JSONObject.fromObject(jsonResponse);

        JSONArray offers = responseObject.getJSONArray(OFFERS);
        JSONArray legs = responseObject.getJSONArray(LEGS);
        Map<String, JSONObject> legMap = constructLegMap(legs);

        for (Object o : offers) {
            JSONObject offer = (JSONObject) o;
            JSONArray offerSegmentAttributes = offer
                    .getJSONArray(SEGMENT_ATTRIBUTES);
            JSONArray legIds = offer.getJSONArray(LEG_IDS);

            Journey journey = new Journey();

            for (int legIndex = 0; legIndex < legIds.size(); legIndex++) {
                String legId = legIds.getString(legIndex);
                JSONObject leg = legMap.get(legId);

                List<Segment> orbSegments = new ArrayList<Segment>();
                JSONArray legSegmentAttributes = offerSegmentAttributes
                        .getJSONArray(legIndex);
                JSONArray segments = leg.getJSONArray(SEGMENTS);
                for (int segmentIndex = 0; segmentIndex < segments.size(); segmentIndex++) {

                    JSONObject segment = segments.getJSONObject(segmentIndex);

                    JSONObject segmentAttributes = legSegmentAttributes.getJSONObject(segmentIndex);

                    parseSegment(segment, segmentAttributes);

                    Segment orbSegment = constructORBSegment();
                    Leg orbLeg = constructORBLeg();

                    orbSegment.addLeg(orbLeg);
                    orbSegments.add(orbSegment);
                }
                Slice slice = new Slice(orbSegments);
                journey.addSlice(slice);
            }
            journey.setCarrier(journey.getSliceList().get(0)
                                       .getMarketingCarrier());

            Eticketability eticketability = null;

            FareType fareType = getFareType(offer.getString(FARE_TYPE));
            List<PassengerPricingInfo> passengerPricingInfo = constructPassengerPricingInfoList(
                    offer.getJSONArray(PRICE_PER_PASSENGER_CATEGORY));

            List<Fee> serviceFeeList = new ArrayList<Fee>();
            serviceFeeList.add(new Fee(referenceDataFactory.findByCode(FeeType.class, "SR"),
                                       new Money("0.00", passengerPricingInfo.get(0).getTotalPrice().getCurrency())));

            AirPricingSolution airPricingSolution = new AirPricingSolution(journey, passengerPricingInfo,
                                                                           eticketability, fareType);
            airPricingSolution.setValidatingVendor(journey.getCarrier());
            airPricingSolution.setDefaultPricingCarrier(journey.getCarrier());
            airPricingSolution.setServiceFees(serviceFeeList);
            airPricingSolutions.add(airPricingSolution);

        }
        return airPricingSolutions;
    }

    public List<PassengerPricingInfo> constructPassengerPricingInfoList(JSONArray pricePerPassengerCategory) {
        List<PassengerPricingInfo> passengerPricingList = new ArrayList<PassengerPricingInfo>();

        Map<String, List<JSONObject>> passengerCategoryToPricePerPassengerMap = getCategoryToPassengerPricingInfoMap(pricePerPassengerCategory);

        for (Map.Entry<String ,List<JSONObject>> category :  passengerCategoryToPricePerPassengerMap.entrySet()) {
            String basePrice = category.getValue().get(0).getJSONObject(BASE_PRICE).getString(AMOUNT);
            String basePriceCurrencyCode = category.getValue().get(0).getJSONObject(BASE_PRICE).getString(CURRENCY_CODE);
            String taxesPrice = category.getValue().get(0).getJSONObject(TAXES_PRICE).getString(AMOUNT);
            String taxesPriceCurrency = category.getValue().get(0).getJSONObject(TAXES_PRICE).getString(CURRENCY_CODE);

            PassengerPricingInfo passengerPricingInfo = new PassengerPricingInfo(
                    referenceDataFactory.findByCode(Ptc.class, expeToOrbPtcMapping.get(category.getKey())),
                    new Money(basePrice, basePriceCurrencyCode),
                    new Money(taxesPrice, taxesPriceCurrency));
            passengerPricingInfo.setPassengerCount(category.getValue().size());
            passengerPricingInfo.addTravelerType(referenceDataFactory.findByCode(TravelerType.class, expeToOrbTravelerTypeMapping.get(category.getKey())));
            passengerPricingList.add(passengerPricingInfo);
        }

        return passengerPricingList;
    }

    private Map<String, List<JSONObject>> getCategoryToPassengerPricingInfoMap(JSONArray pricePerPassengerCategory) {
        Map<String, List<JSONObject>> passengerCategoryToPricePerPassengerMap = new HashMap<String,List< JSONObject>>();

        for (int index = 0; index<pricePerPassengerCategory.size(); index ++ ) {
            JSONObject ppPassengerJSONObject = pricePerPassengerCategory.getJSONObject(index);
            String passengerCategory = ppPassengerJSONObject.getString("passengerCategory");

            if(passengerCategoryToPricePerPassengerMap.containsKey(passengerCategory)) {
                List<JSONObject> jsonObjects = passengerCategoryToPricePerPassengerMap.get(passengerCategory);
                jsonObjects.add(ppPassengerJSONObject);
                passengerCategoryToPricePerPassengerMap.put(passengerCategory, jsonObjects);
            } else {
                List<JSONObject> pricePerPassengerList = new ArrayList<JSONObject>();
                pricePerPassengerList.add(ppPassengerJSONObject);
                passengerCategoryToPricePerPassengerMap.put(passengerCategory, pricePerPassengerList);
            }

        }
        return passengerCategoryToPricePerPassengerMap;
    }

    public Map<String, JSONObject> constructLegMap(JSONArray legs) {
        Map<String, JSONObject> legMap = new HashMap<String, JSONObject>();
        for (Object o : legs) {
            JSONObject leg = (JSONObject) o;
            legMap.put(leg.getString("legId"), leg);
        }
        return legMap;
    }

    public Segment constructORBSegment() {
        Segment segment = new Segment(origin, destination, departureTime,
                                      arrivalTime, marketingCarrier, marketingFlightNumber,
                                      operatingCarrier, operatingFlightNumber, cabin);
        segment.setBookingCode(bookingCode);

        return segment;
    }

    public Leg constructORBLeg() {
        Leg orbLeg = new Leg(origin, destination, departureTime, arrivalTime);
        orbLeg.setNumStops(stops);
        return orbLeg;
    }

    public void parseSegment(JSONObject segment, JSONObject segmentAttributes) {
        //default cabin class coach
        cabin = referenceDataFactory.findByCode(CabinClass.class, "C");
        bookingCode = segmentAttributes.getString(BOOKING_CODE);

        origin = getAirport(segment.getString(DEPARTURE_AIRPORT_CODE));
        departureTime = new DateTime(segment.getString(DEPARTURE_TIME_RAW));

        destination = getAirport(segment.getString(ARRIVAL_AIRPORT_CODE));
        arrivalTime = new DateTime(segment.getString(ARRIVAL_TIME_RAW));

        String airlineCode = segment.getString(AIRLINE_CODE);
        marketingCarrier = new Optional<Carrier>(
                referenceDataFactory.findByCode(Carrier.class, airlineCode));
        marketingFlightNumber = segment.getString(FLIGHT_NUMBER);

        stops = segment.getInt(STOPS);

        if (segment.containsKey(OPERATING_AIRLINE_CODE)) {
            operatingCarrier = new Optional<Carrier>(
                    referenceDataFactory.findByCode(Carrier.class,
                                                    segment.getString(OPERATING_AIRLINE_CODE)));
        } else {
            operatingCarrier = marketingCarrier;
        }
        operatingFlightNumber = marketingFlightNumber;
    }

    private Location getAirport(String airportCode) {
        return airportLookupService.findLocationByIATACode(airportCode,
                                                           referenceDataFactory
                                                                   .findByCode(LocationType.class, "AIRPORT"),
                                                           Locale.US);
    }

    private FareType getFareType(String fareTypeCode) {
        FareType fareType = FARE_TYPES.get(fareTypeCode);
        if (fareType == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Unknown fareType=" + fareTypeCode);
            }
            fareType = FARE_TYPES.get(DEFAULT_FARE_TYPE);
        }
        return fareType;
    }

    private void fareTypeMap() {
        FARE_TYPES.put("PUBLISHED",
                       referenceDataFactory.findByCode(FareType.class, "PUB"));
        FARE_TYPES.put("NET",
                       referenceDataFactory.findByCode(FareType.class, "NET"));
        FARE_TYPES.put("BULK",
                       referenceDataFactory.findByCode(FareType.class, "BULK"));
        FARE_TYPES.put("CHR",
                       referenceDataFactory.findByCode(FareType.class, "CHR"));
    }
    */

}
