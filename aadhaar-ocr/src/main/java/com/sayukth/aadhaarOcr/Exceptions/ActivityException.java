package com.sayukth.aadhaarOcr.Exceptions;

public class ActivityException extends Exception {
    public ActivityException(String message) {
        super(message);
    }

    public ActivityException(String message, Throwable cause) {
        super(message, cause);
    }

    public ActivityException(Throwable e) {
        super(e);
    }
}

