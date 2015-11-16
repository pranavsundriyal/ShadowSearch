package com.orbitz.shadow.transform;

import com.orbitz.shadow.expedia.ExpediaSearchServiceImpl;
import com.orbitz.shadow.model.Request;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.logging.Logger;

public class Transform {

    private static ExecutorService executorService = Executors.newFixedThreadPool(10);

    private static Logger log = Logger.getLogger(Transform.class.getName());

    @FunctionalInterface
    public interface BiFunction<T, U, R> {
        R apply(T t, U u);
    }

    public static BiFunction<Request,String,List<Request>> changeDestination = (request, dest) -> {
        List<Request>requestList = new ArrayList<>();
        if (isDateValid(dest)) {
            request.setDestination(dest);
            requestList.add(request);
        }
        return requestList;
    };


    public static BiFunction<Request,String,List<Request>> changeDepartureDate = (request, date) -> {
        List<Request>requestList = new ArrayList<>();
        if(isDateValid(date)) {
            request.setDeparturteDate(date);
            requestList.add(request);
        }
        return requestList;
    };

    public static BiFunction<Request,String,List<Request>> changeDepartureAheadDays = (request, days) -> {
        List<Request>requestList = new ArrayList<>();
        if (isDateValid(request.getDeparturteDate()) && isNumeric(days)) {
            LocalDate date = convertToLocalDate(request.getDeparturteDate());
            LocalDate newDate = date.plusDays(Long.parseLong(days));
            request.setDeparturteDate(convertLocalDatetoString(newDate));
        }
        requestList.add(request);
        return requestList;
    };

    public static BiFunction<Request,String,List<Request>> allDepartureDaysAhead = (request, days) -> {
        List<Request>requestList = new ArrayList<>();
        if (isDateValid(request.getDeparturteDate()) && isNumeric(days)) {
            int n = Integer.parseInt(days);
            LocalDate date = convertToLocalDate(request.getDeparturteDate());
            for (int i=1; i<=n ; i++) {
                Request newRequest = new Request(request);
                LocalDate newDate = date.plusDays(i);
                newRequest.setDeparturteDate(convertLocalDatetoString(newDate));
                requestList.add(newRequest);
            }

        }
        return requestList;
    };

    public static BiFunction<Request,String,List<Request>> allDepartureDaysBehind = (request, days) -> {
        List<Request>requestList = new ArrayList<>();
        if (isDateValid(request.getDeparturteDate()) && isNumeric(days)) {
            int n = Integer.parseInt(days);
            LocalDate date = convertToLocalDate(request.getDeparturteDate());
            for (int i=1;        i<=n ; i++) {
                Request newRequest = new Request(request);
                LocalDate newDate = date.minusDays(i);
                newRequest.setDeparturteDate(convertLocalDatetoString(newDate));
                requestList.add(newRequest);
            }

        }
        return requestList;
    };

    public static BiFunction<Request,String,List<Request>> flexSearchOW = (request, days) -> {
        List<Request>requestList = new ArrayList<>();
        if (isDateValid(request.getDeparturteDate()) && isNumeric(days)) {
            int n = Integer.parseInt(days);
            LocalDate departDay = convertToLocalDate(request.getDeparturteDate());
            for (int i= 1; i <= n; i++) {
                Request negativeRequest = new Request(request);
                LocalDate departureDate = departDay.minusDays(i);
                negativeRequest.setDeparturteDate(convertLocalDatetoString(departureDate));
                requestList.add(negativeRequest);

                Request plusRequest = new Request(request);
                departureDate = departDay.plusDays(i);
                plusRequest.setDeparturteDate(convertLocalDatetoString(departureDate));
                requestList.add(plusRequest);
            }
        }
        return requestList;
    };

    public static BiFunction<Request,String,List<Request>> flexSearchOWArrival = (request, days) -> {
        List<Request>requestList = new ArrayList<>();
        if (isDateValid(request.getDeparturteDate()) && isNumeric(days) && isDateValid(request.getArrivalDate())) {
            int n = Integer.parseInt(days);
            LocalDate arrivalDay = convertToLocalDate(request.getArrivalDate());
            for (int i= 1; i <= n; i++) {
                Request negativeRequest = new Request(request);
                LocalDate arrivalDate = arrivalDay.minusDays(i);
                negativeRequest.setArrivalDate(convertLocalDatetoString(arrivalDate));
                requestList.add(negativeRequest);

                Request plusRequest = new Request(request);
                arrivalDate = arrivalDay.plusDays(i);
                plusRequest.setArrivalDate(convertLocalDatetoString(arrivalDate));
                requestList.add(plusRequest);
            }
        }
        return requestList;
    };

