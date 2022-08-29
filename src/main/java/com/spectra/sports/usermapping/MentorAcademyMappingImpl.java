package com.spectra.sports.usermapping;

import com.spectra.sports.dto.UserDto;
import com.spectra.sports.entity.MentorAcademyMapping;
import com.spectra.sports.repository.MentorAcademyRepository;
import com.spectra.sports.repository.StudentMentorAcademyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Slf4j
public class MentorAcademyMappingImpl implements Mapping {
    private final MentorAcademyRepository mentorAcademyRepository;
    private final StudentMentorAcademyRepository studentMentorAcademyRepository;

    public MentorAcademyMappingImpl(MentorAcademyRepository mentorAcademyRepository,
                                    StudentMentorAcademyRepository studentMentorAcademyRepository) {
        this.mentorAcademyRepository = mentorAcademyRepository;
        this.studentMentorAcademyRepository = studentMentorAcademyRepository;
    }

    @Override
    public void updateMappingDetails(UserMappingRequest request) {
        var mentorAcademyMapping = new MentorAcademyMapping();
        mentorAcademyMapping.setTagged(true);
        mentorAcademyMapping.setMentorId(request.mentorId());
        mentorAcademyMapping.setAcademyId(request.academyId());
        mentorAcademyMapping.setMentorType(request.mentorType());
        mentorAcademyMapping.setAcademyType(request.academyType());

        mentorAcademyRepository.save(mentorAcademyMapping);
        log.info("Saved Mentor and Academy mapping for Academy {} and Mentor {} ", request.academyId(), request.mentorId());
    }

    public Set<Long> getAllMentorIdsByAcademy(Long academyId) {
        return mentorAcademyRepository
                .getAllMentorIdsByAcademy(academyId)
                .stream()
                .collect(Collectors.toSet());
    }

    public Stream<UserDto> getAllMentorDetailsByAcademyId(Long academyId, Long studentId) {
        var mentors = mentorAcademyRepository.getAllMentorDetailsByAcademyId(academyId);
        var mentorIdsByStudent = studentMentorAcademyRepository.getAllMentorByStudentAndAcademyId(studentId, academyId);

        return mentors.stream().map(user -> UserDto.from(user, mentorIdsByStudent.contains(user.getUserId())));
    }
}