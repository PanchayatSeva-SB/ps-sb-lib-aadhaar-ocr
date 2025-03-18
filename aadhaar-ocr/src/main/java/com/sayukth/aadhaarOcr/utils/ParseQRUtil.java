package com.sayukth.aadhaarOcr.utils;

import static com.yalantis.ucrop.UCropFragment.TAG;

import android.util.Log;

import com.sayukth.aadhaarOcr.error.ActivityException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class ParseQRUtil {
    private static int idx = 0;
    public static final String V2 = "V2";
    public static final String V3 = "V3";
    public static final String V_2 = "2";
    public static final String V4 = "V4";
    private static final int TERMINATOR = 255;

    // Base function to parse different formats of scanned data
    public static HashMap<String, String> parseScannedData(String scannedResult) throws XmlPullParserException, IOException, ActivityException {
        if (isXmlFormat(scannedResult)) {
            // If the result is XML, parse it as XML
            return parseXml(scannedResult);
        } else {
            // If not XML, treat it as byte-encoded data
            return parseByteEncodeData(scannedResult);
        }
    }

    // To check if the scanned data is in XML format
    private static boolean isXmlFormat(String scannedResult) {
        try {
            return scannedResult.trim().startsWith("<?xml") || scannedResult.trim().contains("<PrintLetterBarcodeData");
        } catch (Exception e){
            throw e;
        }
    }


    // To parse XML and return as HashMap
    private static HashMap<String, String> parseXml(String scannedResult) throws XmlPullParserException, IOException, ActivityException {
        HashMap<String, String> resultData = new HashMap<>();

        // Define a mapping of attribute names to desired keys
        HashMap<String, String> attributeKeyMapping = new HashMap<>();
        attributeKeyMapping.put("uid", "AADHAR");
        attributeKeyMapping.put("name", "NAME");
        attributeKeyMapping.put("gender", "GENDER");
        attributeKeyMapping.put("yob", "DATE_OF_YEAR");
        attributeKeyMapping.put("co", "FATHER");
        attributeKeyMapping.put("house", "HouseNumber");
        attributeKeyMapping.put("street", "StreetName");
        attributeKeyMapping.put("lm", "Landmark");
        attributeKeyMapping.put("vtc", "VillageTownCity");
        attributeKeyMapping.put("po", "PostOffice");
        attributeKeyMapping.put("dist", "District");
        attributeKeyMapping.put("state", "State");
        attributeKeyMapping.put("pc", "PostalCode");
        attributeKeyMapping.put("dob", "DATE_OF_BIRTH"); // Added DOB mapping

        try {
            // Clean the input XML
            scannedResult = scannedResult.trim();

            // Ensure proper XML declaration
            if (scannedResult.startsWith("</?xml")) {
                scannedResult = scannedResult.replace("</?xml", "<?xml");
            }

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new java.io.StringReader(scannedResult));

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName();
                if (eventType == XmlPullParser.START_TAG && "PrintLetterBarcodeData".equals(tagName)) {
                    for (int i = 0; i < parser.getAttributeCount(); i++) {
                        String attributeName = parser.getAttributeName(i);
                        String attributeValue = parser.getAttributeValue(i);

                        // Use the mapping to store the value with a different key
                        if (attributeKeyMapping.containsKey(attributeName)) {
                            String mappedKey = attributeKeyMapping.get(attributeName);
                            resultData.put(mappedKey, attributeValue);
                        }
                    }
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            Log.e("QR Parsing Error", "Error parsing QR code: " + e.getMessage());
            throw e;
        }

        try {
            if (resultData.containsKey("DATE_OF_YEAR")) {
                resultData.put("DATE_OF_YEAR", DateUtils.getFormatedDate(resultData.get("DATE_OF_YEAR")));
            }
        } catch (ActivityException e) {
            Log.i("QR Parsing", "Date format exception: " + e);
            throw e;
        }

        return resultData;
    }



    // To parse byte-encoded data and return as HashMap
    private static HashMap<String, String> parseByteEncodeData(String scanData) throws ActivityException {
        HashMap<String, String> resultData = new HashMap<>();

        try {

            if(scanData == null || scanData.trim().isEmpty()){
                Log.e("scan data is null","scan data is null");
                return resultData;
            }

            BigInteger bInt = new BigInteger(scanData);
            byte[] bIntByteArray = bInt.toByteArray();
            System.out.println("scan data: " + scanData);

            // Decompress the data
            byte[] decompressed = decompress(bIntByteArray);
            System.out.println("Decompressed data: " + new String(decompressed));

            ByteArrayInputStream bin = new ByteArrayInputStream(decompressed);
            byte[] result = new byte[5000];

            int count = getNextValue(bin, result);
            String emailMobilePresentBitIndicatorStr = new String(result, 0, count, java.nio.charset.StandardCharsets.ISO_8859_1).trim();

           //emailMobileBitIndicator to check for the version of Aadhaar
            String emailMobileBitIndicator = emailMobilePresentBitIndicatorStr;

            System.out.println("emailMobilePresentBitIndicatorStr: " + emailMobilePresentBitIndicatorStr);

            if (List.of(V2, V3, V_2, V4).contains(emailMobilePresentBitIndicatorStr)) {
                if (V_2.equals(emailMobilePresentBitIndicatorStr)) {
                    Log.e("v2", "v2");
                    count = getNextValue(bin, result);
                    emailMobilePresentBitIndicatorStr = new String(result, 0, count, java.nio.charset.StandardCharsets.ISO_8859_1).trim();
                }

                if (!V_2.equals(emailMobileBitIndicator)) {
                    Log.e("not v2", "not v2");
                    count = getNextValue(bin, result);
                    String referenceId = new String(result, 0, count, java.nio.charset.StandardCharsets.ISO_8859_1).trim();
                    resultData.put("Reference ID", referenceId);
                    System.out.println("Reference ID: " + referenceId);

                    count = getNextValue(bin, result);
                    String referenceId1 = new String(result, 0, count, java.nio.charset.StandardCharsets.ISO_8859_1).trim();
                    resultData.put("Reference ID 1", referenceId1);
                    System.out.println("Reference ID 1: " + referenceId1);
                }

                // Extract and store the fields in HashMap
                extractFields(bin, resultData);
            } else {
                resultData.put("Error", "Invalid scan data");
            }
        } catch (Exception e) {
            e.printStackTrace();
            resultData.put("Error", "Error processing scan data: " + e.getMessage());
            throw new RuntimeException(e);
        }

        try {
            resultData.put("DATE_OF_YEAR", DateUtils.getFormatedDate(resultData.get("DATE_OF_YEAR")));
        } catch (ActivityException e) {
            Log.i(TAG, "date format exception" + e);
            throw e;
        }

        return resultData;
    }

    private static void extractFields(ByteArrayInputStream bin, Map<String, String> resultData) {
        try {
            byte[] result = new byte[5000];
            int count;

            count = getNextValue(bin, result);
            resultData.put("NAME", new String(result, 0, count, java.nio.charset.StandardCharsets.ISO_8859_1).trim());

            count = getNextValue(bin, result);
            resultData.put("DATE_OF_YEAR", new String(result, 0, count, java.nio.charset.StandardCharsets.ISO_8859_1).trim());

            count = getNextValue(bin, result);
            resultData.put("GENDER", new String(result, 0, count, java.nio.charset.StandardCharsets.ISO_8859_1).trim());

            count = getNextValue(bin, result);
            resultData.put("FATHER", new String(result, 0, count, java.nio.charset.StandardCharsets.ISO_8859_1).trim());

            count = getNextValue(bin, result);
            resultData.put("District", new String(result, 0, count, java.nio.charset.StandardCharsets.ISO_8859_1).trim());

            count = getNextValue(bin, result);
            resultData.put("Landmark", new String(result, 0, count, java.nio.charset.StandardCharsets.ISO_8859_1).trim());

            count = getNextValue(bin, result);
            resultData.put("House", new String(result, 0, count, java.nio.charset.StandardCharsets.ISO_8859_1).trim());

            count = getNextValue(bin, result);
            resultData.put("Location", new String(result, 0, count, java.nio.charset.StandardCharsets.ISO_8859_1).trim());

            count = getNextValue(bin, result);
            resultData.put("Pin Code", new String(result, 0, count, java.nio.charset.StandardCharsets.ISO_8859_1).trim());

            count = getNextValue(bin, result);
            resultData.put("Post Office", new String(result, 0, count, java.nio.charset.StandardCharsets.ISO_8859_1).trim());

            count = getNextValue(bin, result);
            resultData.put("State", new String(result, 0, count, java.nio.charset.StandardCharsets.ISO_8859_1).trim());

            count = getNextValue(bin, result);
            resultData.put("Street", new String(result, 0, count, java.nio.charset.StandardCharsets.ISO_8859_1).trim());

            count = getNextValue(bin, result);
            resultData.put("VTC", new String(result, 0, count, java.nio.charset.StandardCharsets.ISO_8859_1).trim());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static int getNextValue(ByteArrayInputStream bin, byte[] buffer) throws IOException {
        int c, index = 0;
        while ((c = bin.read()) != -1 && c != TERMINATOR) {
            buffer[index++] = (byte) c;
        }
        return index;
    }

    private static byte[] decompress(byte[] compressedData) throws Exception {
        try (GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(compressedData));
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = gzipInputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            return byteArrayOutputStream.toByteArray();
        }
        catch (Exception e){
            throw e;
        }
    }

    public static String cleanString(String input) {
        if (input != null) {
            // Remove any non-alphanumeric characters except spaces (if needed)
            return input.replaceAll("[^\\x20-\\x7E]", "").trim();  // Removes non-ASCII characters
        }
        return "";
    }
}