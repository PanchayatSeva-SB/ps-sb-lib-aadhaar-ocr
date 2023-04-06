package com.example.ocrpresenter.ui.OCR;

import android.graphics.Bitmap;

import java.util.HashMap;

public interface DetectAadhaarContract {
    interface View {
        void showAadhaarDetectOptions();

        void showCameraOptions();

        void showImageText(String imageText);

        void showAadharInfo(HashMap<String, String> map);
    }

    interface Presenter {
        void getImageDataAsText(Bitmap bitmap);
    }
}
