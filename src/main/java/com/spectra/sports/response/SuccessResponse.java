//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.spectra.sports.response;

import java.util.Map;
import org.springframework.http.HttpStatus;

public record SuccessResponse<T>(T body, int status, boolean error, String message) {
    public SuccessResponse(T body, int status, boolean error, String message) {
        this.body = body;
        this.status = status;
        this.error = error;
        this.message = message;
    }

    public static <T> SuccessResponse defaultResponse(T body, String message) {
        return new SuccessResponse(body, HttpStatus.OK.value(), false, message);
    }

    public static SuccessResponse errorResponse(int status, String message) {
        return new SuccessResponse(Map.of(), status, true, message);
    }

    public T body() {
        return this.body;
    }

    public int status() {
        return this.status;
    }

    public boolean error() {
        return this.error;
    }

    public String message() {
        return this.message;
    }
}
