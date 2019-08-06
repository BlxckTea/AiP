package com.aip.pillowbuddy;

public class MyAlarm {
    private String AP;
    private Integer hour, minutes;
    private boolean measureSwitch, safeMs;


    public MyAlarm(Integer hour, Integer minutes, boolean safeMs) {
        if(hour > 12) {
            this.hour = hour - 12;
            this.AP = "오후";
        } else {
            this.hour = hour;
            this.AP = "오전";
        }
        this.minutes = minutes;
        this.safeMs = safeMs;
    }

    public Integer getHour() {
        return hour;
    }

    public void setAP(String AP) {
        this.AP = AP;
    }

    public String getAP() {
        return AP;
    }

    public void setHour(Integer hour) {
        if(hour > 12) this.hour = hour - 12;
        else this.hour = hour;

    }

    public Integer getMinutes() {
        return minutes;
    }

    public void setMinutes(Integer minutes) {
        this.minutes = minutes;
    }

    public boolean isMeasureSwitch() {
        return measureSwitch;
    }

    public void setMeasureSwitch(boolean measureSwitch) {
        this.safeMs = measureSwitch;
        this.measureSwitch = measureSwitch;
    }

    public boolean isSafeMs() {
        return safeMs;
    }

    public void setSafeMs(boolean safeMs) {
        this.safeMs = safeMs;
    }
}
