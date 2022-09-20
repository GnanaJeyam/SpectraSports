package com.spectra.sports.repository;

import com.spectra.sports.entity.StudentMentorMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface StudentMentorRepository extends JpaRepository<StudentMentorMapping, Long> {
    @Query("""
        SELECT studentMentor.mentorId FROM StudentMentorMapping studentMentor WHERE studentMentor.studentId = :studentId
         AND studentMentor.mentorType = :mentorType AND studentMentor.expired = false AND studentMentor.tagged = true
    """)
    List<Long> getAllMentorIdsByStudent(Long studentId, String mentorType);

    @Query("""
        SELECT studentMentor FROM StudentMentorMapping studentMentor WHERE studentMentor.studentId = :studentId
         AND studentMentor.mentorId = :mentorId AND studentMentor.expired = false AND studentMentor.tagged = true
    """)
    Optional<StudentMentorMapping> getMentorDetailByMentorIdAndStudentId(Long mentorId, Long studentId);

    @Query("""
        SELECT new Map(user as user, studentMentor as studentMentor) FROM User user INNER JOIN StudentMentorMapping studentMentor  
        ON user.userId = studentMentor.mentorId WHERE studentMentor.studentId = :studentId AND  
        studentMentor.expired = false AND studentMentor.tagged = true 
    """)
    List<Map<String, Object>> getAllMentorDetailsByStudent(Long studentId);

    @Query("""
        SELECT new Map(user as user, studentMentor as studentMentor) FROM User user INNER JOIN StudentMentorMapping studentMentor  
        ON user.userId = studentMentor.studentId WHERE studentMentor.mentorId = :mentorId AND  
        studentMentor.expired = false AND studentMentor.tagged = true 
    """)
    List<Map<String, Object>> getAllStudentDetailsByMentorId(Long mentorId);

    @Query("""
        SELECT studentMentor FROM StudentMentorMapping studentMentor WHERE studentMentor.endDate is not null
         AND studentMentor.endDate < :now AND studentMentor.expired = false
    """)
    List<StudentMentorMapping> getAllExpiryMappings(LocalDate now);
}