package com.spectra.sports.mapper;

import com.spectra.sports.entity.User;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.util.CollectionUtils;

import static java.util.Optional.ofNullable;

public final class UserMapper {

    public static User mapUser(User source, User dest) {
        dest.setPassword(ofNullable(dest.getPassword()).orElse(source.getPassword()));
        dest.setIsVerified(source.getIsVerified());
        dest.setEmail(ofNullable(dest.getEmail()).orElse(source.getEmail()));
        dest.setDescription(ofNullable(dest.getDescription()).orElse(source.getDescription()));
        dest.setExperience(ofNullable(dest.getExperience()).orElse(source.getExperience()));
        dest.setFirstName(ofNullable(dest.getFirstName()).orElse(source.getFirstName()));
        dest.setLastName(ofNullable(dest.getLastName()).orElse(source.getLastName()));
        dest.setImageName(ofNullable(dest.getImageName()).orElse(source.getImageName()));
        dest.setLatitude(ofNullable(dest.getLatitude()).orElse(source.getLatitude()));
        dest.setLongitude(ofNullable(dest.getLongitude()).orElse(source.getLongitude()));
        dest.setLocation(ofNullable(dest.getLocation()).orElse(source.getLocation()));
        dest.setMobileNumber(ofNullable(dest.getMobileNumber()).orElse(source.getMobileNumber()));
        dest.setOtp(ofNullable(dest.getOtp()).orElse(source.getOtp()));
        dest.setSpecialistIn(ArrayUtils.isEmpty(dest.getSpecialistIn()) ? source.getSpecialistIn() : dest.getSpecialistIn());
        dest.setUserId(ofNullable(dest.getUserId()).orElse(source.getUserId()));
        dest.setCreatedBy(ofNullable(dest.getCreatedBy()).orElse(source.getCreatedBy()));
        dest.setRoles(CollectionUtils.isEmpty(dest.getRoles()) ? source.getRoles() : dest.getRoles());
        dest.setAvailableSlots(CollectionUtils.isEmpty(dest.getAvailableSlots()) ? source.getAvailableSlots(): dest.getAvailableSlots());
        dest.setCreatedAt(ofNullable(dest.getCreatedAt()).orElse(source.getCreatedAt()));
        dest.setUpdatedBy(dest.getUserId());

        return dest;
    }
}
