package com.spectra.sports.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.spectra.sports.dao.UserDao;
import com.spectra.sports.dto.UserDto;
import com.spectra.sports.entity.Role;
import com.spectra.sports.entity.RoleType;
import com.spectra.sports.entity.User;
import com.spectra.sports.entity.UserMapping;
import com.spectra.sports.helper.JwtHelper;
import com.spectra.sports.helper.UserContextHolder;
import com.spectra.sports.mapper.UserMapper;
import com.spectra.sports.repository.RoleRepository;
import com.spectra.sports.repository.UserMappingRepository;
import com.spectra.sports.repository.UserRepository;
import com.spectra.sports.response.SuccessResponse;
import com.spectra.sports.service.EmailService;
import com.spectra.sports.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.spectra.sports.constant.SpectraConstant.*;
import static com.spectra.sports.entity.RoleType.ACADEMY;
import static com.spectra.sports.entity.RoleType.MENTOR;
import static java.util.Objects.nonNull;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMappingRepository userMappingRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtHelper jwtHelper;
    private final EmailService emailService;
    private final UserDao userDao;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           UserMappingRepository userMappingRepository,
                           BCryptPasswordEncoder bCryptPasswordEncoder,
                           JwtHelper jwtHelper,
                           EmailService emailService,
                           UserDao userDao) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userMappingRepository = userMappingRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtHelper = jwtHelper;
        this.emailService = emailService;
        this.userDao = userDao;
    }

    public SuccessResponse<?> addUser(User user) {
        Assert.notNull(user, "User Cannot be null");
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setIsVerified(false);

        try {
            var createdUser =  userRepository.save(user);
            var roleIds = (Set)createdUser.getRoles().stream().map(Role::getRoleId).collect(Collectors.toSet());
            user.setRoles(this.roleRepository.getRolesByIds(roleIds));
            var from = UserDto.from(createdUser);
            this.sendSignUpEmailOrOtpEmail((userDto) -> emailService.sendSignUpVerificationEmail(userDto), from);

            return new SuccessResponse(from, HttpStatus.OK.value(), false, "Sign up SuccessFul");
        } catch (Exception exception) {
            String message = "Duplicate User, Please try with different email or Number";
            return new SuccessResponse(Map.of(), HttpStatus.NOT_ACCEPTABLE.value(), true, message);
        }
    }

    public SuccessResponse<?> updateUser(User user) {
        Assert.notNull(user, "User Cannot be null");
        var existingUser = userRepository.getReferenceById(user.getUserId());
        user = UserMapper.mapUser(existingUser, user);
        var updatedUser = userRepository.saveAndFlush(user);

        return SuccessResponse.defaultResponse(UserDto.from(updatedUser), "User Updated SuccessFully");
    }

    public SuccessResponse<?> getUserById(Long userId) {
        try {
            var user = userRepository.getReferenceById(userId);
            return new SuccessResponse(UserDto.from(user), HttpStatus.OK.value(), false, "Get by User Id");
        } catch (Exception exception) {
            return SuccessResponse.errorResponse(HttpStatus.NOT_FOUND.value(), "User Not Found");
        }
    }

    @Override
    public SuccessResponse<List<UserDto>> getNearByMentors() {
        var user = UserContextHolder.getUser();
        var latitude = user.latitude();
        var longitude = user.longitude();

        var nearByUsers = userDao.getAllUsers(latitude, longitude, user.userId());
        var nearByList = nearByUsers.stream().map(UserDto::from).collect(Collectors.toList());
        if( CollectionUtils.isEmpty(nearByUsers) ) {
            nearByList = getAllUsersByRole(MENTOR.name(), 1, 5);
        }

        return SuccessResponse.defaultResponse(nearByList, "Get All Nearby Mentors");
    }

    public Map<String, ? extends Object> signInUser(Map<String, String> credentials) throws JsonProcessingException {
        var username = credentials.get(USERNAME);
        var password = credentials.get(PASSWORD);
        var user = this.userRepository.getUserByUserName(username);

        if (user == null) {
            return getResponse(HttpStatus.UNAUTHORIZED.value(), "Invalid User");
        }
        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            return getResponse(HttpStatus.UNAUTHORIZED.value(), "Invalid username or password");
        }
        if (!user.getIsVerified()) {
            return getResponse(HttpStatus.NOT_ACCEPTABLE.value(), "User not verified yet.");
        }

        var from = UserDto.from(user);
        return Map.of(
            BODY, from,
            STATUS, HttpStatus.OK.value(),
            ERROR, false,
            MESSAGE, "Sign in Succeed",
            "accessToken", jwtHelper.createToken(from)
        );
    }

    public List<UserDto> getAllUsersByRole(String role, Integer page, Integer limit) {
        Assert.notNull(role, "Role cannot be null");
        var roleType = RoleType.valueOf(role.toUpperCase());
        var pageable = PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, "userId"));
        var allUsersByRole = userRepository.getAllUsersByRole(roleType, pageable);

        return allUsersByRole.stream().map(UserDto::from).collect(Collectors.toList());
    }

    @Transactional
    public String verifyUser(String token) {
        UserDto userDto;
        try {
            userDto = jwtHelper.parseToken(token);
        } catch (Exception var4) {
            return "<b>Invalid User. Please try again</b>";
        }

        userRepository.updateUserVerified(userDto.userId());
        String verifiedMessage = """
            <html>
            <head>
            <title> SpectraSports </title>
            </head>
            <body>
            <b> Congratulations, %s! Your account has been verified </b>
            </body>
            </html
            """.formatted(userDto.firstName());

        return verifiedMessage;
    }

    public SuccessResponse<String> sendEmailOtp(String email) {
        var user = userRepository.getUserByUserName(email);
        if (user == null) {
            return SuccessResponse.errorResponse(HttpStatus.NOT_ACCEPTABLE.value(), "Invalid Username or Email.");
        }

        UserDto from = UserDto.from(user);
        sendSignUpEmailOrOtpEmail((userDto) -> emailService.sendForgotPasswordVerificationEmail(userDto), from);

        return SuccessResponse.defaultResponse(Map.of(), "Otp Sent to the User");
    }

    public SuccessResponse<String> validateOtp(Map<String, String> userDetails) {
        var email = userDetails.get(EMAIL);
        var otp = userDetails.get(OTP);
        var user = userRepository.getUserByUserName(email);

        SuccessResponse defaultResponse = SuccessResponse.defaultResponse(Map.of(), "Valid Otp");
        return Optional.ofNullable(validateAndGetUser(otp, user)).orElse(defaultResponse);
    }

    public SuccessResponse<String> resendVerificationEmail(String email) {
        var user = userRepository.getUserByUserName(email);
        var from = UserDto.from(user);
        emailService.sendSignUpVerificationEmail(from);

        return SuccessResponse.defaultResponse(Map.of(), "Verification Email Sent");
    }

    @Transactional
    public SuccessResponse<String> resetPassword(Map<String, String> userDetails) {
        var email = userDetails.get(EMAIL);
        var newPassword = userDetails.get(PASSWORD);
        var otp = userDetails.get(OTP);
        var user = userRepository.getUserByUserName(email);

        return Optional.ofNullable(validateAndGetUser(otp, user))
            .orElseGet(() -> {
                user.setPassword(bCryptPasswordEncoder.encode(newPassword));
                userRepository.saveAndFlush(user);
                return SuccessResponse.defaultResponse(Map.of(), "Password Updated");
        });
    }

    @Override
    public SuccessResponse<String> updateUserMapping(Map<String, String> userDetails) {
        var studentId = userDetails.get(STUDENT_ID);
        var mentorId = userDetails.get(MENTOR_ID);
        var academyId = userDetails.get(ACADEMY_ID);

        Function<String, Long> applyDefaultValue = (val) -> {
            if (val == null) return 0l;
            return Long.valueOf(val);
        };

        var userMapping = new UserMapping();
        userMapping.setStudentId(applyDefaultValue.apply(studentId));
        userMapping.setAcademyId(applyDefaultValue.apply(academyId));
        userMapping.setMentorId(applyDefaultValue.apply(mentorId));

        userMappingRepository.save(userMapping);

        return SuccessResponse.defaultResponse(Map.of(), "User Mapping Added");
    }

    @Override
    public SuccessResponse<List<UserDto>> getMentorsByUser() {
        var currentUser = UserContextHolder.getUser();
        var currentRole = currentUser.roles().stream().findFirst().orElseThrow();
        var page = PageRequest.of(0, 20);
        var hasAcademy = ACADEMY.equals(currentRole.getRoleType());

        List<Map<String, Object>> mentors;
        if (hasAcademy) {
            mentors = userRepository.getAllMentorsByAcademy(currentUser.userId(), MENTOR, page);
        } else {
            mentors = userRepository.getAllMentorsByStudent(currentUser.userId(), MENTOR, page);
        }

        return SuccessResponse.defaultResponse(filterByAcademyOrStudent(mentors, hasAcademy), "Get All Mentors");
    }

    @Override
    public SuccessResponse<List<UserDto>> getAllMentorsByAcademy() {
        var currentUserId = UserContextHolder.getUser().userId();
        var allMentorsByAcademy = userRepository.getAllMentorsByAcademy(currentUserId, PageRequest.of(0, 20));
        var mapToUserDto = allMentorsByAcademy.stream().map(UserDto::from).collect(Collectors.toList());

        return SuccessResponse.defaultResponse(mapToUserDto, "Get All Mentors By Academy ID");
    }

    @Override
    public SuccessResponse<List<UserDto>> getAllMentorsOrAcademyByRole(String roleType) {
        Assert.notNull(roleType, "Role Type Cannot be null");
        var role = RoleType.valueOf(roleType);
        var currentUser = UserContextHolder.getUser().userId();
        List<User> users = null;
        var page = PageRequest.of(0, 20);

        if (ACADEMY.equals(role)) {
            users = userRepository.getAllAcademyByStudentId(currentUser, page);
        } else {
            users = userRepository.getAllMentorsByStudentId(currentUser, page);
        }

        var userDto = users.stream().map(UserDto::from).collect(Collectors.toList());

        return SuccessResponse.defaultResponse(userDto, "Get All Academy/Mentors By Student");
    }

    private List<UserDto> filterByAcademyOrStudent(List<Map<String, Object>> users, boolean hasAcademy) {
        Map<Long, UserDto> bucket = new LinkedHashMap<>();
        for (Map<String, Object> userContext : users) {
            var user = (User) userContext.get("user");
            var flag = (Boolean) userContext.get("flag");
            var academyId = (Long) userContext.get(ACADEMY_ID);
            var studentId = (Long) userContext.get(STUDENT_ID);
            var existingUser = bucket.get(user.getUserId());
            if (existingUser != null) {
                var academyOrStudentId = hasAcademy ? academyId : studentId;
                if ( nonNull(academyOrStudentId) && academyOrStudentId > 0l ) {
                    bucket.put(user.getUserId(), UserDto.from(user, flag));
                }
            } else {
                bucket.put(user.getUserId(), UserDto.from(user, flag));
            }
        }

        return bucket.values().stream().toList();
    }

    private Map<String, ? extends Object> getResponse(int status, String message) {
        return Map.of(BODY, Map.of(), STATUS, status, ERROR, true, MESSAGE, message);
    }

    private SuccessResponse validateAndGetUser(String otp, User user) {
        if (user == null) {
            return SuccessResponse.errorResponse(HttpStatus.NOT_ACCEPTABLE.value(), "Invalid Username or Email.");
        }
        if (!Objects.equals(user.getOtp(), otp)){
            return SuccessResponse.errorResponse(HttpStatus.NOT_ACCEPTABLE.value(), "Invalid otp");
        }

        return null;
    }

    private void sendSignUpEmailOrOtpEmail(Consumer<UserDto> consumer, UserDto from) {
        Runnable sendEmail = () -> consumer.accept(from);
        new Thread(sendEmail).start();
    }
}
