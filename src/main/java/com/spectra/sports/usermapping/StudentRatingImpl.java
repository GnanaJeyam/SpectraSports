package com.spectra.sports.usermapping;

import com.spectra.sports.entity.Attendance;
import com.spectra.sports.entity.StudentRatingDetail;
import com.spectra.sports.repository.StudentRatingDetailRepository;
import com.spectra.sports.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.time.DayOfWeek.*;

@Component
public class StudentRatingImpl {
    private final StudentRatingDetailRepository studentRatingDetailRepository;
    private final UserRepository userRepository;

    public StudentRatingImpl(StudentRatingDetailRepository studentRatingDetailRepository,
                             UserRepository userRepository) {
        this.studentRatingDetailRepository = studentRatingDetailRepository;
        this.userRepository = userRepository;
    }

    public void updateStudentRatingDetails(Long mentorId, Long studentId) {
        var user = userRepository.findById(studentId).orElseThrow();
        var studentRatingDetail = new StudentRatingDetail();
        studentRatingDetail.setRating("0/10");
        studentRatingDetail.setFullName(user.getFirstName() + " " + user.getLastName());
        studentRatingDetail.setMentorId(mentorId);
        studentRatingDetail.setStudentId(studentId);
        studentRatingDetail.setCreatedBy(mentorId);
        studentRatingDetail.setAttendances(getAttendanceList());

        studentRatingDetailRepository.save(studentRatingDetail);
    }

    private List<Attendance> getAttendanceList() {
        return List.of(
                Attendance.builder().day(SUNDAY.name()).build(),
                Attendance.builder().day(MONDAY.name()).build(),
                Attendance.builder().day(TUESDAY.name()).build(),
                Attendance.builder().day(WEDNESDAY.name()).build(),
                Attendance.builder().day(THURSDAY.name()).build(),
                Attendance.builder().day(FRIDAY.name()).build(),
                Attendance.builder().day(SATURDAY.name()).build()
        );
    }
}