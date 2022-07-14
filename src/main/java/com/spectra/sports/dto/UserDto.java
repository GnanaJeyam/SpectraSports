package com.spectra.sports.dto;

import com.spectra.sports.entity.Role;
import com.spectra.sports.entity.Slot;
import com.spectra.sports.entity.User;

import java.util.List;
import java.util.Set;

public record UserDto(Long userId, String firstName, String lastName, String email, String mobileNumber, Set<Role> roles, String location,
                      Double latitude, Double longitude, Boolean isVerified, String experience, String description, String[] specialistIn,
                      List<Slot> availableSlots, String imageName, String otp, Boolean isMapped) {

    public static UserDto from(User user) {
        return from(user, user.isMapped());
    }

    public static UserDto from(User user, Boolean flag) {
        UserDto userDto = new UserDto(
            user.getUserId(), user.getFirstName(), user.getLastName(), user.getEmail(),
            user.getMobileNumber(), user.getRoles(), user.getLocation(), user.getLatitude(),
            user.getLongitude(), user.getIsVerified(), user.getExperience(), user.getDescription(),
            user.getSpecialistIn(), user.getAvailableSlots(), user.getImageName(), user.getOtp(), flag
        );

        return userDto;
    }
}
