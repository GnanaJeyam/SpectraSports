package com.spectra.sports.scheduler;

import com.spectra.sports.repository.StudentMentorAcademyRepository;
import com.spectra.sports.repository.StudentMentorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.stream.Collectors;

@Component
@Slf4j
public class SpectraSportsScheduler {
    private final StudentMentorRepository studentMentorRepository;
    private final StudentMentorAcademyRepository studentMentorAcademyRepository;

    @Autowired
    public SpectraSportsScheduler(StudentMentorRepository studentMentorRepository,
                                  StudentMentorAcademyRepository studentMentorAcademyRepository) {
        this.studentMentorRepository = studentMentorRepository;
        this.studentMentorAcademyRepository = studentMentorAcademyRepository;
    }

    @Scheduled(cron = "0 0 */12 * * ?")
    public void updateExpiryFlag() {
        log.info("Starting the expiry scheduler ................");
        var now = LocalDate.now();
        var mentorExpiry = studentMentorRepository.getAllExpiryMappings(now);
        var academyExpiry = studentMentorAcademyRepository.getAllExpiryMappings(now);

        if (!mentorExpiry.isEmpty()) {
            studentMentorRepository.saveAll(
                mentorExpiry
                    .stream()
                    .map(user -> {
                        user.setExpired(true);
                        return  user;
                    })
                    .collect(Collectors.toList())
            );
            log.info("Updating the Expiry status for student with mentor mapping......");
        }

        if (!academyExpiry.isEmpty()) {
            studentMentorAcademyRepository.saveAll(
                academyExpiry
                    .stream()
                    .map(user -> {
                        user.setExpired(true);
                        return  user;
                    })
                    .collect(Collectors.toList())
            );
            log.info("Updating the Expiry status for student with mentor and academy mapping......");
        }
    }
}