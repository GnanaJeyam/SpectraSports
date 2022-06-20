package com.spectra.sports.service;

import com.spectra.sports.dto.UserDto;

public interface EmailService {
    void sendSignUpVerificationEmail(UserDto user);

    void sendForgotPasswordVerificationEmail(UserDto user);
}
