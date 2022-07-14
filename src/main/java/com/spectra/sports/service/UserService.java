package com.spectra.sports.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.spectra.sports.dto.UserDto;
import com.spectra.sports.entity.User;
import com.spectra.sports.response.SuccessResponse;
import java.util.List;
import java.util.Map;

public interface UserService {
    SuccessResponse<?> addUser(User user);

    SuccessResponse<?> updateUser(User user);

    SuccessResponse<?> getUserById(Long userId);

    SuccessResponse<UserDto> getMentorDetailById(Long mentorId);

    Map<String, ?> getAcademyDetailById(Long academyId);

    SuccessResponse<List<UserDto>> getNearByMentors();

    Map<String, ? extends Object> signInUser(Map<String, String> credentials) throws JsonProcessingException;

    List<UserDto> getAllUsersByRole(String role, Integer page, Integer limit);

    String verifyUser(String token);

    SuccessResponse<String> sendEmailOtp(String email);

    SuccessResponse<String> validateOtp(Map<String, String> userDetails);

    SuccessResponse<String> resendVerificationEmail(String email);

    SuccessResponse<String> resetPassword(Map<String, String> userDetails);

    SuccessResponse<String> updateUserMapping(Map<String, String> userDetails);

    SuccessResponse<List<UserDto>> getMentorsByUser();

    SuccessResponse<List<UserDto>> getAllMentorsByAcademy();

    SuccessResponse<List<UserDto>> getAllMentorsOrAcademyByRole(String roleType);
}
