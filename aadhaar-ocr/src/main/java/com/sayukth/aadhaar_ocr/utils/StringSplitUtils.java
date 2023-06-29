package com.sayukth.aadhaar_ocr.utils;

import com.sayukth.aadhaar_ocr.error.ActivityException;

public class StringSplitUtils {

    public static String getFirstPartOfStringBySplitString(String string, String delimiter) throws ActivityException {
        try {
            String mystring = string;
            String firstWord = " ";
            if (mystring.contains(delimiter)) {
                String[] arr = mystring.split(delimiter, 2);
                firstWord = arr[0];
            }
            return firstWord;

        } catch (Exception e) {
            throw new ActivityException(e);
        }
    }

    public static String getLastPartOfStringBySplitString(String string, String delimiter) throws ActivityException {
        try {
            String mystring = string;
            String theRest = "";
            if (mystring.contains(delimiter)) {
                String[] arr = mystring.split(delimiter, 2);

                theRest = arr[1];
            }
            return theRest;

        } catch (Exception e) {
            throw new ActivityException(e);
        }
    }
}
