package com.spectra.sports.repository;

import com.spectra.sports.entity.RoleType;
import com.spectra.sports.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT user from User user WHERE user.email = :username")
    User getUserByUserName(String username);

    @Query("SELECT user from User user JOIN user.roles userRoles WHERE userRoles.roleType = :role ")
    List<User> getAllUsersByRole(RoleType role, Pageable pageable);

    @Modifying
    @Query("UPDATE User user set user.isVerified = true WHERE user.userId = :userId")
    int updateUserVerified(Long userId);

    @Modifying
    @Query("UPDATE User user set user.otp = :otp WHERE user.userId = :userId")
    int updateUserOtp(String otp, Long userId);
}
