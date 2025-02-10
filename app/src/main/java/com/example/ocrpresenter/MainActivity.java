package com.example.ocrpresenter;

import static com.sayukth.aadhaar_ocr.constants.AadhaarOcrConstants.AADHAAR_REQUEST_IMAGE;
import static com.sayukth.aadhaar_ocr.constants.AadhaarOcrConstants.RESULT_TIMEOUT;
import static com.sayukth.aadhaar_ocr.constants.AadhaarOcrConstants.SCANNER_REQUEST_CODE;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.sayukth.aadhaar_ocr.AadhaarOcrLibraryApplication;
import com.sayukth.aadhaar_ocr.SayukthUtils;
import com.sayukth.aadhaar_ocr.ocrpreferences.AadhaarOcrPreferences;
import com.sayukth.aadhaar_ocr.ui.CustomCameraLaunchActivity;
import com.sayukth.aadhaar_ocr.ui.DetectAadhaarContract;
import com.sayukth.aadhaar_ocr.ui.DetectAadhaarPresenter;
import com.sayukth.aadhaar_ocr.ui.QRScanningActivity;
import com.sayukth.aadhaar_ocr.utils.StringSplitUtils;

import java.io.IOException;
import java.text.StringCharacterIterator;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity implements DetectAadhaarContract.View {
    TextView tvOcrData;
    ImageView ivOcr;
    TextView tvOcrImageText;
    private DetectAadhaarContract.Presenter presenter;
    private DetectAadhaarContract.View detectAadharView;
    private StringCharacterIterator fatherOrSpouseName;
    private boolean isFrontCaptured = false;
    private boolean isBackCaptured = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AadhaarOcrLibraryApplication.init(getApplicationContext());

        tvOcrData = findViewById(R.id.tv_aadhaar_data);
        ivOcr = findViewById(R.id.iv_ocr);
        tvOcrImageText = findViewById(R.id.tv_ocr_image_data);

        SayukthUtils.getName();

        try {
            presenter = new DetectAadhaarPresenter(this, MainActivity.this);
            detectAadharView = this;
        } catch (IOException e) {
            e.printStackTrace();
        }

        LinearLayout detectInput = findViewById(R.id.btn_detect_input);

        detectInput.setOnClickListener(v -> {

            Intent intent = new Intent(MainActivity.this, QRScanningActivity.class);
            startActivityForResult(intent, SCANNER_REQUEST_CODE);

        });
    }



    @Override
    public void showImageText(String imageText) {
        if (imageText != null && !imageText.isEmpty()) {
            tvOcrImageText.setText(imageText);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        boolean isBigQROCR = false;

        try {

            // Check if the result is from the scanner activity
            if (requestCode == SCANNER_REQUEST_CODE) {
                if (resultCode == RESULT_OK) {
                    // Get the scanned Aadhaar data from the intent
                    String scannedAadhaar = intent.getStringExtra("SCANNED_AADHAAR");

                    if (scannedAadhaar != null) {
                        // Display the scanned Aadhaar data (or use it as needed)
                        if(scannedAadhaar.startsWith("<?xml")) {
                            presenter.handleQrCodeScan(scannedAadhaar);
                        } else{
                            presenter.handleQrCodeScan(scannedAadhaar);
                            launchCameraForBigQROCRCapture();
                        }
                    }
                } else if (resultCode == RESULT_TIMEOUT) {
                    Toast.makeText(MainActivity.this, "QR not Detected Switching to OCR", Toast.LENGTH_SHORT).show();
                    launchCameraForFrontSideCapture();
                } else {
                    Toast.makeText(MainActivity.this, "Scan Cancelled", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == AADHAAR_REQUEST_IMAGE) {

                if (resultCode == Activity.RESULT_OK) {


                    String imagePath = intent.getStringExtra("path");

                    // Convert the image path to a Bitmap and display it
                    Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                    ivOcr.setImageBitmap(bitmap);
                    presenter.getImageDataAsText(bitmap);


                    // Extract image text
                    String imageText = presenter.getImageDataAsText(bitmap);
                    tvOcrImageText.setText(imageText);


                    // Check if front or back side is captured
                    if (!isFrontCaptured && isFrontMatch(imageText)) {
                        isFrontCaptured = true; // Mark front as captured
                        Toast.makeText(this, "Front side captured.", Toast.LENGTH_SHORT).show();
                        AadhaarOcrPreferences.getInstance().put(AadhaarOcrPreferences.Key.IS_FRONT_SIDE_CAPTURED, true);

                    } else if (!isBackCaptured && isBackMatch(imageText)) {
                        isBackCaptured = true; // Mark back as captured
                        AadhaarOcrPreferences.getInstance().put(AadhaarOcrPreferences.Key.IS_BACK_SIDE_CAPTURED, true);
                        Toast.makeText(this, "Back side captured.", Toast.LENGTH_SHORT).show();
                    } else if (containsAadhaar(imageText)) {
                        isBigQROCR = true;
                        Toast.makeText(this, "Capturing complete", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Unable to identify side. Please retry.", Toast.LENGTH_SHORT).show();
                    }

                    if(!isBigQROCR) {
                        if (AadhaarOcrPreferences.getInstance().getBoolean(AadhaarOcrPreferences.Key.IS_FRONT_SIDE_CAPTURED) && AadhaarOcrPreferences.getInstance().getBoolean(AadhaarOcrPreferences.Key.IS_BACK_SIDE_CAPTURED)) {

                            AadhaarOcrPreferences.getInstance().put(AadhaarOcrPreferences.Key.IS_FRONT_SIDE_CAPTURED, false);
                            AadhaarOcrPreferences.getInstance().put(AadhaarOcrPreferences.Key.IS_BACK_SIDE_CAPTURED, false);
                            Toast.makeText(this, "Capture Complete", Toast.LENGTH_SHORT).show();
                        } else if (!AadhaarOcrPreferences.getInstance().getBoolean(AadhaarOcrPreferences.Key.IS_FRONT_SIDE_CAPTURED)) {
                            AadhaarOcrPreferences.getInstance().put(AadhaarOcrPreferences.Key.IS_FLIP_GIF_SHOW, true);
                            launchCameraForFrontSideCapture();
                        } else if (!AadhaarOcrPreferences.getInstance().getBoolean(AadhaarOcrPreferences.Key.IS_BACK_SIDE_CAPTURED)) {
                            AadhaarOcrPreferences.getInstance().put(AadhaarOcrPreferences.Key.IS_FLIP_GIF_SHOW, true);
                            launchCameraForBackSideCapture();
                        }
                    }



                }
            }
            else {
                Toast.makeText(MainActivity.this, "Scan cancelled", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isFrontMatch(String imageText) {
        return imageText.contains("DOB") || imageText.contains("Year") || imageText.contains("Of") || imageText.contains("Birth");
    }

    private boolean isBackMatch(String imageText) {
        return imageText.contains("S/O") || imageText.contains("Address") || imageText.contains("W/O") || imageText.contains("D/O");
    }


    @Override
    public void showAadhaarInfo(HashMap<String, String> map) {
        try {
            Log.i("Aadhaar : ", String.valueOf(map));

            map.forEach((k, v) -> System.out.println(("K: " + k + ":" + "V: " + v)));

            StringBuilder aadhaarData = new StringBuilder();

            String aadharId = map.get("AADHAR");
            if (aadharId != null) {
                aadharId = aadharId.replaceAll("\\s", "");
                aadhaarData.append(" Aadhaar Id : " + aadharId + ", \n");
            }

            String name = map.get("NAME");
            if (name != null && !name.equals(" ")) {
                aadhaarData.append(" Sur Name : " + StringSplitUtils.getFirstPartOfStringBySplitString(name, " ") + ", \n");
                aadhaarData.append(" Name : " + StringSplitUtils.getLastPartOfStringBySplitString(name, " ") + ", \n");
            }
            String fsname = map.get("FATHER");
            if (fsname != null && !fsname.equals(" ")) {
                aadhaarData.append(" Father or Spouse Name : " + fsname + ", \n");
            }

            String dob = map.get("DATE_OF_YEAR");
            if (dob != null) {
                aadhaarData.append(" Dob : " + dob + ", \n");
            }

            String genderStr = map.get("GENDER");
            if (genderStr != "" && genderStr != null) {
                if (genderStr.equals("M") || genderStr.startsWith("M")) {
                    aadhaarData.append(" Gender : Male" + ", \n");
                } else if (genderStr.equals("F") || genderStr.startsWith("F")) {
                    aadhaarData.append(" Gender : Female" + ", \n");
                } else {
                    aadhaarData.append(" Gender : Other" + ", \n");
                }
            }

            tvOcrData.setText(aadhaarData);

            String otherStr = map.get("FATHER");
            if (otherStr != null && !otherStr.equals(" ")) {
                String nameRegex = "^[a-zA-Z\\s]*$";
                String fsNameStr = Arrays.toString(otherStr.split(nameRegex, 1));
                aadhaarData.append("FATHER NAME:").append(fatherOrSpouseName).append(" ,\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void launchCameraForFrontSideCapture() {
        isFrontCaptured = false;
        isBackCaptured = false;
        Intent intent = new Intent(MainActivity.this, CustomCameraLaunchActivity.class);
        intent.putExtra("scan type","Front side");
        startActivityForResult(intent, AADHAAR_REQUEST_IMAGE);

    }

    public void launchCameraForBackSideCapture() {
        Intent intent = new Intent(MainActivity.this, CustomCameraLaunchActivity.class);
        intent.putExtra("scan type","Back side");
        startActivityForResult(intent, AADHAAR_REQUEST_IMAGE);
    }

    public void launchCameraForBigQROCRCapture() {
        Intent intent = new Intent(MainActivity.this, CustomCameraLaunchActivity.class);
        intent.putExtra("scan type","Big QR OCR");
        startActivityForResult(intent, AADHAAR_REQUEST_IMAGE);
    }

    private boolean containsAadhaar(String text) {
        String aadharRegex = "^[2-9]{1}[0-9]{3}\\s[0-9]{4}\\s[0-9]{4}$";
        Pattern pattern = Pattern.compile(aadharRegex);

        // Split the string into lines or words to simulate "text blocks"
        String[] lines = text.split("\\r?\\n");

        for (String line : lines) {
            if (pattern.matcher(line).find()) {
                Log.d("TAG", "Aadhaar detected: " + line);
                return true;
            }
        }
        return false;
    }


}