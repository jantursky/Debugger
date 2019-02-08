package com.jantursky.debugger.utils;

public class TimeUtils {

    public static String getLength(long time) {
        if (time < 1000L) {
            return time + " ms";
        } else {
            return time + " s";
        }
    }

}