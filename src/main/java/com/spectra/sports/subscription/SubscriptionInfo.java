package com.spectra.sports.subscription;

import java.time.LocalDate;

public record SubscriptionInfo(LocalDate startDate, LocalDate endDate, Boolean expired,
                               String plan, Double amount) {
}