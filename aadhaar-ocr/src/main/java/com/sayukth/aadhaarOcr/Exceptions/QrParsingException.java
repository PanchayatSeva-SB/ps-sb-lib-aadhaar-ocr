package com.sayukth.aadhaarOcr.Exceptions;

public class QrParsingException extends UtilsException {

    public QrParsingException(String message) {
        super(message);
    }

    public QrParsingException(String message, Throwable cause) {
        super(message, cause);
    }

    public QrParsingException(Throwable e) {
        super(e);
    }
}
