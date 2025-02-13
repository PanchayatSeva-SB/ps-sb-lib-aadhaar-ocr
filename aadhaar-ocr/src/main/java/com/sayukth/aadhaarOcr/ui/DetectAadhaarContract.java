package com.sayukth.aadhaarOcr.ui;

import android.graphics.Bitmap;

import java.util.HashMap;

public interface DetectAadhaarContract {
    interface View {

        void showImageText(String imageText);

        void showAadhaarInfo(HashMap<String, String> map);
    }

    interface Presenter {
        String getImageDataAsText(Bitmap bitmap);

        void handleQrCodeScan(String scanContent);
    }
}
