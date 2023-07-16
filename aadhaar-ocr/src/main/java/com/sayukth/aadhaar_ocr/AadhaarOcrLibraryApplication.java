package com.sayukth.aadhaar_ocr;

import android.content.Context;

public class AadhaarOcrLibraryApplication {

    private static Context context;

    public static void init(Context applicationContext) {
        context = applicationContext;
    }


    public static Context getAppContext() {
        return context;
    }

}
