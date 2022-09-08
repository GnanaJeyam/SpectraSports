package com.spectra.sports.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.spectra.sports.dto.UserDto;
import com.spectra.sports.entity.StudentRatingDetail;
import com.spectra.sports.entity.User;
import com.spectra.sports.response.SuccessResponse;

import java.util.List;
import java.util.Map;

public interface UserService {
    SuccessResponse<?> addUser(User user);

    SuccessResponse<?> updateUser(User user);

    SuccessResponse<?> updateStudentAttendance(StudentRatingDetail studentRatingDetail);

    SuccessResponse<?> getUserById(Long userId);

    SuccessResponse<UserDto> getMentorDetailById(Long mentorId);

    SuccessResponse<Map<String, ?>> getAcademyDetailById(Long academyId);

    SuccessResponse<List<UserDto>> getNearByMentors();

    SuccessResponse<Map<String, ?>> getAllStudentByMentorId();

    SuccessResponse<List<UserDto>> getAllAcademyWithMappedKey(Integer page, Integer limit);

    Map<String, ?> signInUser(Map<String, String> credentials) throws JsonProcessingException;

    List<UserDto> getAllUsersByRole(String role, Integer page, Integer limit);

    String verifyUser(String token);

    SuccessResponse<String> sendEmailOtp(String email);

    SuccessResponse<String> validateOtp(Map<String, String> userDetails);

    SuccessResponse<String> resendVerificationEmail(String email);

    SuccessResponse<String> resetPassword(Map<String, String> userDetails);

    SuccessResponse<String> updateUserMapping(Map<String, String> userDetails);

    SuccessResponse<List<UserDto>> getMentorsByUser(Integer page, Integer limit);

    SuccessResponse<List<StudentRatingDetail>> getAllStudentAttendanceByMentorId(Long mentorId);

    SuccessResponse<StudentRatingDetail> getStudentAttendanceDetailById(Long studentAttendanceId);

    SuccessResponse<List<UserDto>> getAllMentorsByAcademy(Integer page, Integer limit);

    SuccessResponse<List<UserDto>> getAllMentorsAndAcademyByStudent();

    SuccessResponse<List<UserDto>> getAllUsersByNameOrSpecialistIn(String search);
}
