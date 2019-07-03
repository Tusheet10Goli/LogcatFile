package com.example.tvusagerecord.object;

/**
 * class representing object containing one app name and its time stamp to start
 */
public class AppTimeStamp {

    /** string of the time stamp that the app started each time */
    String timeStamp;
    /** name of the app (maybe package name) */
    String appName;


    /**
     * empty constructor of an app time stamp
     */
    public AppTimeStamp() {
        //no content
    }


    /**
     * constructor of app time stamp with parameters
     * @param timeStamp
     * @param appName
     */
    public AppTimeStamp(String timeStamp, String appName) {
        setTimeStamp(timeStamp);
        setAppName(appName);
    }


    /**
     * Multiple setters
     */
    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }


    /**
     * Multiple getters
     */
    public String getTimeStamp() {
        return timeStamp;
    }

    public String getAppName() {
        return appName;
    }
}