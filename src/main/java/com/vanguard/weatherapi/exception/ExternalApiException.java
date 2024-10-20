package com.vanguard.weatherapi.exception;

public class ExternalApiException extends RuntimeException {

    public ExternalApiException(String errorMessage) {
        super(errorMessage);
    }
}