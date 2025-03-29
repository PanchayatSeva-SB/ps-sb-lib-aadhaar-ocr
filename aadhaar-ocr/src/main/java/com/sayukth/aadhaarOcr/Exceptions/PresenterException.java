package com.sayukth.aadhaarOcr.Exceptions;

public class PresenterException extends ActivityException {
    public PresenterException(String message) {
        super(message);
    }

    public PresenterException(String message, Throwable cause) {
        super(message, cause);
    }

    public PresenterException(Throwable e) {
        super(e);
    }
}