    public static BiFunction<Request,String,List<Request>> flexSearchRT = (request, days) -> {
        List<Request>requestList = new ArrayList<>();
        if (isDateValid(request.getDeparturteDate()) && isNumeric(days) && isDateValid(request.getArrivalDate())) {
            int n = Integer.parseInt(days);
            LocalDate departDay = convertToLocalDate(request.getDeparturteDate());
            LocalDate arrivalDay = convertToLocalDate(request.getArrivalDate());
            for (int i = 0; i <= n ; i++) {
                for (int j= 0; j <= n; j++) {
                    Request negativeRequest = new Request(request);
                    LocalDate departureDate = departDay.minusDays(i);
                    LocalDate arrivalDate = arrivalDay.minusDays(j);
                    negativeRequest.setDeparturteDate(convertLocalDatetoString(departureDate));
                    negativeRequest.setArrivalDate(convertLocalDatetoString(arrivalDate));
                    requestList.add(negativeRequest);

                    Request plusRequest = new Request(request);
                    departureDate = departDay.plusDays(i);
                    arrivalDate = arrivalDay.plusDays(j);
                    plusRequest.setDeparturteDate(convertLocalDatetoString(departureDate));
                    plusRequest.setArrivalDate(convertLocalDatetoString(arrivalDate));

                    requestList.add(plusRequest);
                }
            }
        }
        return requestList;
    };
    public static BiFunction<Request,String,List<Request>> changeDepartureBehindDays = (request, days) -> {

        List<Request>requestList = new ArrayList<>();

        if (isDateValid(request.getDeparturteDate()) && isNumeric(days)) {
            LocalDate date = convertToLocalDate(request.getDeparturteDate());
            LocalDate newDate = date.minusDays(Long.parseLong(days));
            request.setDeparturteDate(convertLocalDatetoString(newDate));
        }
        requestList.add(request);
        return requestList;
    };


    public static BiFunction<Request,String,List<Request>> changeOrigin = (request, origin) -> {
        List<Request>requestList = new ArrayList<>();
        request.setOrigin(origin);
        requestList.add(request);
        return requestList;
    };

    public static BiFunction<Request,String,List<Request>> changeMaxOffer = (request, count) -> {
        List<Request>requestList = new ArrayList<>();
        if (isNumeric(count))
            request.setMaxOffer(Integer.parseInt(count));
        requestList.add(request);
        return requestList;
    };

    public static BiFunction<Request,String,List<Request>> multiplyRequest = (request, n)-> {

        List<Request>requestList = new ArrayList<>();

        if (isNumeric(n)){
            int size = Integer.parseInt(n);
            for (int i=0; i <size;i++) {
                requestList.add(request);
            }
        }
        return requestList;
    };

    private static final Map<String, BiFunction> funcMap;
    static {
        Map<String, BiFunction> map = new HashMap<>();
        map.put("changeDestination", changeDestination);
        map.put("changeDepartureDate", changeDepartureDate);
        map.put("changeOrigin", changeOrigin);
        map.put("changeMaxOffer", changeMaxOffer);
        map.put("changeDepartureAheadDays", changeDepartureAheadDays);
        map.put("changeDepartureBehindDays",changeDepartureBehindDays);
        map.put("allDepartureDaysAhead", allDepartureDaysAhead);
        map.put("allDepartureDaysBehind", allDepartureDaysBehind);
        map.put("flexSearchRT", flexSearchRT);
        map.put("flexSearchOW", flexSearchOW);
        map.put("flexSearchOWArrival", flexSearchOWArrival);
        map.put("multiply", multiplyRequest);
        funcMap = map;
    }

    public static Function<Request,String> fire = (request) -> {
        executorService.execute(new ExpediaSearchServiceImpl(request));
        return "success";
    };

    @SuppressWarnings("unchecked")
    public  static List<Request> transformReq(Request request, String function, String param) {

        List<Request> requestList = null;
        if (funcMap.get(function)!=null) {
            try{
                BiFunction<Request, String, Request> biFunction = funcMap.get(function);
                 if (null != biFunction) {
                    requestList  = (List<Request>) biFunction.apply(request, param);
                    System.out.println("apply function " + function + " " + biFunction.getClass().getSimpleName() + " with params " + param);
                    requestList.parallelStream().forEach(r -> fire.apply(r));
                }

            }catch (ClassCastException c){
                log.info("Class Cast exception");
            }
        } else {
            log.info("Function "+function+" not defined");
        }
        return requestList;
    }





    public static boolean isNumeric(String s) {
        return s.matches("[-+]?\\d*\\.?\\d+");
    }

    public static boolean isDateValid(String s) {
        if (s != null) {
            String[] d = s.split("-");
            for (int i = 0; i < d.length; i++) {
                if (!isNumeric(d[0])){
                    log.info("date is not in correct format: "+s);
                    return false;
                }
            }
            return true;
        }
        log.info("date is null");
        return false;
    }

    public static LocalDate convertToLocalDate(String s){
        if (s != null) {
            String d[] = s.split("-");
            if (isNumeric(d[0]) && isNumeric(d[1]) && isNumeric(d[2])) {
                LocalDate localDate = LocalDate.of(Integer.parseInt(d[0]), Integer.parseInt(d[1]), Integer.parseInt(d[2]));
                return localDate;
            } else {
                log.info("date is not in correct format: "+s);
                return null;
            }
        } else {
            log.info("date is null");
            return null;
        }
    }

    public static String convertLocalDatetoString(LocalDate d){
        return d.getYear()+"-"+d.getMonthValue()+"-"+d.getDayOfMonth();
    }



}



