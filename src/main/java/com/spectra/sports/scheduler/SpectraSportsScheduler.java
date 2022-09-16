package com.spectra.sports.scheduler;

import com.spectra.sports.entity.StudentMentorAcademyMapping;
import com.spectra.sports.entity.StudentMentorMapping;
import com.spectra.sports.repository.StudentMentorAcademyRepository;
import com.spectra.sports.repository.StudentMentorRepository;
import com.spectra.sports.repository.StudentRatingDetailRepository;
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
    private final StudentRatingDetailRepository studentRatingDetailRepository;

    @Autowired
    public SpectraSportsScheduler(StudentMentorRepository studentMentorRepository,
                                  StudentMentorAcademyRepository studentMentorAcademyRepository,
                                  StudentRatingDetailRepository studentRatingDetailRepository) {
        this.studentMentorRepository = studentMentorRepository;
        this.studentMentorAcademyRepository = studentMentorAcademyRepository;
        this.studentRatingDetailRepository = studentRatingDetailRepository;
    }

    @Scheduled(cron = "0 0 */12 * * ?")
    public void updateExpiryFlag() {
        log.info("Starting the expiry scheduler ................");
        var now = LocalDate.now();

        updateMentorMappingExpiry(now);
        updateAcademyMappingExpiry(now);
    }

    private void updateMentorMappingExpiry(LocalDate now) {
        var mentorExpiry = studentMentorRepository.getAllExpiryMappings(now);

        if (!mentorExpiry.isEmpty()) {

            var mentorWithStudents = mentorExpiry.stream().collect(Collectors.groupingBy(StudentMentorMapping::getMentorId));
            studentMentorRepository.saveAll(
                mentorExpiry.stream().map(user -> {
                    user.setExpired(true);
                    return  user;
                }).collect(Collectors.toList())
            );

            mentorWithStudents.forEach((mentorId, mentorStudents) -> {
                mentorStudents.forEach( mentorStudent -> {
                    studentRatingDetailRepository.updateMappedByMentorIdAndStudentIdWithoutAcademy(mentorId, mentorStudent.getStudentId());
                });
            });

            log.info("Updating the Expiry status for student with mentor mapping......");
            log.info("Removing all existing student attendance with mentor mapping......");
        }
    }

    private void updateAcademyMappingExpiry(LocalDate now) {
        var academyExpiry = studentMentorAcademyRepository.getAllExpiryMappings(now);

        if (!academyExpiry.isEmpty()) {
            var mentorWithAcademyStudents = academyExpiry.stream()
                    .collect(Collectors.groupingBy(StudentMentorAcademyMapping::getAcademyId));

            studentMentorAcademyRepository.saveAll(
                academyExpiry.stream().map(user -> {
                    user.setExpired(true);
                    return  user;
                }).collect(Collectors.toList())
            );

            mentorWithAcademyStudents.forEach(((academyId, studentMentorAcademyMappings) -> {
                studentMentorAcademyMappings.forEach(academyStudent -> {
                    studentRatingDetailRepository.updateMappedByMentorIdAndStudentIdWithAcademy(
                            academyStudent.getMentorId(), academyStudent.getStudentId(), academyStudent.getAcademyName());
                });
            }));

            log.info("Updating the Expiry status for student with mentor and academy mapping......");
            log.info("Removing all existing student attendance with mentor mapping academy......");
        }
    }
}