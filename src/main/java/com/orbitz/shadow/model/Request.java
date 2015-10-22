package com.orbitz.shadow.model;

/**
 * Created with IntelliJ IDEA.
 * User: Pranav Sundriyal
 * Date: 10/22/15
 * Time: 1:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class Request {
    String arrivalDate;
    String departurteDate;
    String origin;
    String destination;


    public Request(String arrivalDate, String departurteDate, String origin, String destination) {
        this.arrivalDate = arrivalDate;
        this.departurteDate = departurteDate;
        this.origin = origin;
        this.destination = destination;
    }

    public Request(String departurteDate, String origin, String destination) {
        this.departurteDate = departurteDate;
        this.origin = origin;
        this.destination = destination;
    }

    public String getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(String arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public String getDeparturteDate() {
        return departurteDate;
    }

    public void setDeparturteDate(String departurteDate) {
        this.departurteDate = departurteDate;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}
