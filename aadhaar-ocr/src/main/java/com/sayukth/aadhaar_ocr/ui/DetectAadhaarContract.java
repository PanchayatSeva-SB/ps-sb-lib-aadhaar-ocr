package com.sayukth.aadhaar_ocr.ui;

import android.graphics.Bitmap;

import java.util.HashMap;

public interface DetectAadhaarContract {
    interface View {
        void showAadhaarDetectOptions();

        void showCameraOptions();

        void showImageText(String imageText);

        void showAadhaarInfo(HashMap<String, String> map);
    }

    interface Presenter {
        void getImageDataAsText(Bitmap bitmap);
    }
}
