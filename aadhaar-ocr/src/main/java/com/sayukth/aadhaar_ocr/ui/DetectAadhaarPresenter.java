package com.sayukth.aadhaar_ocr.ui;

import static com.sayukth.aadhaar_ocr.constants.AadhaarOcrConstants.AADHAAR;
import static com.sayukth.aadhaar_ocr.constants.AadhaarOcrConstants.AADHAAR_BIGQR_OCR;
import static com.sayukth.aadhaar_ocr.constants.AadhaarOcrConstants.AADHAAR_OCR_BACK_SIDE;
import static com.sayukth.aadhaar_ocr.constants.AadhaarOcrConstants.AADHAAR_OCR_FRONT_SIDE;
import static com.sayukth.aadhaar_ocr.constants.AadhaarOcrConstants.ADDRESS;
import static com.sayukth.aadhaar_ocr.constants.AadhaarOcrConstants.Address;
import static com.sayukth.aadhaar_ocr.constants.AadhaarOcrConstants.BIRTH;
import static com.sayukth.aadhaar_ocr.constants.AadhaarOcrConstants.Birth;
import static com.sayukth.aadhaar_ocr.constants.AadhaarOcrConstants.DATE;
import static com.sayukth.aadhaar_ocr.constants.AadhaarOcrConstants.DATE_OF_YEAR;
import static com.sayukth.aadhaar_ocr.constants.AadhaarOcrConstants.DAUGHTER_OF;
import static com.sayukth.aadhaar_ocr.constants.AadhaarOcrConstants.DOB;
import static com.sayukth.aadhaar_ocr.constants.AadhaarOcrConstants.FATHER;
import static com.sayukth.aadhaar_ocr.constants.AadhaarOcrConstants.FEMALE;
import static com.sayukth.aadhaar_ocr.constants.AadhaarOcrConstants.GENDER;
import static com.sayukth.aadhaar_ocr.constants.AadhaarOcrConstants.GOVERNMENT;
import static com.sayukth.aadhaar_ocr.constants.AadhaarOcrConstants.INDIA;
import static com.sayukth.aadhaar_ocr.constants.AadhaarOcrConstants.MALE;
import static com.sayukth.aadhaar_ocr.constants.AadhaarOcrConstants.NAME;
import static com.sayukth.aadhaar_ocr.constants.AadhaarOcrConstants.OF;
import static com.sayukth.aadhaar_ocr.constants.AadhaarOcrConstants.OTHER;
import static com.sayukth.aadhaar_ocr.constants.AadhaarOcrConstants.SON_OF;
import static com.sayukth.aadhaar_ocr.constants.AadhaarOcrConstants.TRANS;
import static com.sayukth.aadhaar_ocr.constants.AadhaarOcrConstants.WIFE_OF;
import static com.sayukth.aadhaar_ocr.constants.AadhaarOcrConstants.YEAR;
import static com.sayukth.aadhaar_ocr.constants.AadhaarOcrConstants.YEAR_OF;
import static com.sayukth.aadhaar_ocr.constants.AadhaarOcrConstants.YOB;
import static com.sayukth.aadhaar_ocr.constants.AadhaarOcrConstants.Year;
import static com.sayukth.aadhaar_ocr.ocrpreferences.AadhaarOcrPreferences.Key.AADHAAR_OCR_SCAN_SIDE;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.Text;
import com.sayukth.aadhaar_ocr.error.ActivityException;
import com.sayukth.aadhaar_ocr.ocrpreferences.AadhaarOcrPreferences;
import com.sayukth.aadhaar_ocr.utils.DateUtils;
import com.sayukth.aadhaar_ocr.utils.ParseQRUtil;
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
    private static final String AADHAAR_REGEX = "^[2-9]{1}[0-9]{3}\\s[0-9]{4}\\s[0-9]{4}$";
    private static final String NAME_REGEX = "^[a-zA-Z\\s]*$";
    private static final String DATE_FORMAT = "01-01-";



    HashMap<String, String> backAadhaarMap = new HashMap<>();

    public DetectAadhaarPresenter(DetectAadhaarContract.View detectAadharView, Activity activity) throws IOException {
        this.detectAadharView = detectAadharView;
        this.activity = activity;
    }

    @Override
    public String getImageDataAsText(Bitmap photo) {

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
            Log.d("IMAGEtEXT", "Text Block: " + imageText);
            ocrImageText.append(textBlock.getValue() + "\n");
            Log.d("Language : ", imageText + " : " + textBlock.getLanguage());
            stringBuilder.append("#" + imageText + "#");
            stringBuilder.append("\n");

            boolean isFrontMatch = imageText.contains(DOB) || imageText.contains(Year) || imageText.contains(OF) || imageText.contains(Birth);
            boolean isBackMatch = imageText.contains(SON_OF) || imageText.contains(Address) || imageText.contains(WIFE_OF) || imageText.contains(DAUGHTER_OF);

            if(isFrontMatch){
                getTextType(imageText);
            }  else if(isBackMatch){
                try {
                    setFatherOrSpouseMetaData(imageText.toString());
                } catch (ActivityException e) {
                    throw new RuntimeException(e);
                }
            } else if(containsAadhaar(imageText)){
                getTextTypeBigQR(imageText);
            }

        }

        detectAadharView.showAadhaarInfo(metadataMap);

        // Return the extracted text from the image
        return imageText;
    }


    public void getTextType(String val) {

        try {

            if (val.contains("\n")) {
                String valArr[] = val.split("\n");

                if (valArr.length > 0) {
                    for (int newlineIdx = 0; newlineIdx < valArr.length; newlineIdx++) {
                        System.out.println(" if : " + valArr[newlineIdx]);
                        setMetaData(valArr[newlineIdx]);
                        setAadhaarId(valArr[newlineIdx]);
                    }
                }
            } else {
                System.out.println(" else : " + val);
                setMetaData(val);
            }


        } catch (ActivityException e) {

        }

    }

    public void getTextTypeBigQR(String val) {
        try {
            if (val.contains("\n")) {
                String valArr[] = val.split("\n");

                if (valArr.length > 0) {
                    for (int newlineIdx = 0; newlineIdx < valArr.length; newlineIdx++) {
                        System.out.println(" if : " + valArr[newlineIdx]);
                        setAadhaarId(valArr[newlineIdx]);
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


        if (srcVal.contains(ADDRESS)) {
            String metaData = FATHER;

            String text = StringSplitUtils.getLastPartOfStringBySplitString(ocrImageText.toString(), ":");
           System.out.println("Text : "+ text);
            String fsnameWithCareOf = StringSplitUtils.getFirstPartOfStringBySplitString(text.toString(), ",");
            System.out.println("FS : "+ fsnameWithCareOf);
            String fsname = StringSplitUtils.getLastPartOfStringBySplitString(fsnameWithCareOf.trim(), " ");
            System.out.println("FS : " + fsname);

            metadataMap.put(metaData, fsname.trim());
        }

    }

    public void setAadhaarId(String val) throws ActivityException{
        detectAadharView.showImageText(String.valueOf(ocrImageText));
        String aadharRegex = AADHAAR_REGEX;

        Matcher aadharMatcher = getPatternMatcher(AADHAAR_REGEX, val);

        String metaData = AADHAAR;
        String tgtVal = val;


        if (aadharMatcher.matches()) {

            metadataMap.put(metaData, tgtVal.trim());
        }
    }


    public void setMetaData(String val) throws ActivityException {

        try {

            String aadharRegex = AADHAAR_REGEX;
            String nameRegex = NAME_REGEX;

            Matcher aadharMatcher = getPatternMatcher(aadharRegex, val);
            Matcher nameMatcher = getPatternMatcher(nameRegex, val);

            String metaData = OTHER;
            String srcVal = val.toUpperCase();
            String tgtVal = val;

            if (srcVal.contains(MALE) || srcVal.contains(FEMALE) || srcVal.contains(TRANS)) {
                metaData = GENDER;
                if (val.contains("/")) {
                    tgtVal = val.split("/")[1];
                } else {
                    if (val.contains(" ")) {
                        tgtVal = val.split(" ")[1];
                    }
                }

            } else if (srcVal.contains(YEAR) || srcVal.contains(BIRTH) || srcVal.contains(DATE) || srcVal.contains(DOB) ||
                    srcVal.contains(YEAR_OF) || srcVal.contains(YOB)) {
                metaData = DATE_OF_YEAR;

                if (val.contains(":")) {
                    tgtVal = val.split(":")[1];
                } else {
                    String dobValArr[] = val.split(" ");
                    int dobValLen = dobValArr.length;
                    tgtVal = dobValArr[dobValLen - 1];
                }

                tgtVal = getFormatedDate(tgtVal);

            } else if (aadharMatcher.matches()) {
                metaData = AADHAAR;
            } else if (nameMatcher.matches() && !srcVal.contains(GOVERNMENT) && !srcVal.contains(INDIA) && !srcVal.contains(FATHER)) {
                metaData = NAME;

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
                return DATE_FORMAT + datevalue;
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

    public void handleQrCodeScan(String scanContent) {

        HashMap<String, String> parsedDataStr =  ParseQRUtil.parseScannedData(scanContent);
        detectAadharView.showAadhaarInfo(parsedDataStr);
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







}
