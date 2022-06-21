//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.spectra.sports.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.spectra.sports.dto.UserDto;
import com.spectra.sports.entity.Role;
import com.spectra.sports.entity.RoleType;
import com.spectra.sports.entity.User;
import com.spectra.sports.helper.JwtHelper;
import com.spectra.sports.mapper.UserMapper;
import com.spectra.sports.repository.RoleRepository;
import com.spectra.sports.repository.UserRepository;
import com.spectra.sports.response.SuccessResponse;
import com.spectra.sports.service.EmailService;
import com.spectra.sports.service.UserService;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private JwtHelper jwtHelper;
    private EmailService emailService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           BCryptPasswordEncoder bCryptPasswordEncoder,
                           JwtHelper jwtHelper,
                           EmailService emailService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtHelper = jwtHelper;
        this.emailService = emailService;
    }

    public SuccessResponse<?> addUser(User user) {
        Assert.notNull(user, "User Cannot be null");
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setVerified(false);

        try {
            var createdUser =  userRepository.save(user);
            var roleIds = (Set)createdUser.getRoles().stream().map(Role::getRoleId).collect(Collectors.toSet());
            user.setRoles(this.roleRepository.getRolesByIds(roleIds));
            var from = UserDto.from(createdUser);
            this.sendSignUpEmailOrOtpEmail((userDto) -> emailService.sendSignUpVerificationEmail(userDto), from);

            return new SuccessResponse(from, HttpStatus.OK.value(), false, "Sign up SuccessFul");
        } catch (Exception var5) {
            String message = "Duplicate User, Please try with different email or Number";
            return new SuccessResponse(Map.of(), HttpStatus.NOT_ACCEPTABLE.value(), true, message);
        }
    }

    public SuccessResponse<?> updateUser(User user) {
        Assert.notNull(user, "User Cannot be null");
        var existingUser = userRepository.getReferenceById(user.getUserId());
        user = UserMapper.mapUser(existingUser, user);
        var updatedUser = userRepository.saveAndFlush(user);

        return SuccessResponse.defaultResponse(updatedUser, "User Updated SuccessFully");
    }

    public SuccessResponse<?> getUserById(Long userId) {
        try {
            var user = userRepository.getReferenceById(userId);
            return new SuccessResponse(UserDto.from(user), HttpStatus.OK.value(), false, "Get by User Id");
        } catch (Exception var3) {
            return SuccessResponse.errorResponse(HttpStatus.NOT_FOUND.value(), "User Not Found");
        }
    }

    public Map<String, ? extends Object> signInUser(Map<String, String> credentials) throws JsonProcessingException {
        var username = credentials.get("username");
        var password = credentials.get("password");
        var user = this.userRepository.getUserByUserName(username);

        if (user == null) {
            return getResponse(HttpStatus.UNAUTHORIZED.value(), "Invalid User");
        }
        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            return getResponse(HttpStatus.UNAUTHORIZED.value(), "Invalid username or password");
        }
        if (!user.getVerified()) {
            return getResponse(HttpStatus.NOT_ACCEPTABLE.value(), "User not verified yet.");
        }

        var from = UserDto.from(user);
        return Map.of(
            "body", from,
            "status", HttpStatus.OK.value(),
            "error", false,
            "message", "Sign in Succeed",
            "accessToken", jwtHelper.createToken(from)
        );
    }

    public List<UserDto> getAllUsersByRole(String role, Integer page, Integer limit) {
        Assert.notNull(role, "Role cannot be null");
        var roleType = RoleType.valueOf(role.toUpperCase());
        var pageable = PageRequest.of(page - 1, limit);
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
            <b> %s has been verified </b>
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
        var email = userDetails.get("email");
        var otp = userDetails.get("otp");
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
        var email = userDetails.get("email");
        var newPassword = userDetails.get("password");
        var otp = userDetails.get("otp");
        var user = userRepository.getUserByUserName(email);

        return Optional.ofNullable(validateAndGetUser(otp, user))
            .orElseGet(() -> {
                user.setPassword(bCryptPasswordEncoder.encode(newPassword));
                userRepository.saveAndFlush(user);
                return SuccessResponse.defaultResponse(Map.of(), "Password Updated");
        });
    }

    private Map<String, ? extends Object> getResponse(int status, String message) {
        return Map.of("body", Map.of(), "status", status, "error", true, "message", message);
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
