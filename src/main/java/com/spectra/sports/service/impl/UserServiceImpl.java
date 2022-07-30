package com.spectra.sports.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.spectra.sports.constant.SpectraConstant;
import com.spectra.sports.dao.UserDao;
import com.spectra.sports.dto.UserDto;
import com.spectra.sports.entity.*;
import com.spectra.sports.helper.JwtHelper;
import com.spectra.sports.helper.UserContextHolder;
import com.spectra.sports.mapper.UserMapper;
import com.spectra.sports.repository.RoleRepository;
import com.spectra.sports.repository.StudentRatingDetailRepository;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.spectra.sports.constant.SpectraConstant.*;
import static com.spectra.sports.entity.RoleType.ACADEMY;
import static com.spectra.sports.entity.RoleType.MENTOR;
import static com.spectra.sports.response.SuccessResponse.defaultResponse;
import static com.spectra.sports.response.SuccessResponse.errorResponse;
import static java.time.DayOfWeek.*;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMappingRepository userMappingRepository;
    private final StudentRatingDetailRepository studentRatingDetailRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtHelper jwtHelper;
    private final EmailService emailService;
    private final UserDao userDao;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           UserMappingRepository userMappingRepository,
                           StudentRatingDetailRepository studentRatingDetailRepository,
                           BCryptPasswordEncoder bCryptPasswordEncoder,
                           JwtHelper jwtHelper,
                           EmailService emailService,
                           UserDao userDao) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userMappingRepository = userMappingRepository;
        this.studentRatingDetailRepository = studentRatingDetailRepository;
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
            var roleIds = createdUser.getRoles().stream().map(Role::getRoleId).collect(Collectors.toSet());
            user.setRoles(this.roleRepository.getRolesByIds(roleIds));
            var from = UserDto.from(createdUser);
            this.sendSignUpEmailOrOtpEmail((userDto) -> emailService.sendSignUpVerificationEmail(userDto), from);

            return new SuccessResponse<>(from, HttpStatus.OK.value(), false, "Sign up SuccessFul");
        } catch (Exception exception) {
            String message = "Duplicate User, Please try with different email or Number";
            return new SuccessResponse<>(Map.of(), HttpStatus.NOT_ACCEPTABLE.value(), true, message);
        }
    }

    public SuccessResponse<?> updateUser(User user) {
        Assert.notNull(user, "User Cannot be null");
        var existingUser = userRepository.getReferenceById(user.getUserId());
        user = UserMapper.mapUser(existingUser, user);
        var updatedUser = userRepository.saveAndFlush(user);

        return defaultResponse(UserDto.from(updatedUser), "User Updated SuccessFully");
    }

    @Override
    public SuccessResponse<?> updateStudentAttendance(StudentRatingDetail studentRatingDetail) {
        var studentRating = studentRatingDetailRepository.saveAndFlush(studentRatingDetail);

        return defaultResponse(studentRating, "Student attendance details are updated");
    }

    public SuccessResponse<?> getUserById(Long userId) {
        try {
            var user = userRepository.getReferenceById(userId);
            return new SuccessResponse(UserDto.from(user), HttpStatus.OK.value(), false, "Get by User Id");
        } catch (Exception exception) {
            return errorResponse(HttpStatus.NOT_FOUND.value(), "User Not Found");
        }
    }

    @Override
    public SuccessResponse<UserDto> getMentorDetailById(Long mentorId) {
        var currentUserId = UserContextHolder.getCurrentUser().userId();
        var mentorWithMappedFlag = userRepository.getMentorByIdWithCurrentUserMappedFlag(currentUserId, mentorId);

        var mentor = (User) mentorWithMappedFlag.get(USER);
        var flag = (Boolean) mentorWithMappedFlag.get(FLAG);

        return defaultResponse(UserDto.from(mentor, flag), "Get Mentor Detail by mentor id with mapped flag");
    }

    @Override
    public Map<String, ?> getAcademyDetailById(Long academyId) {
        var currentUserId = UserContextHolder.getCurrentUser().userId();
        var academyIdWithCurrentUserMappedFlag = userRepository.getAcademyIdWithCurrentUserMappedFlag(currentUserId, academyId);

        var academy = (User) academyIdWithCurrentUserMappedFlag.get(USER);
        var flag = (Boolean) academyIdWithCurrentUserMappedFlag.get(FLAG);
        var academyDto = UserDto.from(academy, flag);
        var mentors = userDao.getAllMentorsByAcademyId(currentUserId, academyId).stream();
        var bucket = new LinkedHashMap<Long, UserDto>();
        mentors.forEach(user -> {
            UserDto userDto = bucket.get(user.getUserId());
            if ((nonNull(userDto) && user.isMapped()) || isNull(userDto)) {
                bucket.put(user.getUserId(), UserDto.from(user));
            }
        });

        return Map.of(
            BODY, Map.of(SpectraConstant.ACADEMY, academyDto, SpectraConstant.MENTOR, bucket.values()),
            STATUS, HttpStatus.OK.value(),
            ERROR, false,
            MESSAGE,"Get Academy Detail by academy id with mapped flag"
        );
    }

    @Override
    public SuccessResponse<List<UserDto>> getNearByMentors() {
        var user = UserContextHolder.getCurrentUser();
        var latitude = user.latitude();
        var longitude = user.longitude();

        var nearByUsers = userDao.getAllUsers(latitude, longitude, user.userId());
        var nearByList = nearByUsers.stream().map(UserDto::from).collect(Collectors.toList());
        if( CollectionUtils.isEmpty(nearByUsers) ) {
            nearByList = getAllUsersByRole(MENTOR.name(), 1, 5);
        }

        return defaultResponse(nearByList, "Get All Nearby Mentors");
    }

    @Override
    public SuccessResponse<List<UserDto>> getAllAcademyWithMappedKey(Integer page, Integer limit) {
        var currentUser = UserContextHolder.getCurrentUser();
        var academies = userRepository.getAllAcademyWithMappedKey(currentUser.userId(),
                ACADEMY, PageRequest.of(page - 1, limit));
        var users = new LinkedHashMap<Long, UserDto>();
        academies.forEach(record -> {
            var user = (User) record.get(USER);
            var flag = (Boolean) record.get(FLAG);
            var existingUser = users.get(user.getUserId());
            if ((nonNull(existingUser) && flag) || isNull(existingUser)) {
                users.put(user.getUserId(), UserDto.from(user, flag));
            }
        });

        return defaultResponse(users.values(), "Get All Academy with mapped key");
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
            return errorResponse(HttpStatus.NOT_ACCEPTABLE.value(), "Invalid Username or Email.");
        }

        UserDto from = UserDto.from(user);
        sendSignUpEmailOrOtpEmail((userDto) -> emailService.sendForgotPasswordVerificationEmail(userDto), from);

        return defaultResponse(Map.of(), "Otp Sent to the User");
    }

    public SuccessResponse<String> validateOtp(Map<String, String> userDetails) {
        var email = userDetails.get(EMAIL);
        var otp = userDetails.get(OTP);
        var user = userRepository.getUserByUserName(email);

        SuccessResponse defaultResponse = defaultResponse(Map.of(), "Valid Otp");
        return ofNullable(validateAndGetUser(otp, user)).orElse(defaultResponse);
    }

    public SuccessResponse<String> resendVerificationEmail(String email) {
        var user = userRepository.getUserByUserName(email);
        var from = UserDto.from(user);
        emailService.sendSignUpVerificationEmail(from);

        return defaultResponse(Map.of(), "Verification Email Sent");
    }

    @Transactional
    public SuccessResponse<String> resetPassword(Map<String, String> userDetails) {
        var email = userDetails.get(EMAIL);
        var newPassword = userDetails.get(PASSWORD);
        var otp = userDetails.get(OTP);
        var user = userRepository.getUserByUserName(email);

        return ofNullable(validateAndGetUser(otp, user))
            .orElseGet(() -> {
                user.setPassword(bCryptPasswordEncoder.encode(newPassword));
                userRepository.saveAndFlush(user);
                return defaultResponse(Map.of(), "Password Updated");
        });
    }

    @Override
    public SuccessResponse<String> updateUserMapping(Map<String, String> userDetails) {
        var zero = "0";
        var studentId = Long.valueOf(ofNullable(userDetails.get(STUDENT_ID)).orElse(zero));
        var mentorId = Long.valueOf(ofNullable(userDetails.get(MENTOR_ID)).orElse(zero));
        var academyId = Long.valueOf(ofNullable(userDetails.get(ACADEMY_ID)).orElse(zero));

        var userMapping = new UserMapping();
        userMapping.setStudentId(studentId);
        userMapping.setAcademyId(academyId);
        userMapping.setMentorId(mentorId);

        userMappingRepository.save(userMapping);
        new Thread(() -> updateStudentRatingDetails(mentorId, studentId)).start();

        return defaultResponse(Map.of(), "User Mapping Added");
    }

    @Override
    public SuccessResponse<List<UserDto>> getMentorsByUser() {
        var currentUser = UserContextHolder.getCurrentUser();
        var currentRole = currentUser.roles().stream().findFirst().orElseThrow();
        var page = PageRequest.of(0, 20);
        var hasAcademy = ACADEMY.equals(currentRole.getRoleType());

        List<Map<String, Object>> mentors;
        if (hasAcademy) {
            mentors = userRepository.getAllMentorsByAcademy(currentUser.userId(), MENTOR, page);
        } else {
            mentors = userRepository.getAllMentorsByStudent(currentUser.userId(), MENTOR, page);
        }

        return defaultResponse(filterByAcademyOrStudent(mentors, hasAcademy), "Get All Mentors");
    }

    @Override
    public SuccessResponse<List<StudentRatingDetail>> getAllStudentAttendanceByMentorId(Long mentorId) {
        var studentAttendanceDetailsByMentorId = studentRatingDetailRepository.getAllStudentAttendanceDetailsByMentorId(mentorId);

        return defaultResponse(studentAttendanceDetailsByMentorId, "Get All students attendance detail by mentor id");
    }

    @Override
    public SuccessResponse<List<UserDto>> getAllMentorsByAcademy() {
        var currentUserId = UserContextHolder.getCurrentUser().userId();
        var allMentorsByAcademy = userRepository.getAllMentorsByAcademy(currentUserId, PageRequest.of(0, 20));
        var mapToUserDto = allMentorsByAcademy.stream().map(UserDto::from).collect(Collectors.toList());

        return defaultResponse(mapToUserDto, "Get All Mentors By Academy ID");
    }

    @Override
    public SuccessResponse<List<UserDto>> getAllMentorsAndAcademyByStudent() {
        var currentUser = UserContextHolder.getCurrentUser().userId();
        var page = PageRequest.of(0, 20);

        var academies = userRepository.getAllAcademyByStudentId(currentUser, page);
        var mentors = userRepository.getAllMentorsByStudentId(currentUser, page);

        var result = Map.of(
            SpectraConstant.ACADEMY, academies.stream().map(user -> UserDto.from(user, true)),
            SpectraConstant.MENTOR, mentors.stream().map(user -> UserDto.from(user, true))
        );

        return defaultResponse(result, "Get All Academy and Mentors By Student");
    }

    @Override
    public SuccessResponse<List<UserDto>> getAllUsersByNameOrSpecialistIn(String search) {
        var allUsersBySpecialistIn = userRepository.getAllUsersBySpecialistIn(search);
        var allUsersByName = userRepository.getAllUsersByName(search);
        var userDtoStream = Stream.of(allUsersBySpecialistIn, allUsersByName)
                .flatMap(Collection::stream)
                .distinct()
                .sorted(Comparator.comparing(User::getUserId))
                .map(UserDto::from);

        return defaultResponse(userDtoStream, "Get All Users by search");
    }

    private List<UserDto> filterByAcademyOrStudent(List<Map<String, Object>> users, boolean hasAcademy) {
        Map<Long, UserDto> bucket = new LinkedHashMap<>();
        for (Map<String, Object> userContext : users) {
            var user = (User) userContext.get(USER);
            var flag = (Boolean) userContext.get(FLAG);
            var academyId = (Long) userContext.get(ACADEMY_ID);
            var studentId = (Long) userContext.get(STUDENT_ID);
            var existingUser = bucket.get(user.getUserId());
            if (existingUser != null) {
                /**
                 * Doing this deduplication check to confirm whether the mentor has
                 * duplicate entry due to Student/Academy mapping.
                 * Removing the duplicate entry based on the hasAcademy flag.
                */
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

    private void updateStudentRatingDetails(Long mentorId, Long studentId) {
        if (studentId < 1) {
            return;
        }

        var user = userRepository.findById(studentId).orElseThrow();
        var studentRatingDetail = new StudentRatingDetail();
        studentRatingDetail.setRating("0/10");
        studentRatingDetail.setFullName(user.getFirstName() + " " + user.getLastName());
        studentRatingDetail.setMentorId(mentorId);
        studentRatingDetail.setStudentId(studentId);
        studentRatingDetail.setCreatedBy(mentorId);
        studentRatingDetail.setAttendances(List.of(
            Attendance.builder().day(SUNDAY.name()).build(),
            Attendance.builder().day(MONDAY.name()).build(),
            Attendance.builder().day(TUESDAY.name()).build(),
            Attendance.builder().day(WEDNESDAY.name()).build(),
            Attendance.builder().day(THURSDAY.name()).build(),
            Attendance.builder().day(FRIDAY.name()).build(),
            Attendance.builder().day(SATURDAY.name()).build()
        ));

        studentRatingDetailRepository.save(studentRatingDetail);
    }

    private Map<String, ? extends Object> getResponse(int status, String message) {
        return Map.of(BODY, Map.of(), STATUS, status, ERROR, true, MESSAGE, message);
    }

    private SuccessResponse validateAndGetUser(String otp, User user) {
        if (user == null) {
            return errorResponse(HttpStatus.NOT_ACCEPTABLE.value(), "Invalid Username or Email.");
        }
        if (!Objects.equals(user.getOtp(), otp)){
            return errorResponse(HttpStatus.NOT_ACCEPTABLE.value(), "Invalid otp");
        }

        return null;
    }

    private void sendSignUpEmailOrOtpEmail(Consumer<UserDto> consumer, UserDto from) {
        Runnable sendEmail = () -> consumer.accept(from);
        new Thread(sendEmail).start();
    }
}
