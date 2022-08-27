package com.spectra.sports.subscription;

import com.spectra.sports.entity.StudentMentorAcademyMapping;
import com.spectra.sports.entity.StudentMentorMapping;

import java.time.LocalDate;

import static java.lang.Boolean.TRUE;
import static java.util.Objects.isNull;

public record SubscriptionInfo(LocalDate startDate, LocalDate endDate, Boolean expired,
                               String plan, Double amount) {

    public static SubscriptionInfo from(StudentMentorMapping studentMentor) {
        var expired = isNull(studentMentor.getExpired()) ? TRUE : studentMentor.getExpired();

        return new SubscriptionInfo(
                studentMentor.getStartDate(),
                studentMentor.getEndDate(),
                expired,
                studentMentor.getPlan(),
                studentMentor.getAmount()
        );
    }

    public static SubscriptionInfo from(StudentMentorAcademyMapping studentMentorAcademy) {
        var expired = isNull(studentMentorAcademy.getExpired()) ? TRUE : studentMentorAcademy.getExpired();

        return new SubscriptionInfo(
                studentMentorAcademy.getStartDate(),
                studentMentorAcademy.getEndDate(),
                expired,
                studentMentorAcademy.getPlan(),
                studentMentorAcademy.getAmount()
        );
    }
}