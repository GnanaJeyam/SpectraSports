package com.spectra.sports.helper;

import com.spectra.sports.dto.UserDto;

public class UserContextHolder {
    private static final ThreadLocal<UserDto> currentUser = new ThreadLocal();

    public UserContextHolder() {
    }

    public static void setCurrentUser(UserDto userDto) {
        currentUser.set(userDto);
    }

    public static UserDto getCurrentUser() {
        return currentUser.get();
    }
}
