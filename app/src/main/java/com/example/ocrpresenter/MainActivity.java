package com.example.ocrpresenter;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ocrpresenter.ui.OCR.AadhaarDetectActivity;
import com.example.ocrpresenter.ui.OCR.DetectAadhaarContract;
import com.example.ocrpresenter.ui.OCR.DetectAadhaarPresenter;
import com.example.ocrpresenter.utils.StringSplitUtils;
import com.google.android.gms.vision.barcode.Barcode;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.sayukth.aadhaar_ocr.SayukthUtils;

import java.io.IOException;
import java.text.StringCharacterIterator;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements DetectAadhaarContract.View {
    private static final int AADHAAR_REQUEST_IMAGE = 100;
    TextView tvOcrData;
    ImageView ivOcr;
    TextView tvOcrImageText;
    private DetectAadhaarContract.Presenter presenter;
    private StringCharacterIterator fatherOrSpouseName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvOcrData = findViewById(R.id.tv_aadhaar_data);
        ivOcr = findViewById(R.id.iv_ocr);
        tvOcrImageText = findViewById(R.id.tv_ocr_image_data);



        try {
            presenter = new DetectAadhaarPresenter(this, MainActivity.this);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    @Override
    public void showAadhaarDetectOptions() {
        Dexter.withActivity(MainActivity.this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {

                            showCameraOptions();
                        } else {
                            // TODO - handle permission denied case

                        }

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    @Override
    public void showCameraOptions() {
        AadhaarDetectActivity.showImagePickerOptions(this, new AadhaarDetectActivity.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                launchCameraIntent();
            }
        });
    }

    @Override
    public void showImageText(String imageText) {
        if (imageText != null && !imageText.isEmpty()){
            tvOcrImageText.setText(imageText);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        try {

            if (requestCode == AADHAAR_REQUEST_IMAGE) {
                if (resultCode == Activity.RESULT_OK) {
                    Uri uri = intent.getParcelableExtra("path");
                    try {
                        // You can update this bitmap to your server
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
//                        extractText(bitmap);
                        ivOcr.setImageBitmap(bitmap);
                        presenter.getImageDataAsText(bitmap);
                        // loading profile image from local cache
//                        loadProfile(uri.toString());
                    } catch (IOException e) {
                        e.printStackTrace();

                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void scanAadhaar(View view) {
        showAadhaarDetectOptions();

    }


    private void launchCameraIntent() {
        Intent intent = new Intent(MainActivity.this, AadhaarDetectActivity.class);
        intent.putExtra(AadhaarDetectActivity.INTENT_IMAGE_PICKER_OPTION, AadhaarDetectActivity.REQUEST_IMAGE_CAPTURE);

        // setting aspect ratio
        intent.putExtra(AadhaarDetectActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(AadhaarDetectActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(AadhaarDetectActivity.INTENT_ASPECT_RATIO_Y, 1);

        // setting maximum bitmap width and height
        intent.putExtra(AadhaarDetectActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(AadhaarDetectActivity.INTENT_BITMAP_MAX_WIDTH, 1000);
        intent.putExtra(AadhaarDetectActivity.INTENT_BITMAP_MAX_HEIGHT, 1000);

        startActivityForResult(intent, AADHAAR_REQUEST_IMAGE);
    }


    @Override
    public void showAadharInfo(HashMap<String, String> map) {
        try {

            map.forEach((k, v) -> System.out.println(("K: "+k + ":" + "V"+v)));

            StringBuilder aadhaarData = new StringBuilder("Aadhaar Data : \n");
//            Log.i("MainActivity", map + " ");
            String aadharId = map.get("AADHAR");
            if (aadharId != null) {
                aadharId = aadharId.replaceAll("\\s", "");
                aadhaarData.append(" Aadhaar Id : " + aadharId + ", \n");
            }

            String name = map.get("NAME");
            if (name != null   && name != " ")
            {
                aadhaarData.append(" Sur Name : " +StringSplitUtils.getFirstPartOfStringBySplitString(name," ") + ", \n");
                aadhaarData.append("  Name : " + StringSplitUtils.getLastPartOfStringBySplitString(name, " ") + ", \n");
            }
            String fsname = map.get("FATHER");
            if(fsname != null && fsname != " "){
                aadhaarData.append(" FatherNameorSpouse : " + fsname + ", \n");
            }

            String dob = map.get("DATE_OF_YEAR");
            if (dob != null) {
                aadhaarData.append(" Dob : " + dob + " , \n");
            }

            String genderStr = map.get("GENDER");
            if (genderStr != "" && genderStr != null) {
                if (genderStr.equals("M") || genderStr.startsWith("M")) {
                    aadhaarData.append(" Gender : Male " + ", \n");
                } else if (genderStr.equals("F") || genderStr.startsWith("F")) {
                    aadhaarData.append(" Gender : Female " + ", \n");
                } else {
                    aadhaarData.append(" Gender : Other " + ", \n");
                }
            }

//            String Mobile = map.get("Mobile");
//            if (dob != null) {
//                aadhaarData.append(" Dob : " + dob + ", \n");


            tvOcrData.setText(aadhaarData);


            String otherStr = map.get("FATHER");
//            PanchayatSevaUtilities.showToast(otherStr + " ");
            if (otherStr != null && !otherStr.equals(" ")) {

                    String nameRegex = "^[a-zA-Z\\s]*$";
                    String fsNameStr= Arrays.toString(otherStr.split(nameRegex, 1));
                    fatherOrSpouseName.setText(otherStr);
                    aadhaarData.append("FATHER NAME:").append(fatherOrSpouseName).append(" ,\n");
//                   String fsNameStr = PanchayatSevaUtilities.splitString(otherStr);
//                    fatherOrSpouseName.setText(PanchayatSevaUtilities.stringToTitleCaseString(fsNameStr));

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}