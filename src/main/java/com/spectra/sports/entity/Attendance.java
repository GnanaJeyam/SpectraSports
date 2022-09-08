package com.spectra.sports.entity;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class Attendance implements Serializable {
    private String day;
    private short status;
}