package com.spectra.sports.usermapping;

import com.spectra.sports.entity.Attendance;
import com.spectra.sports.entity.StudentRatingDetail;
import com.spectra.sports.repository.StudentRatingDetailRepository;
import com.spectra.sports.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.spectra.sports.constant.SpectraConstant.*;

@Component
public class StudentRatingImpl {
    private final StudentRatingDetailRepository studentRatingDetailRepository;
    private final UserRepository userRepository;

    public StudentRatingImpl(StudentRatingDetailRepository studentRatingDetailRepository,
                             UserRepository userRepository) {
        this.studentRatingDetailRepository = studentRatingDetailRepository;
        this.userRepository = userRepository;
    }

    public void updateStudentRatingDetails(UserMappingRequest request) {
        var user = userRepository.findById(request.studentId()).orElseThrow();
        var studentRatingDetail = new StudentRatingDetail();
        studentRatingDetail.setRating("0/10");
        studentRatingDetail.setFullName(user.getFirstName() + " " + user.getLastName());
        studentRatingDetail.setMentorId(request.mentorId());
        studentRatingDetail.setStudentId(request.studentId());
        studentRatingDetail.setCreatedBy(request.mentorId());
        studentRatingDetail.setAttendances(getAttendanceList());
        studentRatingDetail.setAcademyId(request.academyId());
        studentRatingDetail.setAcademyName(request.academyName());
        studentRatingDetail.setSlot(request.slot());
        studentRatingDetail.setSlotDays(request.slotDays());

        studentRatingDetailRepository.save(studentRatingDetail);
    }

    private List<Attendance> getAttendanceList() {
        return List.of(
                Attendance.builder().day(SUNDAY).build(),
                Attendance.builder().day(MONDAY).build(),
                Attendance.builder().day(TUESDAY).build(),
                Attendance.builder().day(WEDNESDAY).build(),
                Attendance.builder().day(THURSDAY).build(),
                Attendance.builder().day(FRIDAY).build(),
                Attendance.builder().day(SATURDAY).build()
        );
    }
}