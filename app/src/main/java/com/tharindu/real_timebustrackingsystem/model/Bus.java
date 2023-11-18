package com.tharindu.real_timebustrackingsystem.model;

public class Bus {
    private String id;
    private String name;
    private String route;
    private String busNo;

    public Bus() {
    }

    public Bus(String id, String name, String route, String busNo) {
        this.id = id;
        this.name = name;
        this.route = route;
        this.busNo = busNo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getBusNo() {
        return busNo;
    }

    public void setBusNo(String busNo) {
        this.busNo = busNo;
    }
}
