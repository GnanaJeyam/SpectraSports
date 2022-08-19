package com.spectra.sports.util;

import static java.util.Objects.isNull;

public final class NumberUtil {

    private NumberUtil() {
        // Do not allow anyone to create object instance
    }

    public static Long toLong(String number) {
        if (isNull(number)) {
            return 0l;
        }

        return Long.valueOf(number);
    }
}
