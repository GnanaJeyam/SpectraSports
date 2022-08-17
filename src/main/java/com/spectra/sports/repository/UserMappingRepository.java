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
}