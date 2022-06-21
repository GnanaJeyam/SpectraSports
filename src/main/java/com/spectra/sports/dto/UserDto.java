//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.spectra.sports.dto;

import com.spectra.sports.entity.Role;
import com.spectra.sports.entity.Slot;
import com.spectra.sports.entity.User;
import java.util.List;
import java.util.Set;

public record UserDto(Long userId, String firstName, String lastName, String email, String mobileNumber, Set<Role> roles, String location, String latitude, String longitude, Boolean isVerified, String experience, String description, String[] specialistIn, List<Slot> availableSlots, String imageName, String otp) {
    public UserDto(Long userId, String firstName, String lastName, String email, String mobileNumber, Set<Role> roles, String location, String latitude, String longitude, Boolean isVerified, String experience, String description, String[] specialistIn, List<Slot> availableSlots, String imageName, String otp) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.roles = roles;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isVerified = isVerified;
        this.experience = experience;
        this.description = description;
        this.specialistIn = specialistIn;
        this.availableSlots = availableSlots;
        this.imageName = imageName;
        this.otp = otp;
    }

    public static UserDto from(User user) {
        UserDto userDto = new UserDto(
            user.getUserId(), user.getFirstName(), user.getLastName(), user.getEmail(),
            user.getMobileNumber(), user.getRoles(), user.getLocation(), user.getLatitude(),
            user.getLongitude(), user.getVerified(), user.getExperience(), user.getDescription(),
            user.getSpecialistIn(), user.getAvailableSlots(), user.getImageName(), user.getOtp()
        );

        return userDto;
    }
}
