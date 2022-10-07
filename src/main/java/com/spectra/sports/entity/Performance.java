package com.spectra.sports.entity;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class Performance implements Serializable {
    private String className;
    private String rating;
}