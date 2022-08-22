package com.spectra.sports.subscription;

import com.spectra.sports.entity.UserMapping;

import java.time.LocalDate;

import static java.lang.Boolean.TRUE;
import static java.util.Objects.isNull;

public record SubscriptionInfo(LocalDate startDate, LocalDate endDate, Boolean expired,
                               String plan, Double amount) {
    public static SubscriptionInfo from(UserMapping userMapping) {
        if (isNull(userMapping)) {
            return null;
        }
        var expired = isNull(userMapping.getExpired()) ? TRUE : userMapping.getExpired();
        return new SubscriptionInfo(userMapping.getStartDate(), userMapping.getEndDate(),
                expired, userMapping.getPlan(), userMapping.getAmount());
    }
}