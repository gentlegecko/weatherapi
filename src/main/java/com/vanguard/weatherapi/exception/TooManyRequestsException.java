package com.vanguard.weatherapi.exception;

public class TooManyRequestsException extends RuntimeException {

    public TooManyRequestsException(String errorMessage) {
        super(errorMessage);
    }

}