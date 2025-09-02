package com.allra.allra.common;

public class SimpleError {
    private final String code;
    private final String message;

    public SimpleError(String code, String message) {
        this.code = code;
        this.message = message;
    }
    public String getCode() { return code; }
    public String getMessage() { return message; }
}
