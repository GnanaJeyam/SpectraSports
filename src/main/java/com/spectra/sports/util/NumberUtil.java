package com.spectra.sports.util;

public final class NumberUtil {

    private NumberUtil() {
        // Do not allow anyone to create object instance
    }

    public static Long toLong(String number) {
        if (number == null) {
            var zero = 0l;
            return zero;
        }

        return Long.valueOf(number);
    }
}
