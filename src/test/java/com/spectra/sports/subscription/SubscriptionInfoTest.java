package com.spectra.sports.subscription;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubscriptionInfoTest {

    @Test
    void shouldReturnSubscriptionInfo() {
        var todayDate = LocalDate.now();
        var plan = "100 rs plan";
        var subscriptionInfo = new SubscriptionInfo(todayDate, todayDate, false, plan, 10.0, "Slot",
                "Sunday", "academyName", "mappedName");

        assertEquals(plan, subscriptionInfo.plan());
        assertEquals(todayDate, subscriptionInfo.startDate());
    }
}