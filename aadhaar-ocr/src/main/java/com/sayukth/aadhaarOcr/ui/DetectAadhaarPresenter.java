package com.sayukth.aadhaarOcr.ui;

import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.AADHAAR;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.ADDRESS;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.Address;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.BIRTH;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.Birth;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.DATE;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.DATE_OF_YEAR;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.DOB;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.ENROLLMENT_NUMBER;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.FATHER;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.FEMALE;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.GENDER;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.GOVERNMENT;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.INDIA;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.MALE;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.MOBILE;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.NAME;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.OF;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.OTHER;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.TO;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.TRANS;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.YEAR;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.YEAR_OF;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.YOB;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.Year;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.sayukth.aadhaarOcr.error.ActivityException;
import com.sayukth.aadhaarOcr.utils.DateUtils;
import com.sayukth.aadhaarOcr.utils.ParseQRUtil;
import com.sayukth.aadhaarOcr.utils.StringSplitUtils;

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


    /**
     * Constructor for DetectAadhaarPresenter.
     *
     * @param detectAadharView The view interface for displaying extracted Aadhaar details.
     * @param activity The activity context.
     * @throws IOException If an input/output exception occurs.
     */
    public DetectAadhaarPresenter(DetectAadhaarContract.View detectAadharView, Activity activity) throws IOException {
        this.detectAadharView = detectAadharView;
        this.activity = activity;
    }

    /**
     * Extracts text from the given image using OCR.
     *
     * @param photo The Bitmap image of the Aadhaar card.
     * @return The extracted text from the image.
     */
    @Override
    public String getImageDataAsText(Bitmap photo) {

        ocrImageText.setLength(0);
        TextRecognizer textRecognizer = new TextRecognizer.Builder(activity).build();
        Frame imageFrame = new Frame.Builder()
                .setBitmap(photo)
                .build();
        String imageText = "";
        StringBuilder stringBuilder = new StringBuilder();
        SparseArray<TextBlock> textBlockSparseArray = textRecognizer.detect(imageFrame);

        for (int i = 0; i < textBlockSparseArray.size(); i++) {
            TextBlock textBlock = textBlockSparseArray.get(textBlockSparseArray.keyAt(i));
            String textValue = textBlock.getValue();
            imageText = textValue;
            Log.d("IMAGEtEXT", "Text Block: " + imageText);
            ocrImageText.append(textValue).append("\n");
            Log.d("Language : ", imageText + " : " + textBlock.getLanguage());
            stringBuilder.append("#").append(textValue).append("#\n");
            stringBuilder.append("\n");

            classifyTextBlock(imageText);

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

    /**
     * Extracts father or spouse name from text.
     *
     * @param val Extracted text.
     * @throws ActivityException If an error occurs while processing.
     */
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

    /**
     * Extracts father or spouse name using specific patterns.
     *
     * @param val Extracted text.
     * @throws ActivityException If an error occurs while processing.
     */
    public void setFatherOrSpouseMetaDataForPattern(String val) throws ActivityException {
        detectAadharView.showImageText(String.valueOf(ocrImageText));

        String metaData = FATHER;

        String srcVal = val.toUpperCase();

        Pattern pattern = Pattern.compile("(CIO|C/O|S/O|D/O|W/O|C/O:|S/O:|D/O:|W/O:|CO|WO|SO|DO|SIO|DIO|WIO)\\s+([A-Z\\s]+),");
        Matcher matcher = pattern.matcher(srcVal);

        if (matcher.find()) {
            String fsName =  matcher.group(2).trim(); // Extract and return the name
            metadataMap.put(metaData, fsName.trim());
        }

    }

    /**
     * Extracts mobile number from text.
     *
     * @param text Extracted text.
     */
    public void setMobileNumber(String text) {
        Pattern pattern = Pattern.compile("\\b[6789]\\d{9}\\b"); // Indian mobile numbers start with 6,7,8, or 9
        Matcher matcher = pattern.matcher(text);

        String metaData = MOBILE;

        if (matcher.find()) {
            Log.e("mobile",""+matcher.group());// Return the found mobile number
            String mobileNumber = matcher.group();
            metadataMap.put(metaData, mobileNumber);
        }

    }

    /**
     * Extracts address from text.
     *
     * @param text Extracted text.
     */
    public void setAddress(String text) {
        String address = " ";  // Initialize with a default value
        String pincode = " "; // Initialize with a default value

        String metaData = ADDRESS;

        // Regex to match address starting after Father/Spouse Name and stopping at "Mobile" or "PIN Code"
        Pattern pattern = Pattern.compile("(C/O|S/O|D/O|W/O)\\s+[A-Z\\s]+,\\s*(.*?)(?=\\n(?:Mobile:|PIN Code:|$))", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            Log.e("address", "" + matcher.group(2).trim());
            address = matcher.group(2).trim();
        }

        // Extract the last 6-digit PIN Code
        Pattern pincodePattern = Pattern.compile("\\b\\d{6}\\b");
        Matcher pincodeMatcher = pincodePattern.matcher(text);

        while (pincodeMatcher.find()) {
            pincode = pincodeMatcher.group(); // Get the last occurrence of 6-digit number
        }

        // Combine address and PIN Code safely
        String finalAddress = address + pincode;

        metadataMap.put(metaData, finalAddress);
    }

    /**
     * Extracts Aadhaar ID from text.
     *
     * @param val Extracted text.
     * @throws ActivityException If an error occurs while processing.
     */
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

    /**
     * Categorizes extracted metadata.
     *
     * @param val Extracted text.
     * @throws ActivityException If an error occurs while processing.
     */
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

    /**
     * Parses and processes QR code scan result.
     *
     * @param scanContent The scanned QR code content.
     */
    public void handleQrCodeScan(String scanContent) {

        HashMap<String, String> parsedDataStr =  ParseQRUtil.parseScannedData(scanContent);
        detectAadharView.showAadhaarInfo(parsedDataStr);
    }

    /**
     * Checks if text contains Aadhaar number.
     *
     * @param text The input text.
     * @return True if Aadhaar number is found, false otherwise.
     */
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

    /**
     * Checks if the given image text matches the front side of an Aadhaar card.
     * The front side typically contains the date of birth or year of birth.
     *
     * @param imageText The extracted text from the Aadhaar card image.
     * @return true if the text indicates the front side, false otherwise.
     */
    private boolean isFrontMatch(String imageText) {
        return imageText.contains(DOB) || imageText.contains(Year) ||
                imageText.contains(OF) || imageText.contains(Birth);
    }

    /**
     * Checks if the given image text matches the back side of an Aadhaar card.
     * The back side typically contains the address details.
     *
     * @param imageText The extracted text from the Aadhaar card image.
     * @return true if the text indicates the back side, false otherwise.
     */
    private boolean isBackMatch(String imageText) {
        return imageText.contains(Address);
    }

    /**
     * Checks if the given image text indicates a full scan of the front side.
     * A full scan usually contains enrollment details.
     *
     * @param imageText The extracted text from the Aadhaar card image.
     * @return true if the text indicates a full scan of the front side, false otherwise.
     */
    private boolean isFrontSideFullScan(String imageText) {
        return imageText.contains(TO) || imageText.contains(ENROLLMENT_NUMBER);
    }


    /**
     * Classifies the extracted text block and processes it accordingly.
     * Determines whether the text belongs to the front side, back side, full scan, or Aadhaar number.
     *
     * @param textValue The extracted text from the Aadhaar card image.
     */
    private void classifyTextBlock(String textValue) {
        if (isFrontMatch(textValue)) {
            // If the text matches front side patterns, process it as front side data
            getTextType(textValue);
        } else if (isBackMatch(textValue)) {
            try {
                // If the text matches back side patterns, process father/spouse metadata
                setFatherOrSpouseMetaData(textValue);
            } catch (ActivityException e) {
                Log.e("Exception", "Error processing back match", e);
            }
        } else if (isFrontSideFullScan(textValue)) {
            // If the text indicates a full front side scan, process relevant details
            processFullFrontScan(textValue);
        } else if (containsAadhaar(textValue)) {
            // If Aadhaar number is detected, process it separately
            getTextTypeBigQR(textValue);
        }
    }

    /**
     * Processes a full front-side scan of the Aadhaar card.
     * Clears metadata and extracts information such as name, father/spouse details,
     * mobile number, and address from the text.
     *
     * @param textValue The extracted text from the Aadhaar card image.
     */
    private void processFullFrontScan(String textValue) {
        metadataMap.clear(); // Clear previous metadata before processing new data
        getTextType(textValue); // Extract general text type information

        try {
            // Extract father/spouse details if present in the text
            setFatherOrSpouseMetaDataForPattern(textValue);
        } catch (ActivityException e) {
            Log.e("Exception", "Error processing pattern match", e);
        }

        // Extract and set mobile number and address from the scanned text
        setMobileNumber(textValue);
        setAddress(textValue);
    }








}
