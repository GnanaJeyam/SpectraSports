package com.spectra.sports.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Slot implements Serializable {
    private String day;
    private List<SlotDetail> slots;
    private boolean isSelected;
}
