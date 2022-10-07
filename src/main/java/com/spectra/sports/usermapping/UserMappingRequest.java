package com.spectra.sports.usermapping;

import java.util.Map;

import static com.spectra.sports.constant.SpectraConstant.*;
import static com.spectra.sports.util.NumberUtil.toDouble;
import static com.spectra.sports.util.NumberUtil.toLong;

public record UserMappingRequest(Long studentId, Long mentorId, Long academyId, Long totalMonths, Double amount,
                                 String plan, String mentorType, String academyType, String slot, String slotDays,
                                 String academyName, String email, String message, String mappedName) {

    public static UserMappingRequest from(Map<String, String> userDetails) {
        return new UserMappingRequest(
            toLong(userDetails.get(STUDENT_ID)), toLong(userDetails.get(MENTOR_ID)), toLong(userDetails.get(ACADEMY_ID)),
            toLong(userDetails.get(MONTHS)), toDouble(userDetails.get(AMOUNT)), userDetails.get(PLAN),
            userDetails.get(MENTOR_TYPE), userDetails.get(ACADEMY_TYPE), userDetails.get(SLOT), userDetails.get(SLOT_DAYS),
            userDetails.get(ACADEMY_NAME), userDetails.get(EMAIL), userDetails.get(MESSAGE), userDetails.get(MAPPED_NAME)
        );
    }
}