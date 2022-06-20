//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.spectra.sports.helper;

import com.spectra.sports.dto.UserDto;

public class UserContextHolder {
    private static final ThreadLocal<UserDto> currentUser = new ThreadLocal();

    public UserContextHolder() {
    }

    public static void setCurrentUser(UserDto userDto) {
        currentUser.set(userDto);
    }

    public static UserDto getUser() {
        return (UserDto)currentUser.get();
    }
}
