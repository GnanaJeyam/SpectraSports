package com.spectra.sports.repository;

import com.spectra.sports.entity.UserMapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserMappingRepository extends JpaRepository<UserMapping, Long> {
}