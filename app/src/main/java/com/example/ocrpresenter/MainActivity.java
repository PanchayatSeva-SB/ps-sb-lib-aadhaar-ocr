package com.example.ocrpresenter;

import static android.content.ContentValues.TAG;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.AADHAAR_REQUEST_IMAGE;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.Address;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.Birth;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.DOB;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.ENROLLMENT_NUMBER;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.GENERIC_EXCEPTION_MSSG;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.OF;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.RESULT_TIMEOUT;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.SCANNER_REQUEST_CODE;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.TO;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.Year;
import static com.sayukth.aadhaarOcr.ocrpreferences.AadhaarOcrPreferences.Key.IS_SIGNATURE_DATA_BIG_QR_OCR;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.sayukth.aadhaarOcr.AadhaarOcrLibraryApplication;
import com.sayukth.aadhaarOcr.SayukthUtils;
import com.sayukth.aadhaarOcr.ocrpreferences.AadhaarOcrPreferences;
import com.sayukth.aadhaarOcr.ui.CustomCameraLaunchActivity;
import com.sayukth.aadhaarOcr.ui.DetectAadhaarContract;
import com.sayukth.aadhaarOcr.ui.DetectAadhaarPresenter;
import com.sayukth.aadhaarOcr.ui.QRScanningActivity;
import com.sayukth.aadhaarOcr.utils.StringSplitUtils;

