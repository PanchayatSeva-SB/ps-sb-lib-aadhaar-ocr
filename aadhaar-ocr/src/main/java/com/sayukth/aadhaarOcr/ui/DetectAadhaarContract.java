package com.sayukth.aadhaarOcr.ui;

import android.graphics.Bitmap;

import com.sayukth.aadhaarOcr.Exceptions.PresenterException;

import java.util.HashMap;

public interface DetectAadhaarContract {
    interface View {

        void showImageText(String imageText);

        void showAadhaarInfo(HashMap<String, String> map);
    }

    interface Presenter {
        String getImageDataAsText(Bitmap bitmap) throws PresenterException;

        HashMap<String, String> handleQrCodeScan(String scanContent) throws PresenterException;
    }
}
