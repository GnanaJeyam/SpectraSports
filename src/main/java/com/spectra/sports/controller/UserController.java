package com.spectra.sports.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.spectra.sports.dto.UserDto;
import com.spectra.sports.entity.StudentRatingDetail;
import com.spectra.sports.entity.User;
import com.spectra.sports.response.SuccessResponse;
import com.spectra.sports.service.S3Service;
import com.spectra.sports.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    private UserService userService;
    private S3Service s3Service;

    @Autowired
    public UserController(UserService userService, S3Service s3Service) {
        this.userService = userService;
        this.s3Service = s3Service;
    }

    @GetMapping({"/{userId}"})
    SuccessResponse<?> getUserById(@PathVariable("userId") Long userId) {
        var userById = userService.getUserById(userId);

        return userById;
    }

    @GetMapping({"/verify/{token}"})
    ResponseEntity<String> verifyUser(@PathVariable("token") String token) {
        var verifiedMessage = userService.verifyUser(token);

        return ResponseEntity.ok(verifiedMessage);
    }

    @GetMapping({"/email-otp"})
    SuccessResponse<String> sendEmailOtp(@RequestParam("email") String email) {
        var sentOtp = userService.sendEmailOtp(email);
        return sentOtp;
    }

    @GetMapping({"/nearby-mentors"})
    SuccessResponse<List<UserDto>> getNearbyMentors() {
        var nearByMentors = userService.getNearByMentors();

        return nearByMentors;
    }

    @GetMapping({"/mentors"})
    SuccessResponse<List<UserDto>> getAllMentors(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                                 @RequestParam(value = "limit",defaultValue = "20") Integer limit) {
        var mentorsByRole = userService.getMentorsByUser(page, limit);

        return mentorsByRole;
    }

    @GetMapping({"/academy/mentors"})
    SuccessResponse<List<UserDto>> getAllMentorsByAcademy(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                                          @RequestParam(value = "limit",defaultValue = "30") Integer limit) {
        var mentorsByAcademy = userService.getAllMentorsByAcademy(page, limit);

        return mentorsByAcademy;
    }

    // Mentor has no academy list view
    // Mentor has to return only mapped academy
    // only for student
    @GetMapping({"/all/academy"})
    SuccessResponse<List<UserDto>> getAllAcademyWithMappedKey(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                                              @RequestParam(value = "limit",defaultValue = "20") Integer limit) {
        var mentorsByAcademy = userService.getAllAcademyWithMappedKey(page, limit);

        return mentorsByAcademy;
    }

    @GetMapping({"/roles/all"})
    SuccessResponse<List<UserDto>> getAllMentorsAndAcademyByStudent() {
        var mentorsAndAcademyByStudent = userService.getAllMentorsAndAcademyByStudent();

        return mentorsAndAcademyByStudent;
    }

    @GetMapping({"/mentor/students"})
    SuccessResponse<Map<String, ?>> getAllStudentByMentorId() {
        var mentorsAndAcademyByStudent = userService.getAllStudentByMentorId();

        return mentorsAndAcademyByStudent;
    }

    @GetMapping({"/search/{searchKey}"})
    SuccessResponse<List<UserDto>> retrieveAllUsersBySearchKey(@PathVariable("searchKey") String searchKey) {
        var mentorsOrAcademyByStudent = userService.getAllUsersByNameOrSpecialistIn(searchKey);

        return mentorsOrAcademyByStudent;
    }

    @GetMapping({"/detail/mentor/{mentorId}"})
    SuccessResponse<UserDto> getMentorDetailById(@PathVariable("mentorId") Long mentorId) {
        var mentorsOrAcademyByStudent = userService.getMentorDetailById(mentorId);

        return mentorsOrAcademyByStudent;
    }

    @GetMapping({"/student/attendance/{mentorId}"})
    SuccessResponse<List<StudentRatingDetail>> getAllStudentAttendanceByMentorId(@PathVariable("mentorId") Long mentorId) {
        var allStudentAttendanceByMentorId = userService.getAllStudentAttendanceByMentorId(mentorId);

        return allStudentAttendanceByMentorId;
    }

    @GetMapping({"/student/attendance/detail/{id}"})
    SuccessResponse<StudentRatingDetail> getAllStudentAttendanceById(@PathVariable("id") Long studentAttendanceId) {
        var studentRatingDetail = userService.getStudentAttendanceDetailById(studentAttendanceId);

        return studentRatingDetail;
    }

    @GetMapping({"/detail/academy/{academyId}"})
    SuccessResponse<Map<String, ?>> getAcademyDetailById(@PathVariable("academyId") Long academyId) {
        var mentorsOrAcademyByStudent = userService.getAcademyDetailById(academyId);

        return mentorsOrAcademyByStudent;
    }

    @GetMapping({"/all/{role}"})
    SuccessResponse<List<UserDto>> getAllUsersByRole(@PathVariable("role") String role,
                                                    @RequestParam("page") Integer page,
                                                    @RequestParam(value = "limit",defaultValue = "10") Integer limit) {
        var allUsersByRole = userService.getAllUsersByRole(role, page, limit);

        return SuccessResponse.defaultResponse(allUsersByRole, "Get Users By Role");
    }

    @PostMapping({"/signup"})
    SuccessResponse<?> addUser(@RequestBody User user) {
        var newUser = userService.addUser(user);

        return newUser;
    }

    @PostMapping("/sign-in")
    Map<String, ?> signInUser(@RequestBody Map<String, String> userCredentials) throws JsonProcessingException {
        var successResponse = userService.signInUser(userCredentials);

        return successResponse;
    }

    @PostMapping({"/verification-email"})
    SuccessResponse<String> sendVerificationEmail(@RequestParam("email") String email) {
        var sentOtp = userService.resendVerificationEmail(email);

        return sentOtp;
    }

    @PostMapping({"/validate-otp"})
    SuccessResponse<String> validateOtp(@RequestBody Map<String, String> userDetails) {
        var sentOtp = userService.validateOtp(userDetails);

        return sentOtp;
    }

    @PostMapping({"/reset-password"})
    SuccessResponse<String> resetPassword(@RequestBody Map<String, String> userDetails) {
        var resetPassword = userService.resetPassword(userDetails);

        return resetPassword;
    }

    @PostMapping({"/upload"})
    SuccessResponse<String> uploadImage(MultipartFile file) throws IOException {
        var uploadFileToS3 = s3Service.uploadFileToS3(file);

        return uploadFileToS3;
    }

    @PutMapping({"/update"})
    SuccessResponse<?> updateUser(@RequestBody User user) {
        var updatedUser = userService.updateUser(user);

        return updatedUser;
    }

    @PutMapping({"/update/student/attendance"})
    SuccessResponse<?> updateStudentAttendance(@RequestBody StudentRatingDetail studentRatingDetail) {
        var updatedStudentDetail = userService.updateStudentAttendance(studentRatingDetail);

        return updatedStudentDetail;
    }

    @PutMapping({"/update-mapping"})
    SuccessResponse<?> updateUser(@RequestBody Map<String, String> userDetails) {

        return userService.updateUserMapping(userDetails);
    }
}
