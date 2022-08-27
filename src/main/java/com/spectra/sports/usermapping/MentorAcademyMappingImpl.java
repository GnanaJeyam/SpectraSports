package com.spectra.sports.usermapping;

import com.spectra.sports.entity.MentorAcademyMapping;
import com.spectra.sports.repository.MentorAcademyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class MentorAcademyMappingImpl implements Mapping {
    private final MentorAcademyRepository mentorAcademyRepository;

    public MentorAcademyMappingImpl(MentorAcademyRepository mentorAcademyRepository) {
        this.mentorAcademyRepository = mentorAcademyRepository;
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
}