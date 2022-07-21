package com.spectra.sports.repository;

import com.spectra.sports.entity.RoleType;
import com.spectra.sports.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT user from User user WHERE user.email = :username")
    User getUserByUserName(String username);

    @Query("SELECT user from User user JOIN user.roles userRoles WHERE userRoles.roleType = :role ")
    List<User> getAllUsersByRole(RoleType role, Pageable pageable);

    @Query("""
                    SELECT new Map(user as user, userMapping.academyId as academyId, userMapping.studentId as studentId,
                      ( CASE 
                        WHEN userMapping.studentId = :userId THEN true else false 
                      END ) as flag )  
                     from User user 
                     LEFT JOIN UserMapping userMapping 
                     ON user.userId = userMapping.mentorId
                     JOIN user.roles userRoles
                     WHERE userRoles.roleType = :role
                     GROUP BY user.userId, userMapping.studentId, userMapping.mentorId, userMapping.academyId
                     ORDER BY user.userId DESC  
            """)
    List<Map<String, Object>> getAllMentorsByStudent(Long userId, RoleType role, Pageable pageable);

    @Query("""
                    SELECT new Map(user as user, userMapping.academyId as academyId,   
                        userMapping.studentId as studentId,
                        ( CASE WHEN userMapping.academyId = :userId
                        THEN true else false END ) as flag )  
                    from User user LEFT JOIN UserMapping userMapping 
                    ON user.userId = userMapping.mentorId
                    JOIN user.roles userRoles
                     WHERE userRoles.roleType = :role
                     GROUP BY user.userId, userMapping.studentId, userMapping.mentorId, userMapping.academyId
                     ORDER BY user.userId DESC  
            """)
    List<Map<String, Object>> getAllMentorsByAcademy(Long userId, RoleType role, Pageable pageable);

    @Query("""
                    SELECT new Map( user as user,   
                        ( CASE WHEN userMapping.studentId = :userId
                        THEN true else false END ) as flag )  
                    from User user LEFT JOIN UserMapping userMapping 
                    ON user.userId = userMapping.academyId
                    JOIN user.roles userRoles
                     WHERE userRoles.roleType = :role
                     ORDER BY user.userId DESC  
            """)
    List<Map<String, Object>> getAllAcademyWithMappedKey(Long userId, RoleType role, Pageable pageable);

    @Query("""
                    SELECT user from User user where user.userId IN 
                    ( SELECT userMapping.mentorId from UserMapping userMapping WHERE
                        userMapping.academyId = :academyId ) 
            """)
    List<User> getAllMentorsByAcademy(Long academyId, Pageable pageable);

    @Query("""
                    SELECT new Map( user as user, CASE WHEN EXISTS ( SELECT userMapping.studentId from UserMapping userMapping   
                    WHERE userMapping.mentorId = :mentorId AND userMapping.studentId = :userId ) THEN true ELSE false END as flag )
                    from User user where user.userId = :mentorId
            """)
    Map<String, Object> getMentorByIdWithCurrentUserMappedFlag(Long userId, Long mentorId);

    @Query("""
                    SELECT new Map( user as user, CASE WHEN EXISTS ( SELECT userMapping.studentId from UserMapping userMapping   
                    WHERE userMapping.academyId = :academyId AND userMapping.studentId = :userId ) THEN true ELSE false END as flag )
                    from User user where user.userId = :academyId
            """)
    Map<String, Object> getAcademyIdWithCurrentUserMappedFlag(Long userId, Long academyId);

    @Query("""
                SELECT user from User user where user.userId IN 
                ( SELECT userMapping.mentorId from UserMapping userMapping WHERE
                    userMapping.studentId = :studentId ) 
            """)
    List<User> getAllMentorsByStudentId(Long studentId, Pageable pageable);

    @Query("""
                SELECT user from User user where user.userId IN 
                ( SELECT userMapping.academyId from UserMapping userMapping WHERE
                    userMapping.studentId = :studentId AND userMapping.academyId IS NOT NULL ) 
            """)
    List<User> getAllAcademyByStudentId(Long studentId, Pageable pageable);

    @Query(value = """
            SELECT usr.* from users usr where ( :searchKey Ilike  any (usr.specialistin) ) limit 20  
        """, nativeQuery = true)
    List<User> getAllUsersBySpecialistIn(String searchKey);

    @Query(value = """
            SELECT usr.* from users usr where ( usr.first_name Ilike %:searchKey% ) limit 20
        """, nativeQuery = true)
    List<User> getAllUsersByName(String searchKey);

    @Modifying
    @Query("UPDATE User user set user.isVerified = true WHERE user.userId = :userId")
    int updateUserVerified(Long userId);

    @Modifying
    @Query("UPDATE User user set user.otp = :otp WHERE user.userId = :userId")
    int updateUserOtp(String otp, Long userId);
}
