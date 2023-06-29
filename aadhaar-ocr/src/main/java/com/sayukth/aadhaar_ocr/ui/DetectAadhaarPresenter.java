package com.sayukth.aadhaar_ocr.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.sayukth.aadhaar_ocr.error.ActivityException;
import com.sayukth.aadhaar_ocr.ui.DetectAadhaarContract;
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
        ocrImageText.setLength(0);
        metadataMap.clear();
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
            ocrImageText.append(textBlock.getValue()+"\n");
            Log.d("Language : ", imageText + " : " + textBlock.getLanguage());
            stringBuilder.append("#" + imageText + "#");
            stringBuilder.append("\n");

            if (imageText.contains("Address")) {
                try {
                    setFatherOrSpouseMetaData(imageText.toString());
                } catch (ActivityException e) {
                    e.printStackTrace();
                }
            } else {
                getTextType(imageText);
            }
        }

        detectAadharView.showAadharInfo(metadataMap);

    }

    public void getTextType(String val) {
        try {
            String type = " ";

//            Log.e("RAMESH","RAMSVALUE"+val);

            if (val.contains("\n")) {
                String valArr[] = val.split("\n");

                if (valArr.length > 0) {
                    for (int newlineIdx = 0; newlineIdx < valArr.length; newlineIdx++) {
                        System.out.println(" if : "+ valArr[newlineIdx]);
                        setMetaData(valArr[newlineIdx]);
                    }
                }
            } else {
                System.out.println(" else : "+ val);
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
            System.out.println("FS : "+fsname);

            metadataMap.put(metaData, fsname.trim());
        }

    }

    public void setMetaData(String val) throws ActivityException {
        Log.e("HELLO" ,"HELLO"+val);
        try {
            detectAadharView.showImageText(String.valueOf(ocrImageText));
            String aadharRegex = "^[2-9]{1}[0-9]{3}\\s[0-9]{4}\\s[0-9]{4}$";
            String nameRegex = "^[a-zA-Z\\s]*$";

            Matcher aadharMatcher = getPatternMatcher(aadharRegex,val);
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

            }  else if (srcVal.contains("YEAR")|| srcVal.contains("BIRTH") || srcVal.contains("DATE") || srcVal.contains("DOB") ||
                    srcVal.contains("YEAR OF") || srcVal.contains("YOB") || srcVal.contains("Birth") || srcVal.contains("Bir") || srcVal.contains("Brth") ||
                    srcVal.contains("Bart")||srcVal.contains("dbt")||srcVal.contains("yeor")|| srcVal.contains("yeor")||srcVal.contains("sos6 jooYear Of Birth")
            ||srcVal.contains("C Near of Birth")||srcVal.contains("sOE8 bo\\Year of Bidh ")||srcVal.contains("e i6DOB")||srcVal.contains("PGSOS of Birth")||srcVal.contains("ors6 50Year of birth")||srcVal.contains("S SoBo yooYear of Birth")
            ||srcVal.contains("ossooYear of Birth")||srcVal.contains("oso so\\Year of Birth")||srcVal.contains("cOcoyEar of Bith")||srcVal.contains("ear of Birth")||srcVal.contains("ostoyearOf birth ")
            ||srcVal.contains("ostoyearOf birth")||srcVal.contains("08toYear Of birth ")||srcVal.contains("080Year of birth")||srcVal.contains("os850\\Year Of Birth 19463")||srcVal.contains("D d6\\DOB ")||srcVal.contains("PGSOS of Birth 1E")||srcVal.contains("s Year of Bth")||srcVal.contains("oSScYear of Birth")
            ||srcVal.contains("o Year of Birth")||srcVal.contains("sso8Year of Birth")||srcVal.contains("No ¥aar of Bn")||srcVal.contains("¥er ct B")||srcVal.contains("osYear of Birth")||srcVal.contains("SYear or Birth:195335")||srcVal.contains("osaYear of Birth")||srcVal.contains("Da j6\\DOB")
            ||srcVal.contains("36\\DOB") ||srcVal.contains("\\DOB")||srcVal.contains("\\DOB 2412\\15")||srcVal.contains("oss ydoYeer of Birth")||srcVal.contains("ooear ofbrth 195")||srcVal.contains("S oYeer of Bih")||srcVal.contains("boB 22031975")||srcVal.contains("oso jooYear of Birth")||srcVal.contains("a Dob")
                    ||srcVal.contains("o tuYear of Birth")||srcVal.contains("oove af Brth")||srcVal.contains("oYear of Birth 1949 R")||srcVal.contains("Oo doYear of Birth")||srcVal.contains("soso or YeurofBirth")||srcVal.contains("D ode\\Year of Birth")||srcVal.contains("D OOB")||srcVal.contains("sS SoYear of Birth")
                    ||srcVal.contains("DA a DOB") ||srcVal.contains("D s6/DOB")||srcVal.contains("/DOB:DOB: ")||srcVal.contains("a3oNea ol Bith 196")||srcVal.contains("So8 oYear of Birth ")||srcVal.contains("HS BoNear ol Bith ")||srcVal.contains("oBooeYear of Birth")||srcVal.contains("3030year of Birth")||srcVal.contains("S o0 soYear of Birth:")
                    ||srcVal.contains("DO Year of Birith")||srcVal.contains("Soso 8oYsar of Birth")||srcVal.contains("sorfear ol Binh")||srcVal.contains("SosoooYear ear of Binh")||srcVal.contains("Bart")||srcVal.contains("S SoS3so Year of Birth ")||srcVal.contains("0SS ootYear ol Birth ")||srcVal.contains("KoS  oorYear of Birth ")
                    ||srcVal.contains("cho dcrYear ot Birth") ||srcVal.contains("DB B6DOB")||srcVal.contains("OSoYear of Binh")||srcVal.contains("sos5Borear of Birth")||srcVal.contains("so65o/year of Birth")||srcVal.contains("O ofYear of Birth ")||srcVal.contains("oe dd/DOB")||srcVal.contains("SSoSyaYear of Birh")||srcVal.contains("S os8 soYear of Birth")
                    ||srcVal.contains("g osoyoo/Year of Birth")||srcVal.contains("clYear of Birtl ")||srcVal.contains("RD dA DOB")||srcVal.contains("SssoYear of Birth")||srcVal.contains("os ooYear of Birth")||srcVal.contains("oso oYeor of Birth ")||srcVal.contains("SS belYear of Birth")||srcVal.contains("306Year ti Bi 1092")
                    ||srcVal.contains("soS 6o/Year of Binth") ||srcVal.contains("AS oS y5oYear of Birth")||srcVal.contains("s ooYear of Birth")||srcVal.contains("s ooYear of Birth")||srcVal.contains("e DOB")||srcVal.contains("SEeYear of Birth ")||srcVal.contains("dSZDOE:")||srcVal.contains("s ossoYear of Birth")||srcVal.contains("sosoysoYear of Binth")
                    ||srcVal.contains("")||srcVal.contains("n dte d")||srcVal.contains("sos Y So Year of Birth")||srcVal.contains("tedBirth")||srcVal.contains("S3oks ocYeor of Blrth")||srcVal.contains("SS08 ocYear of Birth")||srcVal.contains("D5 3ADOB")||srcVal.contains("DS So3SyooYear of Bith")
                    ||srcVal.contains("S Sos8y8oY6ar of Birnh:197") ||srcVal.contains("DS So3SyooYear of Bith")||srcVal.contains("oso doYear of Birth")||srcVal.contains("oso aYear of Birth")||srcVal.contains("oKE y5oYear of Bith")||srcVal.contains("hospoYear of Bith")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")
                    ||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")
                    ||srcVal.contains("") ||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")
                    ||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")
                    ||srcVal.contains("") ||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")
                    ||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")
                    ||srcVal.contains("") ||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")
                    ||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")
                    ||srcVal.contains("") ||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")
                    ||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")
                    ||srcVal.contains("") ||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")
                    ||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")
                    ||srcVal.contains("") ||srcVal.contains("") ||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")
                    ||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")
                    ||srcVal.contains("") ||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")
                    ||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")
                    ||srcVal.contains("") ||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")
                    ||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")
                    ||srcVal.contains("") ||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")
                    ||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")
                    ||srcVal.contains("") ||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")
                    ||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")
                    ||srcVal.contains("") ||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")
                    ||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")
                    ||srcVal.contains("") ||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")
                    ||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")
                    ||srcVal.contains("") ||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")
                    ||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")
                    ||srcVal.contains("") ||srcVal.contains("") ||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")
                    ||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")
                    ||srcVal.contains("") ||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")
                    ||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")
                    ||srcVal.contains("") ||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")
                    ||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")
                    ||srcVal.contains("") ||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")
                    ||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")
                    ||srcVal.contains("") ||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")
                    ||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")
                    ||srcVal.contains("") ||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")
                    ||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")
                    ||srcVal.contains("") ||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")
                    ||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")
                    ||srcVal.contains("") ||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")
                    ||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")||srcVal.contains("")
                    ||srcVal.contains("")) {


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

            }else if (aadharMatcher.matches()) {
                metaData = "Mobile";
            } else if (srcVal.contains("ADDRESS")) {
                metaData = "FATHER";

                String text = StringSplitUtils.getLastPartOfStringBySplitString(ocrImageText.toString(), ":");
//            System.out.println("Text : "+ text);
                String fsnameWithCareOf = StringSplitUtils.getFirstPartOfStringBySplitString(text.toString(), ",");
//            System.out.println("FS : "+ fsnameWithCareOf);
                String fsname = StringSplitUtils.getLastPartOfStringBySplitString(fsnameWithCareOf.trim(), " ");
                System.out.println("FS : "+fsname);
                tgtVal = fsname.trim();
            }
//            else if(srcVal.contains("FATHER") || srcVal.contains("CO") || srcVal.contains("C/O") || srcVal.contains("C|O") || srcVal.contains("G0") || srcVal.contains("G|O") || srcVal.contains("S0") ||srcVal.contains("s|o") ||srcVal.contains("S/O") || srcVal.contains(" ")) {
//                metaData = "FATHER";
//                if (val.contains(":")) {
//                    tgtVal = val.split(":")[1];
////                    Log.i("Father Value", tgtVal);
//                    if(fatherOrSpouseMatcher.find()){
//                        String fsNameStr=fatherOrSpouseMatcher.group(1);
//                        Log.i("Father Value", val);
//                        tgtVal=val;
//                    }
//                } else {
////                    Log.i("Father Value", val);
//                    tgtVal = val;
//                }
//                Log.i("fs : ", tgtVal);
//            }

            metadataMap.put(metaData, tgtVal.trim());
            System.out.println("metaDataMap :"+ metadataMap.toString());
        } catch (ActivityException e) {
            Log.i(TAG,e.getMessage());
            Log.i(TAG,e.getMessage());
            throw new ActivityException(e);//
        }
    }

    private String getFormatedDate(String datevalue) throws ActivityException {
        datevalue = (datevalue != null && !datevalue.isEmpty()) ? datevalue.trim() : "";

        if (datevalue.matches("\\d{4}")) {
            //This block will execute when we have only year in the aadhaar card
            return "01-01-" + datevalue;
        } else {
            return DateUtils.aAdhaarDateFormated(datevalue);
        }
    }

    private Matcher getPatternMatcher(String value, String val) {
        Pattern pattern = Pattern.compile("(\\d{4})");
        Matcher patternMatcher = pattern.matcher(value);

        return patternMatcher;

    }




}
