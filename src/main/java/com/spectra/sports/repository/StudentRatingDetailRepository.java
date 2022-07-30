package com.spectra.sports.repository;

import com.spectra.sports.entity.StudentRatingDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRatingDetailRepository extends JpaRepository<StudentRatingDetail, Long> {

    @Query("""
            SELECT studentDetail from StudentRatingDetail studentDetail where studentDetail.mentorId = :mentorId
    """)
    List<StudentRatingDetail> getAllStudentAttendanceDetailsByMentorId(Long mentorId);
}