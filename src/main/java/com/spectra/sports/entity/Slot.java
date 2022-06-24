package com.spectra.sports.entity;

import java.io.Serializable;

public class Slot implements Serializable {
    private String day;
    private String[] slots;

    public Slot() {
    }

    public String getDay() {
        return this.day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String[] getSlots() {
        return this.slots;
    }

    public void setSlots(String[] slots) {
        this.slots = slots;
    }
}
