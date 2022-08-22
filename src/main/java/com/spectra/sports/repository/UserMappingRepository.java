package com.spectra.sports.repository;

import com.spectra.sports.entity.UserMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface UserMappingRepository extends JpaRepository<UserMapping, Long> {

    @Query("""
        SELECT userMapping from UserMapping userMapping WHERE userMapping.endDate is not null  
        AND userMapping.endDate < :date AND userMapping.expired <> true 
    """)
    List<UserMapping> getAllExpiryPlans(LocalDate date);

    @Query("""
        SELECT userMapping from UserMapping userMapping WHERE userMapping.studentId = :studentId        
    """)
    List<UserMapping> getAllUserMappingsByStudentId(Long studentId);

    @Query("""
        SELECT userMapping from UserMapping userMapping WHERE userMapping.studentId = :studentId AND 
        userMapping.academyId IS NOT NULL AND userMapping.academyId > 0        
    """)
    List<UserMapping> getAllUserMappingsWithAcademyByStudentId(Long studentId);

    @Query("""
        SELECT userMapping from UserMapping userMapping WHERE userMapping.mentorId = :mentorId       
    """)
    List<UserMapping> getAllStudentsByMentorId(Long mentorId);

    @Query("""
        SELECT userMapping from UserMapping userMapping WHERE userMapping.academyId = :academyId       
    """)
    List<UserMapping> getAllUserMappingByAcademyId(Long academyId);

    @Query("""
        SELECT userMapping from UserMapping userMapping WHERE userMapping.mentorId = :mentorId 
         AND userMapping.studentId = :studentId       
    """)
    List<UserMapping> getAllUserMappingByMentorAndStudentId(Long mentorId, Long studentId);

    @Query("""
        SELECT userMapping from UserMapping userMapping WHERE userMapping.mentorId = :mentorId 
        AND userMapping.academyId = :academyId      
    """)
    List<UserMapping> getAllUserMappingByMentorAndAcademyId(Long mentorId, Long academyId);
}