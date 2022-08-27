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
        SELECT * FROM ( 
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

    private final UserRowMapper USER_ROW_MAPPER = new UserRowMapper();
    private final JdbcTemplate jdbcTemplate;

    public UserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<User> getAllUsers(Double latitude, Double longitude, Long userId) {

        return jdbcTemplate.query(NEARBY_MENTORS, USER_ROW_MAPPER, new Object[] {latitude, longitude, latitude, userId});
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

            return user;
        }
    }
}
