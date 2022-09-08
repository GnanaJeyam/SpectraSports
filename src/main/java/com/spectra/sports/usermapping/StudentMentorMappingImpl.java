package com.spectra.sports.usermapping;

import com.spectra.sports.dto.UserDto;
import com.spectra.sports.entity.StudentMentorMapping;
import com.spectra.sports.entity.User;
import com.spectra.sports.repository.StudentMentorRepository;
import com.spectra.sports.subscription.SubscriptionInfo;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.spectra.sports.constant.SpectraConstant.STUDENT_MENTOR;
import static com.spectra.sports.constant.SpectraConstant.USER;

@Component
public class StudentMentorMappingImpl implements Mapping {
    private final StudentMentorRepository studentMentorRepository;
    private final StudentRatingImpl studentRating;

    public StudentMentorMappingImpl(StudentMentorRepository studentMentorRepository,
                                    StudentRatingImpl studentRating) {
        this.studentMentorRepository = studentMentorRepository;
        this.studentRating = studentRating;
    }

    @Override
    public void updateMappingDetails(UserMappingRequest request) {
        var todayDate = LocalDate.now();
        var expiryDate = todayDate.plusMonths(request.totalMonths());

        var studentMentorMapping = new StudentMentorMapping();
        studentMentorMapping.setExpired(false);
        studentMentorMapping.setTagged(true);
        studentMentorMapping.setStartDate(todayDate);
        studentMentorMapping.setEndDate(expiryDate);
        studentMentorMapping.setPlan(request.plan());
        studentMentorMapping.setAmount(request.amount());
        studentMentorMapping.setSlot(request.slot());
        studentMentorMapping.setMentorId(request.mentorId());
        studentMentorMapping.setStudentId(request.studentId());
        studentMentorMapping.setMentorType(request.mentorType());
        studentMentorMapping.setSlotDays(request.slotDays());

        studentMentorRepository.save(studentMentorMapping);
        studentRating.updateStudentRatingDetails(request);
    }

    public Set<Long> getAllMentorIdsByStudent(Long studentId) {
        return studentMentorRepository.getAllMentorIdsByStudent(studentId)
                .stream().collect(Collectors.toSet());
    }

    public Optional<StudentMentorMapping> getMentorDetailByMentorIdAndStudentId(Long mentorId, Long studentId) {

        return studentMentorRepository.getMentorDetailByMentorIdAndStudentId(mentorId, studentId);
    }

    public Stream<UserDto> getAllMentorDetailsByStudentId(Long studentId) {

        var allMentorDetailsByStudent = studentMentorRepository.getAllMentorDetailsByStudent(studentId);

        return mergeSubscriptionDetails(allMentorDetailsByStudent);
    }

    public Stream<UserDto> getAllStudentDetailsByMentorId(Long mentorId) {

        var allMentorDetailsByStudent = studentMentorRepository.getAllStudentDetailsByMentorId(mentorId);

        return mergeSubscriptionDetails(allMentorDetailsByStudent);
    }

    private Stream<UserDto> mergeSubscriptionDetails(List<Map<String, Object>> users) {
        return users.stream()
                .map(userMap -> {
                    var user = (User) userMap.get(USER);
                    var studentMentor = (StudentMentorMapping) userMap.get(STUDENT_MENTOR);

                    user.setSubscriptionInfo(SubscriptionInfo.from(studentMentor));

                    return UserDto.from(user, true);
                });
    }
}