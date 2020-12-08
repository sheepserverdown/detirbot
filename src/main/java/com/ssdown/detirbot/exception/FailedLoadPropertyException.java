package com.ssdown.detirbot.exception;

public class FailedLoadPropertyException extends RuntimeException {
    public FailedLoadPropertyException(String message, Exception e) {
        super(message, e);
    }
}
