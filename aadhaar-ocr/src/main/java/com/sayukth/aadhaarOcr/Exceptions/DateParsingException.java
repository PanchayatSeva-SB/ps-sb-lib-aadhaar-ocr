package com.sayukth.aadhaarOcr.Exceptions;

public class DateParsingException extends UtilsException {

    public DateParsingException(String message) {
        super(message);
    }

    public DateParsingException(String message, Throwable cause) {
        super(message, cause);
    }

    public DateParsingException(Throwable e) {
        super(e);
    }
}
