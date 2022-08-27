package com.spectra.sports.repository;

import com.spectra.sports.entity.StudentMentorAcademyMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface StudentMentorAcademyRepository extends JpaRepository<StudentMentorAcademyMapping, Long> {

    @Query("""
        SELECT DISTINCT stdMentorAcademy.academyId from StudentMentorAcademyMapping stdMentorAcademy 
         WHERE stdMentorAcademy.studentId = :studentId AND stdMentorAcademy.expired = false AND stdMentorAcademy.tagged = true      
    """)
    Set<Long> getAllAcademyByStudentId(Long studentId);

    @Query("""
        SELECT stdMentorAcademy.academyId from StudentMentorAcademyMapping stdMentorAcademy WHERE  
         stdMentorAcademy.academyId = :academyId AND ( stdMentorAcademy.studentId = :userId OR stdMentorAcademy.mentorId = :userId ) 
         AND stdMentorAcademy.expired = false AND stdMentorAcademy.tagged = true     
    """)
    Optional<List<Long>> getStudentOrMentorWithAcademyMappingExists(Long userId, Long academyId);

    @Query("""
        SELECT stdMentorAcademy from StudentMentorAcademyMapping stdMentorAcademy WHERE stdMentorAcademy.academyId = :academyId 
         AND stdMentorAcademy.mentorId = :mentorId AND stdMentorAcademy.expired = false AND stdMentorAcademy.tagged = true      
    """)
    Optional<StudentMentorAcademyMapping> getEntityByMentorAndAcademyId(Long mentorId, Long academyId);

    @Query("""
        SELECT new Map(user as user, studentMentorAcademy as studentMentorAcademy) FROM User user INNER JOIN  
        StudentMentorAcademyMapping studentMentorAcademy ON user.userId = studentMentorAcademy.academyId WHERE 
        studentMentorAcademy.studentId = :studentId AND studentMentorAcademy.expired = false AND studentMentorAcademy.tagged = true 
    """)
    List<Map<String, Object>> getAllAcademyDetailsByStudent(Long studentId);

    @Query("""
        SELECT new Map(user as user, studentMentorAcademy as studentMentorAcademy) FROM User user INNER JOIN  
        StudentMentorAcademyMapping studentMentorAcademy ON user.userId = studentMentorAcademy.studentId WHERE 
        studentMentorAcademy.mentorId = :mentorId AND studentMentorAcademy.expired = false AND studentMentorAcademy.tagged = true 
    """)
    List<Map<String, Object>> getAllAcademyStudentDetailsByMentorId(Long mentorId);

    @Query("""
        SELECT new Map(user as user, studentMentorAcademy as studentMentorAcademy) FROM User user INNER JOIN  
        StudentMentorAcademyMapping studentMentorAcademy ON user.userId = studentMentorAcademy.mentorId WHERE  
        studentMentorAcademy.academyId = :academyId AND studentMentorAcademy.expired = false AND studentMentorAcademy.tagged = true 
    """)
    List<Map<String, Object>> getAllMentorDetailsByAcademyId(Long academyId);

    @Query("""
        SELECT studentMentorAcademy FROM StudentMentorAcademyMapping studentMentorAcademy WHERE studentMentorAcademy.endDate is not null
         AND studentMentorAcademy.endDate < :now AND studentMentorAcademy.expired = false
    """)
    List<StudentMentorAcademyMapping> getAllExpiryMappings(LocalDate now);

}