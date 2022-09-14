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

    public static Double toDouble(String number) {
        if (isNull(number)) {
            return 0.0d;
        }

        return Double.valueOf(number);
    }

    public static boolean notZero(Long... values) {
        if (values == null || values.length == 0) {
            return false;
        }

        for (int i = 0; i < values.length; i++) {
            if (isNull(values[i]) || values[i] == 0l) {
                return false;
            }
        }

        return true;
    }
}
