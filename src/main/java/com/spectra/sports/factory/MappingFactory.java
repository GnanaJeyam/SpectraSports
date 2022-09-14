package com.spectra.sports.factory;

import com.spectra.sports.usermapping.*;
import org.springframework.stereotype.Component;

import static com.spectra.sports.util.NumberUtil.notZero;

@Component
public class MappingFactory {

    private final StudentMentorMappingImpl studentMentorMapping;
    private final StudentMentorAcademyMappingImpl studentMentorAcademyMappingImpl;
    private final MentorAcademyMappingImpl mentorAcademyMappingImpl;

    public MappingFactory(StudentMentorMappingImpl studentMentorMapping,
                          StudentMentorAcademyMappingImpl studentMentorAcademyMappingImpl,
                          MentorAcademyMappingImpl mentorAcademyMappingImpl) {
        this.studentMentorMapping = studentMentorMapping;
        this.studentMentorAcademyMappingImpl = studentMentorAcademyMappingImpl;
        this.mentorAcademyMappingImpl = mentorAcademyMappingImpl;
    }

    public Mapping getMapping(UserMappingRequest request) {
        var studentId = request.studentId();
        var mentorId = request.mentorId();
        var academyId = request.academyId();

        if (notZero(studentId, mentorId, academyId)) {
            return studentMentorAcademyMappingImpl;
        } else if (notZero(mentorId, academyId)) {
            return mentorAcademyMappingImpl;
        } else {
            return studentMentorMapping;
        }
    }
}