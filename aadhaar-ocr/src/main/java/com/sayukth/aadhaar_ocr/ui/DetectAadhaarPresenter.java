package com.sayukth.aadhaar_ocr.ui;

import static com.sayukth.aadhaar_ocr.constants.AadhaarOcrConstants.AADHAAR_OCR_BACK_SIDE;
import static com.sayukth.aadhaar_ocr.constants.AadhaarOcrConstants.AADHAAR_OCR_FRONT_SIDE;
import static com.sayukth.aadhaar_ocr.ocrpreferences.AadhaarOcrPreferences.Key.AADHAAR_OCR_SCAN_SIDE;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.sayukth.aadhaar_ocr.error.ActivityException;
import com.sayukth.aadhaar_ocr.ocrpreferences.AadhaarOcrPreferences;
import com.sayukth.aadhaar_ocr.utils.DateUtils;
import com.sayukth.aadhaar_ocr.utils.StringSplitUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DetectAadhaarPresenter implements DetectAadhaarContract.Presenter {
    private static final String TAG = "DetectAadhaarPresent";
    HashMap<String, String> metadataMap = new HashMap<String, String>();
    private DetectAadhaarContract.View detectAadharView;
    private Activity activity;
    StringBuilder ocrImageText = new StringBuilder();

    HashMap<String, String> backAadhaarMap = new HashMap<>();

    public DetectAadhaarPresenter(DetectAadhaarContract.View detectAadharView, Activity activity) throws IOException {
        this.detectAadharView = detectAadharView;
        this.activity = activity;
    }

    @Override
    public void getImageDataAsText(Bitmap photo) {

        String aadhaarOcrScanSide = AadhaarOcrPreferences.getInstance().getString(AADHAAR_OCR_SCAN_SIDE, "");

        ocrImageText.setLength(0);
        TextRecognizer textRecognizer = new TextRecognizer.Builder(activity).build();
        Frame imageFrame = new Frame.Builder()
                .setBitmap(photo)
                .build();
        String imageText = "";
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.setLength(0);
        SparseArray<TextBlock> textBlockSparseArray = textRecognizer.detect(imageFrame);

        for (int i = 0; i < textBlockSparseArray.size(); i++) {
            TextBlock textBlock = textBlockSparseArray.get(textBlockSparseArray.keyAt(i));
            imageText = textBlock.getValue();
            ocrImageText.append(textBlock.getValue() + "\n");
            Log.d("Language : ", imageText + " : " + textBlock.getLanguage());
            stringBuilder.append("#" + imageText + "#");
            stringBuilder.append("\n");


            if (aadhaarOcrScanSide.equals(AADHAAR_OCR_BACK_SIDE)) {
                try {
                    setFatherOrSpouseMetaData(imageText.toString());
                } catch (ActivityException e) {
                    throw new RuntimeException(e);
                }
            } else if (aadhaarOcrScanSide.equals(AADHAAR_OCR_FRONT_SIDE)) {
                getTextType(imageText);
            }
        }

        detectAadharView.showAadhaarInfo(metadataMap);

    }

    public void getTextType(String val) {
        try {
            String type = " ";

//            Log.e("RAMESH","RAMSVALUE"+val);

            if (val.contains("\n")) {
                String valArr[] = val.split("\n");

                if (valArr.length > 0) {
                    for (int newlineIdx = 0; newlineIdx < valArr.length; newlineIdx++) {
                        System.out.println(" if : " + valArr[newlineIdx]);
                        setMetaData(valArr[newlineIdx]);
                    }
                }
            } else {
                System.out.println(" else : " + val);
                setMetaData(val);
            }
        } catch (ActivityException e) {

        }

    }

    public void setFatherOrSpouseMetaData(String val) throws ActivityException {
        detectAadharView.showImageText(String.valueOf(ocrImageText));

        String srcVal = val.toUpperCase();
        if (srcVal.contains("ADDRESS")) {
            String metaData = "FATHER";

            String text = StringSplitUtils.getLastPartOfStringBySplitString(ocrImageText.toString(), ":");
//            System.out.println("Text : "+ text);
            String fsnameWithCareOf = StringSplitUtils.getFirstPartOfStringBySplitString(text.toString(), ",");
//            System.out.println("FS : "+ fsnameWithCareOf);
            String fsname = StringSplitUtils.getLastPartOfStringBySplitString(fsnameWithCareOf.trim(), " ");
            System.out.println("FS : " + fsname);

            metadataMap.put(metaData, fsname.trim());
        }

    }


    public void setMetaData(String val) throws ActivityException {
        try {
            detectAadharView.showImageText(String.valueOf(ocrImageText));

            String aadharRegex = "^[2-9]{1}[0-9]{3}\\s[0-9]{4}\\s[0-9]{4}$";
            String nameRegex = "^[a-zA-Z\\s]*$";

            Matcher aadharMatcher = getPatternMatcher(aadharRegex, val);
            Matcher nameMatcher = getPatternMatcher(nameRegex, val);

            String metaData = "OTHER";
            String srcVal = val.toUpperCase();
            String tgtVal = val;

            if (srcVal.contains("MALE") || srcVal.contains("FEMALE") || srcVal.contains("TRANS")) {
                metaData = "GENDER";
                if (val.contains("/")) {
                    tgtVal = val.split("/")[1];
                } else {
                    if (val.contains(" ")) {
                        tgtVal = val.split(" ")[1];
                    }
                }

            } else if (srcVal.contains("YEAR") || srcVal.contains("BIRTH") || srcVal.contains("DATE") || srcVal.contains("DOB") ||
                    srcVal.contains("YEAR OF") || srcVal.contains("YOB")) {
                metaData = "DATE_OF_YEAR";

                if (val.contains(":")) {
                    tgtVal = val.split(":")[1];
                } else {
                    String dobValArr[] = val.split(" ");
                    int dobValLen = dobValArr.length;
                    tgtVal = dobValArr[dobValLen - 1];
                }

                tgtVal = getFormatedDate(tgtVal);

            } else if (aadharMatcher.matches()) {
                metaData = "AADHAR";
            } else if (nameMatcher.matches() && !srcVal.contains("GOVERNMENT") && !srcVal.contains("INDIA") && !srcVal.contains("FATHER")) {
                metaData = "NAME";

//            } else {
//                if (srcVal != null && srcVal != "") {
//                    if (srcVal.contains(".")) {
//                        Log.i(". contains", srcVal);
//
//                        String dotRemovedString = StringSplitUtils.removeSymbolFromString(srcVal, '.');
//
//                        Log.i("dot removed ", dotRemovedString);
//
//                        Matcher nameOtherMatcher = getPatternMatcher(nameRegex, dotRemovedString);
//
//
//                        if (nameOtherMatcher.matches() && !dotRemovedString.contains("GOVERNMENT") && !dotRemovedString.contains("INDIA") && !dotRemovedString.contains("FATHER")) {
//                            metaData = "NAME";
//                            tgtVal = StringSplitUtils.toTitleCase(dotRemovedString);
//                        }
//
//                    }
//                }
            }

            metadataMap.put(metaData, tgtVal.trim());
        } catch (ActivityException e) {
            Log.i(TAG, e.getMessage());
            Log.i(TAG, e.getMessage());
            throw new ActivityException(e);
        }
    }

    private String getFormatedDate(String datevalue) throws ActivityException {
        try {
            datevalue = (datevalue != null && !datevalue.isEmpty()) ? datevalue.trim() : "";

            if (datevalue.matches("\\d{4}")) {
                //This block will execute when we have only year in the aadhaar card
                return "01-01-" + datevalue;
            } else {
                return DateUtils.aAdhaarDateFormated(datevalue);
            }
        } catch (ActivityException execption) {
            Log.i(TAG, execption.getMessage());
            throw new ActivityException(execption);
        }
    }

    private Matcher getPatternMatcher(String regex, String value) {
        Pattern pattern = Pattern.compile(regex);
        Matcher patternMatcher = pattern.matcher(value);

        return patternMatcher;
    }


}
