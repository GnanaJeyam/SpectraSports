package com.spectra.sports.repository;

import com.spectra.sports.entity.StudentRatingDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface StudentRatingDetailRepository extends JpaRepository<StudentRatingDetail, Long> {

    @Query("""
            SELECT studentDetail from StudentRatingDetail studentDetail WHERE studentDetail.mentorId = :mentorId 
            ORDER BY studentDetail.studentRatingDetailId DESC
    """)
    List<StudentRatingDetail> getAllStudentAttendanceDetailsByMentorId(Long mentorId);

    @Query("""
            SELECT studentDetail from StudentRatingDetail studentDetail WHERE studentDetail.mentorId = :mentorId 
            AND studentDetail.studentId = :studentId AND studentDetail.academyName is null ORDER BY studentDetail.studentRatingDetailId DESC
    """)
    List<StudentRatingDetail> getAttendanceDetailsByStudentAndMentorId(Long studentId, Long mentorId);


    @Query("""
            SELECT studentDetail from StudentRatingDetail studentDetail WHERE studentDetail.mentorId = :mentorId 
            AND studentDetail.studentId = :studentId AND studentDetail.academyId = :academyId ORDER BY studentDetail.studentRatingDetailId DESC
    """)
    List<StudentRatingDetail> getAttendanceDetailsByStudentAndMentorAndAcademyId(Long studentId, Long mentorId, Long academyId);

    @Modifying
    @Transactional
    @Query("""
         DELETE from StudentRatingDetail studentDetail WHERE studentDetail.mentorId = :mentorId AND  
         studentDetail.studentId = :studentId AND studentDetail.academyName is null        
    """)
    void updateMappedByMentorIdAndStudentIdWithoutAcademy(Long mentorId, Long studentId);

    @Modifying
    @Transactional
    @Query("""
         DELETE from StudentRatingDetail studentDetail WHERE studentDetail.mentorId = :mentorId AND  
         studentDetail.studentId = :studentId AND studentDetail.academyName = :academyName        
    """)
    void updateMappedByMentorIdAndStudentIdWithAcademy(Long mentorId, Long studentId, String academyName);
}