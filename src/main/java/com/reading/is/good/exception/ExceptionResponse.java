package com.reading.is.good.exception;

import org.springframework.http.HttpStatus;

public enum ExceptionResponse {
    GENERAL("err-0000", HttpStatus.INTERNAL_SERVER_ERROR, "Unknown exception occurred. "),
    UNAUTHORIZED("err-1000", HttpStatus.UNAUTHORIZED, "The request is unauthorized."),
    BAD_REQUEST("err-1001", HttpStatus.BAD_REQUEST, "%s"),
    NOT_FOUND("err-1002", HttpStatus.NOT_FOUND, "%s");

    private String code;
    private HttpStatus status;
    private String message;

    ExceptionResponse(String code, HttpStatus status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
