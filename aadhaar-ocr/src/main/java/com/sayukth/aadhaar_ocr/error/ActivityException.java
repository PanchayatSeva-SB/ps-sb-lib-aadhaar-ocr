package com.sayukth.aadhaar_ocr.error;

public class ActivityException extends Exception {

    public ActivityException(Exception e) {
        super(e);
    }


    public ActivityException(String message) {
        super(message);
    }

}




