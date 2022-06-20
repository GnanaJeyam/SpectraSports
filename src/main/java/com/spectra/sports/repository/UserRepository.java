//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.spectra.sports.repository;

import com.spectra.sports.entity.RoleType;
import com.spectra.sports.entity.User;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT user from User user WHERE user.email = :username")
    User getUserByUserName(String username);

    @Query("    SELECT user from User user JOIN user.roles userRoles\n    WHERE userRoles.roleType = :role\n")
    List<User> getAllUsersByRole(RoleType role, Pageable pageable);

    @Modifying
    @Query("UPDATE User user set user.isVerified = true WHERE user.userId = :userId")
    int updateUserVerified(Long userId);

    @Modifying
    @Query("UPDATE User user set user.otp = :otp WHERE user.userId = :userId")
    int updateUserOtp(String otp, Long userId);
}
