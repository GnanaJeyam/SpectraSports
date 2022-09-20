package com.spectra.sports.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.spectra.sports.constant.SpectraConstant;
import com.spectra.sports.dao.UserDao;
import com.spectra.sports.dto.StudentAttendanceRequest;
import com.spectra.sports.dto.UserDto;
import com.spectra.sports.entity.Role;
import com.spectra.sports.entity.RoleType;
import com.spectra.sports.entity.StudentRatingDetail;
import com.spectra.sports.entity.User;
import com.spectra.sports.factory.MappingFactory;
import com.spectra.sports.helper.JwtHelper;
import com.spectra.sports.helper.UserContextHolder;
import com.spectra.sports.mapper.UserMapper;
import com.spectra.sports.repository.RoleRepository;
import com.spectra.sports.repository.StudentRatingDetailRepository;
import com.spectra.sports.repository.UserRepository;
import com.spectra.sports.response.SuccessResponse;
import com.spectra.sports.service.EmailService;
import com.spectra.sports.service.UserService;
import com.spectra.sports.subscription.SubscriptionInfo;
import com.spectra.sports.usermapping.MentorAcademyMappingImpl;
import com.spectra.sports.usermapping.StudentMentorAcademyMappingImpl;
import com.spectra.sports.usermapping.StudentMentorMappingImpl;
import com.spectra.sports.usermapping.UserMappingRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.spectra.sports.constant.SpectraConstant.*;
import static com.spectra.sports.constant.SuccessOrErrorMessages.*;
import static com.spectra.sports.entity.RoleType.ACADEMY;
import static com.spectra.sports.entity.RoleType.MENTOR;
import static com.spectra.sports.entity.RoleType.*;
import static com.spectra.sports.response.SuccessResponse.defaultResponse;
import static com.spectra.sports.response.SuccessResponse.errorResponse;
import static com.spectra.sports.util.NumberUtil.*;
import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static org.springframework.util.CollectionUtils.isEmpty;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final StudentRatingDetailRepository studentRatingDetailRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtHelper jwtHelper;
    private final EmailService emailService;
    private final UserDao userDao;
    private final MappingFactory mappingFactory;
    private final StudentMentorMappingImpl studentMentorMapping;
    private final MentorAcademyMappingImpl mentorAcademy;
    private final StudentMentorAcademyMappingImpl studentMentorAcademy;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           StudentRatingDetailRepository studentRatingDetailRepository,
                           BCryptPasswordEncoder bCryptPasswordEncoder,
                           JwtHelper jwtHelper,
                           EmailService emailService,
                           UserDao userDao, MappingFactory mappingFactory,
                           StudentMentorMappingImpl studentMentorMapping,
                           MentorAcademyMappingImpl mentorAcademy,
                           StudentMentorAcademyMappingImpl studentMentorAcademy) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.studentRatingDetailRepository = studentRatingDetailRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtHelper = jwtHelper;
        this.emailService = emailService;
        this.userDao = userDao;
        this.mappingFactory = mappingFactory;
        this.studentMentorMapping = studentMentorMapping;
        this.mentorAcademy = mentorAcademy;
        this.studentMentorAcademy = studentMentorAcademy;
    }

    public SuccessResponse<?> addUser(User user) {
        Assert.notNull(user, USER_CANNOT_BE_NULL);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setIsVerified(false);

        try {
            var createdUser = userRepository.save(user);
            var roleIds = createdUser.getRoles().stream().map(Role::getRoleId).collect(Collectors.toSet());
            user.setRoles(roleRepository.getRolesByIds(roleIds));
            var from = UserDto.from(createdUser);
            sendSignUpEmailOrOtpEmail(emailService::sendSignUpVerificationEmail, from);

            return new SuccessResponse<>(from, HttpStatus.OK.value(), false, SIGN_UP_SUCCESS_FUL);
        } catch (Exception exception) {
            String message = DUPLICATE_USER;
            return new SuccessResponse<>(Map.of(), HttpStatus.NOT_ACCEPTABLE.value(), true, message);
        }
    }

    public SuccessResponse<?> updateUser(User user) {
        Assert.notNull(user, USER_CANNOT_BE_NULL);
        var existingUser = userRepository.getReferenceById(user.getUserId());
        user = UserMapper.mapUser(existingUser, user);
        var updatedUser = userRepository.saveAndFlush(user);

        return defaultResponse(UserDto.from(updatedUser), USER_UPDATED);
    }

    @Override
    public SuccessResponse<?> updateStudentAttendance(StudentRatingDetail studentRatingDetail) {
        Assert.notNull(studentRatingDetail.getStudentRatingDetailId(), STUDENT_RATING_DETAIL_ID_CANT_BE_NULL);

        var existingStudentRating = studentRatingDetailRepository.findById(studentRatingDetail.getStudentRatingDetailId()).orElseThrow();
        var existingAttendances = existingStudentRating.getAttendances();

        var newAttendance = studentRatingDetail.getAttendances().stream().findFirst().orElseThrow();

        studentRatingDetail.setAttendances(existingAttendances.stream().map(attendance -> {
            if (attendance.getDay().equals(newAttendance.getDay())) {
                return newAttendance;
            }

            return attendance;
        }).collect(Collectors.toList()));

        studentRatingDetail.setAcademyName(existingStudentRating.getAcademyName());
        studentRatingDetail.setFullName(existingStudentRating.getFullName());
        studentRatingDetail.setSlot(existingStudentRating.getSlot());
        studentRatingDetail.setSlotDays(existingStudentRating.getSlotDays());

        var studentRating = studentRatingDetailRepository.saveAndFlush(studentRatingDetail);

        return defaultResponse(studentRating, STUDENT_ATTENDANCE_DETAILS_ARE_UPDATED);
    }

    public SuccessResponse<?> getUserById(Long userId) {
        try {
            var user = userRepository.getReferenceById(userId);
            return new SuccessResponse<>(UserDto.from(user), HttpStatus.OK.value(), false, GET_BY_USER_ID);
        } catch (Exception exception) {
            return errorResponse(HttpStatus.NOT_FOUND.value(), USER_NOT_FOUND);
        }
    }

    @Override
    public SuccessResponse<UserDto> getMentorDetailById(Long mentorId) {
        var currentUser = UserContextHolder.getCurrentUser();
        var userId = currentUser.userId();
        var mentor = userRepository.findById(mentorId).orElseThrow();
        var roles = currentUser.roles().stream().findFirst().orElseThrow();

        if (roles.getRoleType().equals(ACADEMY)) {
            var academyMapping = studentMentorAcademy.getEntityByMentorAndAcademyId(mentorId, userId);
            if (academyMapping.isPresent()) {
                mentor.setSubscriptionInfo(SubscriptionInfo.from(academyMapping.get()));
                mentor.setMapped(true);
            }
        } else if (roles.getRoleType().equals(RoleType.USER)) {
            var studentMentor = studentMentorMapping.getMentorDetailByMentorIdAndStudentId(mentorId, userId);
            if (studentMentor.isPresent()) {
                mentor.setSubscriptionInfo(SubscriptionInfo.from(studentMentor.get()));
                mentor.setMapped(true);
            }
        }

        return defaultResponse(UserDto.from(mentor), GET_MENTOR_DETAIL_BY_MENTOR_ID_WITH_MAPPED_FLAG);
    }

    @Override
    public SuccessResponse<Map<String, ?>> getAcademyDetailById(Long academyId) {
        var currentUser = UserContextHolder.getCurrentUser();
        var studentId = currentUser.userId();

        var academy = studentMentorAcademy.getStudentOrMentorWithAcademyMapping(studentId, academyId);
        var mentors = mentorAcademy.getAllMentorDetailsByAcademyId(academyId, studentId);

        return defaultResponse( Map.of(SpectraConstant.ACADEMY, academy, SpectraConstant.MENTOR, mentors),
                GET_ACADEMY_DETAIL_BY_ACADEMY_ID_WITH_MAPPED_FLAG);
    }

    @Override
    public SuccessResponse<List<UserDto>> getNearByMentors() {
        var user = UserContextHolder.getCurrentUser();
        var latitude = user.latitude();
        var longitude = user.longitude();

        var nearByUsers = userDao.getAllUsers(latitude, longitude, user.userId());
        var nearByList = nearByUsers.stream().map(UserDto::from).collect(Collectors.toList());
        if (isEmpty(nearByUsers)) {
            nearByList = getAllUsersByRole(MENTOR.name(), 1, 5)
                    .stream()
                    .filter(userDto -> userDto.userId() != user.userId())
                    .collect(Collectors.toList());
        }

        return defaultResponse(nearByList, GET_ALL_NEARBY_MENTORS);
    }

    @Override
    public SuccessResponse<Map<String, ?>> getAllStudentByMentorId() {
        var mentorId = UserContextHolder.getCurrentUser().userId();

        var mentors = studentMentorMapping.getAllStudentDetailsByMentorId(mentorId);
        var academies = studentMentorAcademy.getAllAcademyStudentDetailsByMentorId(mentorId);

        return defaultResponse(Map.of(MENTOR, mentors, ACADEMY, academies), GET_ALL_STUDENTS_BY_MENTOR_ID);
    }

    @Override
    public SuccessResponse<List<UserDto>> getAllAcademyWithMappedKey(Integer page, Integer limit) {
        var currentUser = UserContextHolder.getCurrentUser();
        var academies = userRepository.getAllUsersByRole(ACADEMY, PageRequest.of(page - 1, limit));
        var academyByStudentId = studentMentorAcademy.getAllAcademyByStudentId(currentUser.userId());

        var academiesDto = academies
                .stream()
                .map(user -> UserDto.from(user, academyByStudentId.contains(user.getUserId())))
                .collect(Collectors.toList());

        return defaultResponse(academiesDto, GET_ALL_ACADEMY_WITH_MAPPED_KEY);
    }

    public Map<String, ?> signInUser(Map<String, String> credentials) throws JsonProcessingException {
        var username = credentials.get(USERNAME);
        var password = credentials.get(PASSWORD);
        var user = this.userRepository.getUserByUserName(username);

        if (isNull(user)) {
            return getResponse(HttpStatus.UNAUTHORIZED.value(), INVALID_USER);
        }
        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            return getResponse(HttpStatus.UNAUTHORIZED.value(), INVALID_USERNAME_OR_PASSWORD);
        }
        if (!user.getIsVerified()) {
            return getResponse(HttpStatus.NOT_ACCEPTABLE.value(), USER_NOT_VERIFIED_YET);
        }

        var from = UserDto.from(user);
        return Map.of(
                BODY, from,
                STATUS, HttpStatus.OK.value(),
                ERROR, false,
                MESSAGE, SIGN_IN_SUCCEED,
                ACCESS_TOKEN, jwtHelper.createToken(from)
        );
    }

    public List<UserDto> getAllUsersByRole(String role, Integer page, Integer limit) {
        Assert.notNull(role, ROLE_CANNOT_BE_NULL);
        var roleType = RoleType.valueOf(role.toUpperCase());
        var pageable = PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, USER_ID));
        var allUsersByRole = userRepository.getAllUsersByRole(roleType, pageable);

        return allUsersByRole.stream().map(UserDto::from).collect(Collectors.toList());
    }

    @Transactional
    public String verifyUser(String token) {
        UserDto userDto;
        try {
            userDto = jwtHelper.parseToken(token);
        } catch (Exception exception) {
            log.error(INVALID_USER, exception);
            return INVALID_USER;
        }

        userRepository.updateUserVerified(userDto.userId());
        String verifiedMessage = VERIFIED_MESSAGE.formatted(userDto.firstName());

        return verifiedMessage;
    }

    public SuccessResponse<String> sendEmailOtp(String email) {
        var user = userRepository.getUserByUserName(email);
        if (user == null) {
            return errorResponse(HttpStatus.NOT_ACCEPTABLE.value(), INVALID_USERNAME_OR_EMAIL);
        }

        UserDto from = UserDto.from(user);
        sendSignUpEmailOrOtpEmail((userDto) -> emailService.sendForgotPasswordVerificationEmail(userDto), from);

        return defaultResponse(Map.of(), OTP_SENT_TO_THE_USER);
    }

    public SuccessResponse<String> validateOtp(Map<String, String> userDetails) {
        var email = userDetails.get(EMAIL);
        var otp = userDetails.get(OTP);
        var user = userRepository.getUserByUserName(email);

        var defaultResponse = defaultResponse(Map.of(), VALID_OTP);
        return ofNullable(validateAndGetUser(otp, user)).orElse(defaultResponse);
    }

    public SuccessResponse<String> resendVerificationEmail(String email) {
        var user = userRepository.getUserByUserName(email);
        var from = UserDto.from(user);
        emailService.sendSignUpVerificationEmail(from);

        return defaultResponse(Map.of(), VERIFICATION_EMAIL_SENT);
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
                    return defaultResponse(Map.of(), PASSWORD_UPDATED);
                });
    }

    /**
     * 1. User mapped with mentor with academy
     * 2. User mapped with mentor without academy
     * 3. Mentor mapped with academy
     *
     * @param userDetails
     * @return
     */
    @Override
    public SuccessResponse<String> updateUserMapping(Map<String, String> userDetails) {
        var request = createUserMappingRequest(userDetails);
        var mapping = mappingFactory.getMapping(request);
        mapping.updateMappingDetails(request);

        return defaultResponse(Map.of(), USER_MAPPING_ADDED);
    }


    private UserMappingRequest createUserMappingRequest(Map<String, String> userDetails) {
        var studentId = toLong(userDetails.get(STUDENT_ID));
        var mentorId = toLong(userDetails.get(MENTOR_ID));
        var academyId = toLong(userDetails.get(ACADEMY_ID));
        var totalMonths = toLong(userDetails.get(MONTHS));
        var plan = userDetails.get(PLAN);
        var mentorType = userDetails.get(MENTOR_TYPE);
        var academyType = userDetails.get(ACADEMY_TYPE);
        var academyName = userDetails.get(ACADEMY_NAME);
        var slot = userDetails.get(SLOT);
        var slotDays = userDetails.get(SLOT_DAYS);
        var amount = toDouble(userDetails.get(AMOUNT));

        return new UserMappingRequest(
            studentId, mentorId, academyId, totalMonths, amount,
            plan, mentorType, academyType, slot, slotDays, academyName
        );
    }

    @Override
    public SuccessResponse<List<UserDto>> getMentorsByUserOrAcademy(Integer page, Integer limit) {
        var currentUser = UserContextHolder.getCurrentUser();
        var currentRole = currentUser.roles().stream().findFirst().orElseThrow();
        var userId = currentUser.userId();
        var hasAcademy = ACADEMY.equals(currentRole.getRoleType());

        return getResponseByMentorType(page, limit, userId, MENTOR, hasAcademy);
    }

    @Override
    public SuccessResponse<List<UserDto>> getCoachesByUserUserOrAcademy(Integer page, Integer limit) {
        var currentUser = UserContextHolder.getCurrentUser();
        var currentRole = currentUser.roles().stream().findFirst().orElseThrow();
        var userId = currentUser.userId();
        var hasAcademy = ACADEMY.equals(currentRole.getRoleType());

        return getResponseByMentorType(page, limit, userId, COACH, hasAcademy);
    }

    @Override
    public SuccessResponse<List<StudentRatingDetail>> getAllStudentAttendanceByMentorId(Long mentorId) {
        var studentAttendanceDetailsByMentorId = studentRatingDetailRepository.getAllStudentAttendanceDetailsByMentorId(mentorId);

        return defaultResponse(studentAttendanceDetailsByMentorId, GET_ALL_STUDENTS_ATTENDANCE_DETAIL_BY_MENTOR_ID);
    }

    @Override
    public SuccessResponse<List<StudentRatingDetail>> getStudentAttendanceByStudentIdAndMentorOrAcademyId(StudentAttendanceRequest request) {
        List<StudentRatingDetail> details;
        if (notZero(request.academyId())) {
            details = studentRatingDetailRepository
                .getAttendanceDetailsByStudentAndMentorAndAcademyId(request.studentId(), request.mentorId(), request.academyId());
        } else {
            details = studentRatingDetailRepository.getAttendanceDetailsByStudentAndMentorId(request.studentId(), request.mentorId());
        }

        var studentRating = details.stream().findFirst().orElse(null);

        return defaultResponse(studentRating, GET_ALL_STUDENTS_ATTENDANCE_DETAIL);
    }

    @Override
    public SuccessResponse<StudentRatingDetail> getStudentAttendanceDetailById(Long studentAttendanceId) {
        var studentRatingDetail = studentRatingDetailRepository.getReferenceById(studentAttendanceId);

        return defaultResponse(studentRatingDetail, GET_STUDENT_ATTENDANCE_DETAIL_BY_ID);
    }

    @Override
    public SuccessResponse<List<UserDto>> getAllMentorsByAcademy(Integer page, Integer limit) {
        var currentUserId = UserContextHolder.getCurrentUser().userId();
        var pageRequest = PageRequest.of(page - 1, limit);
        var allMentorsByAcademy = userRepository.getAllMentorsByAcademy(currentUserId, pageRequest);
        var mapToUserDto = allMentorsByAcademy.stream().map(user -> UserDto.from(user, true));

        return defaultResponse(mapToUserDto, GET_ALL_MENTORS_BY_ACADEMY_ID);
    }

    @Override
    public SuccessResponse<List<UserDto>> getAllMentorsAndAcademyByStudent() {
        var userId = UserContextHolder.getCurrentUser().userId();

        var mentors = studentMentorMapping.getAllMentorDetailsByStudentId(userId);
        var academies = studentMentorAcademy.getAllAcademyDetailsByStudent(userId);

        var result = Map.of(
                SpectraConstant.ACADEMY, academies,
                SpectraConstant.MENTOR, mentors
        );

        return defaultResponse(result, GET_ALL_ACADEMY_AND_MENTORS_BY_STUDENT);
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

        return defaultResponse(userDtoStream, GET_ALL_USERS_BY_SEARCH);
    }

    private SuccessResponse getResponseByMentorType(Integer page, Integer limit, Long userId, RoleType mentorType, boolean hasAcademy) {
        var pageable = PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, USER_ID));
        var mentors = userRepository.getAllUsersByRole(mentorType, pageable);
        var mentorIdsByAcademyOrStudent = hasAcademy ? mentorAcademy.getAllMentorIdsByAcademy(userId, mentorType.name()) :
                studentMentorMapping.getAllMentorIdsByStudent(userId, mentorType.name());

        var mentorsDto = mentors.stream()
                .map(user -> UserDto.from(user, mentorIdsByAcademyOrStudent.contains(user.getUserId())))
                .collect(Collectors.toList());

        return defaultResponse(mentorsDto, GET_ALL_MENTORS);
    }

    private Map<String, ? extends Object> getResponse(int status, String message) {
        return Map.of(BODY, Map.of(), STATUS, status, ERROR, true, MESSAGE, message);
    }

    private SuccessResponse validateAndGetUser(String otp, User user) {
        if (isNull(user)) {
            return errorResponse(HttpStatus.NOT_ACCEPTABLE.value(), INVALID_USERNAME_OR_EMAIL);
        }
        if (!Objects.equals(user.getOtp(), otp)) {
            return errorResponse(HttpStatus.NOT_ACCEPTABLE.value(), INVALID_OTP);
        }

        return null;
    }

    private void sendSignUpEmailOrOtpEmail(Consumer<UserDto> consumer, UserDto from) {
        Runnable sendEmail = () -> consumer.accept(from);
        new Thread(sendEmail).start();
    }
}
