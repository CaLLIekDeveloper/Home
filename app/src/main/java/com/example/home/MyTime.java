package com.example.home;

public class MyTime {
    public long startTime;
    public long endTime;

    public long timePassed;

    public long getBase()
    {
        return endTime-startTime;
    }
}
