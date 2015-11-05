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

public class Transform {

    public static ExecutorService executorService = Executors.newFixedThreadPool(10);

    @FunctionalInterface
    public interface BiFunction<T, U, R> {
        R   apply(T t, U u);
    }

    public static BiFunction<Request,String,Request> changeDestination = (request, dest) -> {
        request.setDestination(dest);
        return request;
    };


    public static BiFunction<Request,String,Request> changeDepartureDate = (request, date) -> {
        request.setDeparturteDate(date);
        return request;
    };

    public static BiFunction<Request,String,Request> changeDepartureAheadDays = (request, days) -> {
        if (isDateCorrect(request.getDeparturteDate()) && isNumeric(days)) {
            LocalDate date = convertToLocalDate(request.getDeparturteDate());
            LocalDate newDate = date.plusDays(Long.parseLong(days));
            request.setDeparturteDate(convertLocalDatetoString(newDate));
        }
        return request;
    };

    public static BiFunction<Request,String,Request> changeDepartureBehindDays = (request, days) -> {

        if (isDateCorrect(request.getDeparturteDate()) && isNumeric(days)) {
            LocalDate date = convertToLocalDate(request.getDeparturteDate());
            LocalDate newDate = date.minusDays(Long.parseLong(days));
            request.setDeparturteDate(convertLocalDatetoString(newDate));
        }
        return request;
    };


    public static BiFunction<Request,String,Request> changeOrigin = (request, origin) -> {
        request.setOrigin(origin);
        return request;
    };

    public static BiFunction<Request,String,Request> changeMaxOffer = (request, count) -> {
        if (isNumeric(count))
            request.setMaxOffer(Integer.parseInt(count));
        return request;
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
        funcMap = map;
    }



    public static Function<Request,String> fire = (request) -> {
        executorService.execute(new ExpediaSearchServiceImpl(request));
        return "success";
    };


    public  static Request transformReq(Request request, String function, String param) {
        if (funcMap.get(function)!=null) {
            BiFunction<Request, String, Request> biFunction = funcMap.get(function);
            if (biFunction != null) {
                request = biFunction.apply(request, param);
                System.out.println("apply function " + function + " " + biFunction.getClass().getSimpleName() + " with params " + param);
                fire.apply(request);

            }
        }
        return request;
    }


    public static BiFunction<Request,Integer,List<Request>> multiplyRequest = (r, n)-> {
        List<Request> requestList = new ArrayList<>();
        for (int i=0; i<n;i++) {
            fire.apply(r);
        }
        return  requestList;
    };

    public static boolean isNumeric(String s) {
        return s.matches("[-+]?\\d*\\.?\\d+");
    }

    public static boolean isDateCorrect(String s) {
        String [] d = s.split("-");
        for (int i =0 ; i<d.length;i++){
            if (!isNumeric(d[0]))
                return false;
        }
        return true;
    }

    public static LocalDate convertToLocalDate(String s){
        String d [] = s.split("-");
        LocalDate localDate = LocalDate.of(Integer.parseInt(d[0]), Integer.parseInt(d[1]), Integer.parseInt(d[2]));
        return localDate;
    }

    public static String convertLocalDatetoString(LocalDate d){
        return d.getYear()+"-"+d.getMonthValue()+"-"+d.getDayOfMonth();
    }


    public static Map<String, BiFunction> getFuncMap() {
        return funcMap;
    }
}



