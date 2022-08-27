package com.spectra.sports.usermapping;

public record UserMappingRequest(Long studentId, Long mentorId, Long academyId, Long totalMonths, Double amount,
                                 String plan, String mentorType, String academyType, String slot) {
}