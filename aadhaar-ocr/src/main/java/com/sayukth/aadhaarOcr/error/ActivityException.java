package com.sayukth.aadhaarOcr.error;

public class ActivityException extends Exception {

    public ActivityException(Exception e) {
        super(e);
    }


    public ActivityException(String message) {
        super(message);
    }

}