import java.text.StringCharacterIterator;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity implements DetectAadhaarContract.View {
    TextView tvOcrData;
    ImageView ivOcr;
    TextView tvOcrImageText;
    Button scanMobileNumber;
    private DetectAadhaarContract.Presenter presenter;
    private DetectAadhaarContract.View detectAadharView;
    private StringCharacterIterator fatherOrSpouseName;
    private boolean isFrontCaptured = false;
    private boolean isBackCaptured = false;

    private static final String SCANNED_AADHAAR = "SCANNED_AADHAAR";
    private static final String XML_FORMAT = "<?xml";
    private static final String XML_FORMAT_ALTERNATE = "<PrintLetterBarcodeData";
    private static final String AADHAAR_REGEX = "^[2-9]{1}[0-9]{3}\\s[0-9]{4}\\s[0-9]{4}$";
    private static final String MOBILE_REGEX = "\\b[6789]\\d{9}\\b";
    private static final String QPDA_FORMAT = "<QPDA";
    private static final String QPDB_FORMAT = "<QPDB";
    private static final String QDB_FORMAT ="<QDB";
    private static final String QDA_FORMAT = "<QDA";


    boolean isBigQROCR = false;
    boolean isPattern3 = false;
    boolean isMObileNumberCapture;
    boolean isSignatureDataBigQR = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            AadhaarOcrLibraryApplication.init(getApplicationContext());

            tvOcrData = findViewById(R.id.tv_aadhaar_data);
            ivOcr = findViewById(R.id.iv_ocr);
            tvOcrImageText = findViewById(R.id.tv_ocr_image_data);
            scanMobileNumber = findViewById(R.id.scan_mobile_number);

            SayukthUtils.getName();

            presenter = new DetectAadhaarPresenter(this, MainActivity.this);
            detectAadharView = this;

            LinearLayout detectInput = findViewById(R.id.btn_detect_input);

            detectInput.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, QRScanningActivity.class);
                startActivityForResult(intent, SCANNER_REQUEST_CODE);
            });

            scanMobileNumber.setOnClickListener(v -> {
                AadhaarOcrPreferences.getInstance().put(AadhaarOcrPreferences.Key.IS_MOBILE_NUMBER_CAPTURED, true);
                Intent intent = new Intent(MainActivity.this, CustomCameraLaunchActivity.class);
                intent.putExtra(getString(R.string.scan_type), "Mobile Number");
                startActivityForResult(intent, AADHAAR_REQUEST_IMAGE);
            });

        } catch (Exception e) {
            Log.e(TAG, e.getMessage() != null ? e.getMessage() : GENERIC_EXCEPTION_MSSG, e);
        }
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
        if (requestCode == SCANNER_REQUEST_CODE) {
            handleScannerResult(resultCode, intent);
        } else if (requestCode == AADHAAR_REQUEST_IMAGE) {
            handleAadhaarImageResult(resultCode, intent);
        } else {
            Toast.makeText(MainActivity.this, getString(R.string.capture_complete), Toast.LENGTH_SHORT).show();
        }

    }

    private boolean isFrontMatch(String imageText) {
        return imageText.contains(DOB) || imageText.contains(Year) || imageText.contains(OF) || imageText.contains(Birth);
    }

    private boolean isBackMatch(String imageText) {
        return imageText.contains(Address);
    }

    private boolean isFrontSideFullScan(String imageText) {
        return imageText.contains(TO) || imageText.contains(ENROLLMENT_NUMBER);
    }


    @Override
    public void showAadhaarInfo(HashMap<String, String> map) {
        try {
            Log.i("Aadhaar : ", String.valueOf(map));

            map.forEach((k, v) -> System.out.println(("Key: " + k + ":" + "Value: " + v)));

            StringBuilder aadhaarData = new StringBuilder();

            String aadharId = map.get("AADHAR");
            if (aadharId != null) {
                aadharId = aadharId.replaceAll("\\s", "");
                aadhaarData.append("Aadhaar Id : " + aadharId + ", \n");
            }

            String name = map.get("NAME");
            if (name != null && !name.equals(" ")) {
                aadhaarData.append("Sur Name : " + StringSplitUtils.getFirstPartOfStringBySplitString(name, " ") + ", \n");
                aadhaarData.append("Name : " + StringSplitUtils.getLastPartOfStringBySplitString(name, " ") + ", \n");
            }
            String fsname = map.get("FATHER");
            if (fsname != null && !fsname.equals(" ")) {
                aadhaarData.append("Father or Spouse Name : " + fsname + ", \n");
            }

            String dob = map.get("DATE_OF_YEAR");
            if (dob != null) {
                aadhaarData.append("Dob : " + dob + ", \n");
            }

            String genderStr = map.get("GENDER");
            if (genderStr != null && genderStr != "") {
                if (genderStr.equals("M") || genderStr.startsWith("M")) {
                    aadhaarData.append(" Gender : Male" + ", \n");
                } else if (genderStr.equals("F") || genderStr.startsWith("F")) {
                    aadhaarData.append("Gender : Female" + ", \n");
                } else {
                    aadhaarData.append("Gender : Other" + ", \n");
                }
            }

            String mobileStr = map.get("MOBILE");

            if (mobileStr != null && mobileStr != "") {
                aadhaarData.append("Mobile: " + mobileStr + ",\n");
            }

            String addressStr = map.get("ADDRESS");

            if (addressStr != null && addressStr != "") {
                aadhaarData.append("Address: " + addressStr + ",\n");
            }

            tvOcrData.setText(aadhaarData);

            String otherStr = map.get("FATHER");
            if (otherStr != null && !otherStr.equals(" ")) {
                String nameRegex = "^[a-zA-Z\\s]*$";
                String fsNameStr = Arrays.toString(otherStr.split(nameRegex, 1));
                aadhaarData.append("FATHER NAME:").append(fatherOrSpouseName).append(" ,\n");
            }

        } catch (Exception e) {
            Log.e(TAG, e.getMessage() != null ? e.getMessage() : GENERIC_EXCEPTION_MSSG, e);
        }
    }


    public void launchCameraForFrontSideCapture() {
        isFrontCaptured = false;
        isBackCaptured = false;
        Intent intent = new Intent(MainActivity.this, CustomCameraLaunchActivity.class);
        intent.putExtra(getString(R.string.scan_type), getString(R.string.front_side));
        startActivityForResult(intent, AADHAAR_REQUEST_IMAGE);

    }

    public void launchCameraForBackSideCapture() {
        Intent intent = new Intent(MainActivity.this, CustomCameraLaunchActivity.class);
        intent.putExtra(getString(R.string.scan_type), getString(R.string.back_side));
        startActivityForResult(intent, AADHAAR_REQUEST_IMAGE);
    }

    public void launchCameraForBigQROCRCapture() {
        AadhaarOcrPreferences.getInstance().put(AadhaarOcrPreferences.Key.IS_BIG_QR_OCR, true);
        Intent intent = new Intent(MainActivity.this, CustomCameraLaunchActivity.class);
        intent.putExtra(getString(R.string.scan_type), getString(R.string.big_qr_ocr));
        startActivityForResult(intent, AADHAAR_REQUEST_IMAGE);
    }

    private boolean containsAadhaar(String text) {
        String aadharRegex = AADHAAR_REGEX;
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

    private void handleScannerResult(int resultCode, Intent intent) {
        try {
            if (resultCode == RESULT_OK) {
                // Get the scanned Aadhaar data from the intent
                String scannedAadhaar = intent.getStringExtra(SCANNED_AADHAAR);

                if (scannedAadhaar != null) {

                    // Attempt to process the scanned Aadhaar data
                    presenter.handleQrCodeScan(scannedAadhaar);

                    // Check the format and decide next action
                    if (!scannedAadhaar.startsWith(XML_FORMAT) && !scannedAadhaar.contains(XML_FORMAT_ALTERNATE) && !scannedAadhaar.trim().startsWith(QPDB_FORMAT) && !scannedAadhaar.trim().startsWith(QPDA_FORMAT) && !scannedAadhaar.trim().startsWith(QDB_FORMAT) && !scannedAadhaar.trim().startsWith(QDA_FORMAT)) {
                        launchCameraForBigQROCRCapture();
                    } else if (scannedAadhaar.trim().startsWith(QPDB_FORMAT) || scannedAadhaar.trim().startsWith(QPDA_FORMAT) || scannedAadhaar.trim().startsWith(QDB_FORMAT) || scannedAadhaar.trim().startsWith(QDA_FORMAT)) {
                        AadhaarOcrPreferences.getInstance().put(IS_SIGNATURE_DATA_BIG_QR_OCR, true);
                        launchCameraForBackSideCapture();
                    }

                }
            } else if (resultCode == RESULT_TIMEOUT) {
                Toast.makeText(this, getString(R.string.qr_to_ocr_switch), Toast.LENGTH_SHORT).show();
                launchCameraForFrontSideCapture();
            } else {
                Toast.makeText(this, getString(R.string.scan_cancelled), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage() != null ? e.getMessage() : GENERIC_EXCEPTION_MSSG, e);
        }
    }


    private void handleAadhaarImageResult(int resultCode, Intent intent) {
        try {
            if (resultCode == Activity.RESULT_OK) {
                String imagePath = intent.getStringExtra(getString(R.string.path));

                // Convert the image path to a Bitmap and display it
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                ivOcr.setImageBitmap(bitmap);

                // Extract image text
                String imageText = presenter.getImageDataAsText(bitmap);
                tvOcrImageText.setText(imageText);

                if (AadhaarOcrPreferences.getInstance().getBoolean(AadhaarOcrPreferences.Key.IS_BIG_QR_OCR)) {
                    AadhaarOcrPreferences.getInstance().put(AadhaarOcrPreferences.Key.IS_BIG_QR_OCR, false);
                    if (containsAadhaar(imageText)) {
                        isBigQROCR = true;
                        showToast(R.string.capture_complete);
                    }
                } else if (AadhaarOcrPreferences.getInstance().getBoolean(AadhaarOcrPreferences.Key.IS_MOBILE_NUMBER_CAPTURED)) {
                    AadhaarOcrPreferences.getInstance().put(AadhaarOcrPreferences.Key.IS_MOBILE_NUMBER_CAPTURED, false);
                    if (containsMobileNumber(imageText)) {
                        showToast(R.string.mobile_number_captured);
                        isMObileNumberCapture = true;
                    }
                } else if(AadhaarOcrPreferences.getInstance().getBoolean(IS_SIGNATURE_DATA_BIG_QR_OCR)){
                    AadhaarOcrPreferences.getInstance().put(IS_SIGNATURE_DATA_BIG_QR_OCR, false);
                    isSignatureDataBigQR = true;
                }
                else {
                    processAadhaarSideScan(imageText);
                }

                checkCaptureCompletion();
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage() != null ? e.getMessage() : GENERIC_EXCEPTION_MSSG, e);
        }

    }

    private void processAadhaarSideScan(String imageText) {
        if (isFrontSideFullScan(imageText)) {
            isPattern3 = true;
            showToast(R.string.capture_complete);
        } else if (!isFrontCaptured && isFrontMatch(imageText)) {
            isFrontCaptured = true; // Mark front as captured
            showToast(R.string.front_side_captured);
            AadhaarOcrPreferences.getInstance().put(AadhaarOcrPreferences.Key.IS_FRONT_SIDE_CAPTURED, true);
        } else if (!isBackCaptured && isBackMatch(imageText)) {
            isBackCaptured = true; // Mark back as captured
            showToast(R.string.back_side_captured);
            AadhaarOcrPreferences.getInstance().put(AadhaarOcrPreferences.Key.IS_BACK_SIDE_CAPTURED, true);
        } else {
            showToast(R.string.unable_to_identify_side);
        }
    }

    private void checkCaptureCompletion() {
        if (!isBigQROCR && !isPattern3 && !isMObileNumberCapture && !isSignatureDataBigQR) {
            if (AadhaarOcrPreferences.getInstance().getBoolean(AadhaarOcrPreferences.Key.IS_FRONT_SIDE_CAPTURED) &&
                    AadhaarOcrPreferences.getInstance().getBoolean(AadhaarOcrPreferences.Key.IS_BACK_SIDE_CAPTURED)) {

                AadhaarOcrPreferences.getInstance().put(AadhaarOcrPreferences.Key.IS_FRONT_SIDE_CAPTURED, false);
                AadhaarOcrPreferences.getInstance().put(AadhaarOcrPreferences.Key.IS_BACK_SIDE_CAPTURED, false);
                showToast(R.string.capture_complete);
            } else {
                handleRemainingCapture();
            }
        }
    }

    private void handleRemainingCapture() {
        if (!AadhaarOcrPreferences.getInstance().getBoolean(AadhaarOcrPreferences.Key.IS_FRONT_SIDE_CAPTURED)) {
            AadhaarOcrPreferences.getInstance().put(AadhaarOcrPreferences.Key.IS_FLIP_GIF_SHOW, true);
            launchCameraForFrontSideCapture();
        } else if (!AadhaarOcrPreferences.getInstance().getBoolean(AadhaarOcrPreferences.Key.IS_BACK_SIDE_CAPTURED)) {
            AadhaarOcrPreferences.getInstance().put(AadhaarOcrPreferences.Key.IS_FLIP_GIF_SHOW, true);
            launchCameraForBackSideCapture();
        }
    }

    private void showToast(int resId) {
        Toast.makeText(this, getString(resId), Toast.LENGTH_SHORT).show();
    }

    private boolean containsMobileNumber(String text) {
        // Mobile number regex pattern (10-digit numbers starting with 6-9)
        Pattern mobilePattern = Pattern.compile(MOBILE_REGEX);

        // Split the string into lines or words to simulate "text blocks"
        String[] lines = text.split("\\r?\\n");

        for (String line : lines) {
            if (mobilePattern.matcher(line).find()) {
                Log.d("TAG", "Mobile number detected: " + line);
                return true;
            }
        }
        return false;
    }


}