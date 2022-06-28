package com.spectra.sports.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.spectra.sports.dto.UserDto;
import com.spectra.sports.entity.User;
import com.spectra.sports.response.SuccessResponse;
import com.spectra.sports.service.S3Service;
import com.spectra.sports.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
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

    @PostMapping({"/verification-email"})
    SuccessResponse<String> sendVerificationEmail(@RequestParam("email") String email) {
        var sentOtp = userService.resendVerificationEmail(email);

        return sentOtp;
    }

    @GetMapping({"/all/{role}"})
    ResponseEntity<List<UserDto>> getAllUsersByRole(@PathVariable("role") String role,
                                                    @RequestParam("page") Integer page,
                                                    @RequestParam(value = "limit",defaultValue = "10") Integer limit) {
        List<UserDto> allUsersByRole = userService.getAllUsersByRole(role, page, limit);

        return ResponseEntity.ok(allUsersByRole);
    }

    @GetMapping({"/download"})
    @Deprecated
    ResponseEntity<Resource> download(@RequestParam("name") String test) {
        String fileName = "attachment; filename=\"%s\"".formatted(new Object[]{test});
        InputStream inputStream = s3Service.retrieveFileFromS3(test);
        return ((BodyBuilder)ResponseEntity.ok().header("Content-Disposition", new String[]{fileName})).contentType(MediaType.APPLICATION_OCTET_STREAM).body(new InputStreamResource(inputStream));
    }

    @PostMapping({"/signup"})
    SuccessResponse<?> addUser(@RequestBody User user) {
        var newUser = userService.addUser(user);

        return newUser;
    }

    @PutMapping({"/update"})
    SuccessResponse<?> updateUser(@RequestBody User user) {
        var updatedUser = userService.updateUser(user);

        return updatedUser;
    }

    @PostMapping({"/sign-in"})
    Map<String, ?> signInUser(@RequestBody Map<String, String> userCredentials) throws JsonProcessingException {
        var successResponse = userService.signInUser(userCredentials);

        return successResponse;
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
}
