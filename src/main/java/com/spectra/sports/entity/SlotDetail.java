package com.spectra.sports.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class SlotDetail implements Serializable {
    private String time;
    private boolean isSelected;
}