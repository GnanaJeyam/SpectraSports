package com.spectra.sports.scheduler;

import com.spectra.sports.repository.UserMappingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.stream.Collectors;

@Component
@Slf4j
public class SpectraSportsScheduler {
    private final UserMappingRepository userMappingRepository;

    @Autowired
    public SpectraSportsScheduler(UserMappingRepository userMappingRepository) {
        this.userMappingRepository = userMappingRepository;
    }

    @Scheduled(cron = "0 0 23 * * ?")
    public void updateExpiryFlag() {
        var now = LocalDate.now();
        var allExpiryPlans = userMappingRepository.getAllExpiryPlans(now);
        if (!allExpiryPlans.isEmpty()) {
            var updatedMapping = allExpiryPlans
                    .stream()
                    .map(userMapping -> {
                        userMapping.setExpired(true);
                        return userMapping;
                    }).collect(Collectors.toList());

            userMappingRepository.saveAll(updatedMapping);
        }
    }
}