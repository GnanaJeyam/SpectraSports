package com.spectra.sports.repository;

import com.spectra.sports.entity.MentorAcademyMapping;
import com.spectra.sports.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MentorAcademyRepository extends JpaRepository<MentorAcademyMapping, Long> {

    @Query("""
       SELECT mentorAcademy.mentorId FROM MentorAcademyMapping mentorAcademy WHERE mentorAcademy.academyId = :academyId AND 
       mentorAcademy.mentorType = :mentorType AND mentorAcademy.tagged = true
    """)
    List<Long> getAllMentorIdsByAcademy(Long academyId, String mentorType);

    @Query("""
        SELECT user FROM User user INNER JOIN MentorAcademyMapping mentorAcademy ON user.userId = mentorAcademy.mentorId  
        WHERE mentorAcademy.academyId = :academyId AND mentorAcademy.tagged = true 
    """)
    List<User> getAllMentorDetailsByAcademyId(Long academyId);
}