package com.spectra.sports.usermapping;

import com.spectra.sports.dto.UserDto;
import com.spectra.sports.entity.StudentMentorAcademyMapping;
import com.spectra.sports.entity.User;
import com.spectra.sports.repository.StudentMentorAcademyRepository;
import com.spectra.sports.subscription.SubscriptionInfo;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static com.spectra.sports.constant.SpectraConstant.*;

@Component
public class StudentMentorAcademyMappingImpl implements Mapping {
    private final StudentMentorAcademyRepository studentMentorAcademyRepository;
    private final StudentRatingImpl studentRating;

    public StudentMentorAcademyMappingImpl(StudentMentorAcademyRepository studentMentorAcademyRepository,
                                           StudentRatingImpl studentRating) {
        this.studentMentorAcademyRepository = studentMentorAcademyRepository;
        this.studentRating = studentRating;
    }

    @Override
    public void updateMappingDetails(UserMappingRequest request) {
        var todayDate = LocalDate.now();
        var expiryDate = todayDate.plusMonths(request.totalMonths());

        var studentMentorAcademy = new StudentMentorAcademyMapping();
        studentMentorAcademy.setExpired(false);
        studentMentorAcademy.setTagged(true);
        studentMentorAcademy.setStartDate(todayDate);
        studentMentorAcademy.setEndDate(expiryDate);
        studentMentorAcademy.setPlan(request.plan());
        studentMentorAcademy.setAmount(request.amount());
        studentMentorAcademy.setSlot(request.slot());
        studentMentorAcademy.setAcademyId(request.academyId());
        studentMentorAcademy.setMentorId(request.mentorId());
        studentMentorAcademy.setStudentId(request.studentId());
        studentMentorAcademy.setMentorType(request.mentorType());
        studentMentorAcademy.setAcademyType(request.academyType());

        studentMentorAcademyRepository.save(studentMentorAcademy);
        studentRating.updateStudentRatingDetails(request.mentorId(), request.studentId());
    }

    public Set<Long> getAllAcademyByStudentId(Long studentId) {

        return studentMentorAcademyRepository.getAllAcademyByStudentId(studentId);
    }

    public Optional<StudentMentorAcademyMapping> getEntityByMentorAndAcademyId(Long mentorId, Long academyId) {

        return studentMentorAcademyRepository.getEntityByMentorAndAcademyId(mentorId, academyId);
    }

    public Stream<UserDto> getAllAcademyDetailsByStudent(Long studentId) {

        var allAcademyDetailsByStudent = studentMentorAcademyRepository.getAllAcademyDetailsByStudent(studentId);

        return mergeSubscriptionDetails(allAcademyDetailsByStudent);
    }

    public Stream<UserDto> getAllAcademyStudentDetailsByMentorId(Long mentorId) {

        var studentsByMentorId = studentMentorAcademyRepository.getAllAcademyStudentDetailsByMentorId(mentorId);

        return mergeSubscriptionDetails(studentsByMentorId);
    }

    public UserDto getStudentOrMentorWithAcademyMapping(Long studentId, Long academyId) {

        var academy = studentMentorAcademyRepository.getAcademyDetailWithMapping(studentId, academyId);
        var user = (User) academy.get(USER);
        var isMapped = (Boolean) academy.get(FLAG);

        return UserDto.from(user, isMapped);
    }

    private Stream<UserDto> mergeSubscriptionDetails(List<Map<String, Object>> users) {
        return users.stream()
                .map(userMap -> {
                    var user = (User) userMap.get(USER);
                    var studentMentorAcademy = (StudentMentorAcademyMapping) userMap.get(STUDENT_MENTOR_ACADEMY);

                    user.setSubscriptionInfo(SubscriptionInfo.from(studentMentorAcademy));

                    return UserDto.from(user, true);
                });
    }
}