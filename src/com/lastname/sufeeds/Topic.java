package com.lastname.sufeeds;

public class Topic {
    private String name;
    private int week;

    public Topic(String name, int week) {
        this.name = name;
        this.week = week;
    }

    public String getName() {
        return name;
    }

    public int getWeek() {
        return week;
    }
}
