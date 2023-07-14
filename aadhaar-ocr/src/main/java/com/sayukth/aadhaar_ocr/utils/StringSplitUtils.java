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

    public static String removeSymbolFromString(String originalString, char charToRemove) {
        StringBuilder stringBuilder = new StringBuilder();

        for (char c : originalString.toCharArray()) {
            if (c != charToRemove) {
                stringBuilder.append(c);
            }
        }

        String modifiedString = stringBuilder.toString();
        return modifiedString;

    }



    public static String toTitleCase(String str) {

        if (str == null) {
            return null;
        }

        boolean space = true;
        StringBuilder builder = new StringBuilder(str);
        final int len = builder.length();

        for (int i = 0; i < len; ++i) {
            char c = builder.charAt(i);
            if (space) {
                if (!Character.isWhitespace(c)) {
                    // Convert to title case and switch out of whitespace mode.
                    builder.setCharAt(i, Character.toTitleCase(c));
                    space = false;
                }
            } else if (Character.isWhitespace(c)) {
                space = true;
            } else {
                builder.setCharAt(i, Character.toLowerCase(c));
            }
        }

        return builder.toString();
    }
}
