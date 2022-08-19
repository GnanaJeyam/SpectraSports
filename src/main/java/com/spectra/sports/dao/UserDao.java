package com.spectra.sports.dao;

import com.spectra.sports.entity.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class UserDao {
    private final static String NEARBY_MENTORS =  """
        SELECT *, false as flag FROM ( 
            SELECT u.* , (
              6371 * acos (
              cos ( radians( ? ) )
              * cos( radians( u.latitude  ) )
              * cos( radians( u.longitude ) - radians( ? ) )
              + sin ( radians( ? ) )
              * sin( radians( u.latitude  )
            ))) AS dist from users u JOIN user_roles uro on u.user_id = uro.user_id join
            roles r on uro.role_id = r.role_id  WHERE r.role_name = 'MENTOR' AND
            u.user_id <> ? ) as output 
         WHERE output.dist < 15
    """;
    private final static String GET_ALL_MENTORS_BY_ACADEMY_ID = """
        select u.*, ( case when um.student_id = ? then true else false end ) as flag
        from users u inner join user_mapping um on u.user_id = um.mentor_id where um.academy_id = ?    
    """;

    private final UserRowMapper USER_ROW_MAPPER = new UserRowMapper();
    private final JdbcTemplate jdbcTemplate;

    public UserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<User> getAllUsers(Double latitude, Double longitude, Long userId) {

        return jdbcTemplate.query(NEARBY_MENTORS, USER_ROW_MAPPER, new Object[] {latitude, longitude, latitude, userId});
    }

    public List<User> getAllMentorsByAcademyId(Long userId, Long academyId) {

        return jdbcTemplate.query(GET_ALL_MENTORS_BY_ACADEMY_ID, USER_ROW_MAPPER, new Object[]{userId, academyId});
    }

    class UserRowMapper implements RowMapper<User> {

        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setUserId(rs.getLong("user_id"));
            user.setDescription(rs.getString("description"));
            user.setEmail(rs.getString("email"));
            user.setExperience(rs.getString("experience"));
            user.setFirstName(rs.getString("first_name"));
            user.setLastName(rs.getString("last_name"));
            user.setLatitude(rs.getDouble("latitude"));
            user.setLongitude(rs.getDouble("longitude"));
            user.setLocation(rs.getString("location"));
            user.setMobileNumber(rs.getString("mobile_number"));
            user.setImageName(rs.getString("image_name"));
            String specialistIn = rs.getString("specialistin");
            user.setSpecialistIn(specialistIn.replaceAll("\\{|}", "").split(","));
            user.setMapped(rs.getBoolean("flag"));

            return user;
        }
    }
}
