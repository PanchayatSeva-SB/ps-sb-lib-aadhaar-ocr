package com.sayukth.aadhaarOcr.ui;

import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.AADHAAR;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.AADHAAR_;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.ADDRESS;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.AUTHENTICATION;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.AUTHORITY;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.Address;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.BIRTH;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.Birth;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.CITIZENSHIP;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.DATE;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.DATE_OF_YEAR;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.DIGITALLY;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.DOB;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.ENROLLMENT;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.ENROLLMENT_NUMBER;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.FATHER;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.FEEMALE;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.FEMALE;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.GENDER;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.GOVERNMENT;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.HEMALE;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.INDIA;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.MALE;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.MOBILE;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.NAME;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.OF;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.OFFLINE;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.OTHER;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.SIGNED;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.TEMALE;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.TO;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.TRANS;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.UNIQUE;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.VERIFICATION;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.XML;
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
import com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants;
import com.sayukth.aadhaarOcr.error.ActivityException;
import com.sayukth.aadhaarOcr.ocrpreferences.AadhaarOcrPreferences;
import com.sayukth.aadhaarOcr.utils.DateUtils;
import com.sayukth.aadhaarOcr.utils.ParseQRUtil;
import com.sayukth.aadhaarOcr.utils.StringSplitUtils;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DetectAadhaarPresenter implements DetectAadhaarContract.Presenter {
    private static final String TAG = "DetectAadhaarPresent";
    HashMap<String, String> metadataMap = new HashMap<String, String>();
    private DetectAadhaarContract.View detectAadharView;
    private Activity activity;
    StringBuilder ocrImageText = new StringBuilder();
    private static final String AADHAAR_REGEX = "^[2-9]{1}[0-9]{3}\\s*[0-9]{4}\\s*[0-9]{4}$";
    private static final String NAME_REGEX = "^[a-zA-Z\\s]*$";
    private static final String DATE_FORMAT = "01-01-";
    private static final String PINCODE_REGEX = ".*\\b\\d{6}\\b.*";
    private static final String VID_PATTERN = ".*\\bVID:\\s*\\d{16}\\b.*";
    private static final String MOBILE_REGEX ="\\b[6789]\\d{9}\\b";

    Pattern datePattern = Pattern.compile("(\\d{4}[-/]\\d{1,2}[-/]\\d{1,2})|((\\d{1,2})[-/](\\d{1,2})[-/](\\d{4}))");
    // Regex pattern
    Pattern datePatternWithAnyCharacter = Pattern.compile(
            "(\\d{4})\\D(\\d{1,2})\\D(\\d{1,2})" +  // YYYY-anything-MM-anything-DD
                    "|(\\d{1,2})\\D(\\d{1,2})\\D(\\d{4})"   // DD-anything-MM-anything-YYYY
    );
    Pattern onlyYear = Pattern.compile("\\d{4}");

    Pattern namePattern = Pattern.compile("(?:D/O|S/O|W/O|C/O|DIO|SIO|WIO|CIO)[:\\s]+([^,]+)", Pattern.CASE_INSENSITIVE);

    Pattern mobilePattern = Pattern.compile(MOBILE_REGEX);

    Pattern addressPattern = Pattern.compile("(C/O|S/O|D/O|W/O)\\s+[A-Z\\s]+,\\s*(.*?)(?=\\n(?:Mobile:|PIN Code:|$))", Pattern.DOTALL);


    List<String> genderListFemale = List.of("FEMALE", "TEMALE", "HEMALE", "FEEMALE");
    List<String> genderListMale = List.of("MALE");
    List<String> genderListTrans = List.of("TRANS");

    // Combine all gender lists into a single list
    List<String> genderKeywords = new ArrayList<>();





    /**
     * Constructor for DetectAadhaarPresenter.
     *
     * @param detectAadharView The view interface for displaying extracted Aadhaar details.
     * @param activity         The activity context.
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

            AadhaarOcrPreferences.getInstance().put(AadhaarOcrPreferences.Key.OCR_CAPTURED_TEXT, imageText);

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

    public void getTextTypeMobileNumber(String val) {
        try {
            if (val.contains("\n")) {
                String valArr[] = val.split("\n");

                if (valArr.length > 0) {
                    for (int newlineIdx = 0; newlineIdx < valArr.length; newlineIdx++) {
                        System.out.println(" if : " + valArr[newlineIdx]);
                        setMobileNumber(valArr[newlineIdx]);
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
            String formattedFatherName = "";
            List<String> possibleNames = new ArrayList<>();

            String text = StringSplitUtils.getLastPartOfStringBySplitString(ocrImageText.toString(), ":");
            Matcher matcher = namePattern.matcher(text);

            while (matcher.find()) {  // Collect all matches
                possibleNames.add(matcher.group(1).trim().replaceAll("\\n+", " "));
            }

            // Get most probable name
            if (!possibleNames.isEmpty()) {
                formattedFatherName = getMostProbableName(possibleNames);
            }

            // ✅ If valid, store and exit
            if (isValidName(formattedFatherName)) {
                metadataMap.put(metaData, formattedFatherName.trim());
                return;
            }

            possibleNames.clear();
            List<String> validLines = new ArrayList<>();
            String[] lines = val.split("\n");

            for (String line : lines) {
                if (line.matches(PINCODE_REGEX) || line.matches(AADHAAR_REGEX) || line.matches(VID_PATTERN)) continue;
                if (line.contains("@") || line.contains("1947")) continue;
                if (line.matches(".*\\b(lock|unlock|aadhaar|security|obligated|entities|unique|Authority)\\b.*")) continue;

                validLines.add(line.trim());
            }

            for (String validLine : validLines) {
                Matcher lineMatcher = namePattern.matcher(validLine);
                while (lineMatcher.find()) {
                    possibleNames.add(lineMatcher.group(1).trim());
                }
            }

            // Get most probable name
            if (!possibleNames.isEmpty()) {
                formattedFatherName = getMostProbableName(possibleNames);
            }

            // ✅ If valid, store and exit
            if (isValidName(formattedFatherName)) {
                metadataMap.put(metaData, formattedFatherName.trim());
                return;
            }

            for (String line : lines) {
                Matcher manualMatcher = namePattern.matcher(line);
                if (manualMatcher.find()) {
                    formattedFatherName = manualMatcher.group(1).trim();
                    break; // Take first match
                }
            }

            // ✅ Store final extracted name
            metadataMap.put(metaData, formattedFatherName.trim());
        }
    }


    /**
     * Validates if extracted name is meaningful.
     * - Should have more than 2 characters
     * - Should contain only alphabets and spaces
     */
    private boolean isValidName(String name) {
        return name.length() > 2 && name.matches("^[a-zA-Z\\s]*$");
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
            String fsName = matcher.group(2).trim(); // Extract the name

            // Remove any 6-digit PIN code from fsName
            fsName = fsName.replaceAll("\\b\\d{6}\\b", "").trim();

            metadataMap.put(metaData, fsName);
        }
    }


    /**
     * Extracts mobile number from text.
     *
     * @param text Extracted text.
     */
    public void setMobileNumber(String text) {
        Pattern pattern = Pattern.compile(MOBILE_REGEX); // Indian mobile numbers start with 6,7,8, or 9
        Matcher matcher = pattern.matcher(text);

        String metaData = MOBILE;

        if (matcher.find()) {
            Log.e("mobile", "" + matcher.group());// Return the found mobile number
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
        Matcher matcher = addressPattern.matcher(text);

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
    public void setAadhaarId(String val) throws ActivityException {
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

            genderKeywords.addAll(genderListTrans);
            genderKeywords.addAll(genderListFemale);
            genderKeywords.addAll(genderListMale);

            for (String gender : genderKeywords) {
                if (srcVal.contains(gender)) {
                    metaData = GENDER;
                    if (genderListFemale.contains(gender)) {
                        tgtVal = AadhaarOcrConstants.FEMALE_STR;
                    } else if (genderListMale.contains(gender)) {
                        tgtVal = AadhaarOcrConstants.MALE_STR;
                    } else if (genderListTrans.contains(gender)) {
                        tgtVal = AadhaarOcrConstants.TRANS_STR;
                    }
                    break; // Exit the loop once a match is found
                }
            }
            if (srcVal.contains(YEAR) || srcVal.contains(BIRTH) || srcVal.contains(DATE) || srcVal.contains(DOB) ||
                    srcVal.contains(YEAR_OF) || srcVal.contains(YOB)) {
                metaData = DATE_OF_YEAR;

                Matcher matcher1 = datePattern.matcher(val);
                Matcher matcher2 = datePatternWithAnyCharacter.matcher(val);
                Matcher matcher3 = onlyYear.matcher(val);


                if (matcher1.find()) {
                    tgtVal = matcher1.group();
                } else if (matcher2.find()) {
                    StringBuilder formattedDate = new StringBuilder();
                    if (matcher2.group(1) != null) {  // YYYY-any-MM-any-DD format
                        formattedDate.append(matcher2.group(1)).append("/")
                                .append(matcher2.group(2)).append("/")
                                .append(matcher2.group(3));
                    } else {  // DD-any-MM-any-YYYY format
                        formattedDate.append(matcher2.group(4)).append("/")
                                .append(matcher2.group(5)).append("/")
                                .append(matcher2.group(6));
                    }
                    tgtVal = formattedDate.toString();
                } else if (matcher3.find()) {
                    tgtVal = matcher3.group();

                }

                tgtVal = getFormatedDate(tgtVal);

            } else if (aadharMatcher.matches()) {
                metaData = AADHAAR;
            } else if (!srcVal.contains(DIGITALLY) && !srcVal.contains(SIGNED) && !srcVal.contains(UNIQUE) && !srcVal.contains(AUTHORITY) && !srcVal.contains(GOVERNMENT) && !srcVal.contains(INDIA) && !srcVal.contains(FATHER) && !srcVal.contains(AADHAAR_) && !srcVal.contains(CITIZENSHIP) && !srcVal.contains(VERIFICATION) && !srcVal.contains(AUTHENTICATION) && !srcVal.contains(OFFLINE) && !srcVal.contains(XML) && !srcVal.contains(ENROLLMENT)) {
                if (nameMatcher.matches()) {
                    metaData = NAME;
                }


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

        HashMap<String, String> parsedDataStr = null;
        try {
            parsedDataStr = ParseQRUtil.parseScannedData(scanContent.trim());
            detectAadharView.showAadhaarInfo(parsedDataStr);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

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

    private boolean containsMobileNumber(String text) {
        // Mobile number regex pattern (10-digit numbers starting with 6-9)

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
        } else if (containsMobileNumber(textValue)) {
            getTextTypeMobileNumber(textValue);
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
             throw new RuntimeException(e);
        }

        // Extract and set mobile number and address from the scanned text
        setMobileNumber(textValue);
        setAddress(textValue);
    }

    // Function to select the most probable name
    private String getMostProbableName(List<String> names) {
        names.sort(Comparator.comparingInt(String::length).reversed()); // Prefer longer name
        return names.get(0); // Return the first one (longest valid name)
    }


}
