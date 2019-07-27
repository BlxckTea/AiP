package com.aip.pillowbuddy;

public class MyAlarm {
    private Integer intHour, intMinute;
    private boolean switchIsOn;

    public MyAlarm(Integer intHour, Integer intMinute, boolean switchIsOn) {
        this.intHour = intHour;
        this.intMinute = intMinute;
        this.switchIsOn = switchIsOn;
    }

    public Integer getIntHour() {
        return intHour;
    }

    public void setIntHour(Integer intHour) {
        this.intHour = intHour;
    }

    public Integer getIntMinute() {
        return intMinute;
    }

    public void setIntMinute(Integer intMinute) {
        this.intMinute = intMinute;
    }

    public boolean isSwitchIsOn() {
        return switchIsOn;
    }

    public void setSwitchIsOn(boolean switchIsOn) {
        this.switchIsOn = switchIsOn;
    }
}
