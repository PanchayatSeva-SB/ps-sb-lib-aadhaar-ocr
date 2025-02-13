package com.sayukth.aadhaarOcr;

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
