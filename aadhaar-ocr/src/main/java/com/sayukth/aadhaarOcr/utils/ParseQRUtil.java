package com.sayukth.aadhaarOcr.utils;

import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.AADHAAR;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.ADDRESS;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.DATE_OF_YEAR;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.DISTRICT;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.FATHER;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.GENDER;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.HOUSE_NUMBER;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.LANDMARK;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.LOCATION;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.NAME;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.PINCODE;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.POSTAL_CODE;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.POST_OFFICE;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.QDA_FORMAT_TAG;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.QDB_FORMAT_TAG;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.QPDA_FORMAT_TAG;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.QPDB_FORMAT_TAG;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.STATE;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.STREET_NAME;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.VILLAGE_TOWN_CITY;
import static com.yalantis.ucrop.UCropFragment.TAG;

import android.util.Log;

import com.sayukth.aadhaarOcr.Exceptions.QrParsingException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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

    private static final String XML_FORMAT = "<?xml";
    private static final String XML_FORMAT_ALTERNATE = "<PrintLetterBarcodeData";
    private static final String SIGNATURE_BIG_QR_QDB_FORMAT = "<QDB";
    private static final String SIGNATURE_BIG_QR_QDA_FORMAT = "<QDA";
    private static final String SIGNATURE_BIG_QR_QPDB_FORMAT = "<QPDB";
    private static final String SIGNATURE_BIG_QR_QPDA_FORMAT = "<QPDA";



    /**
     * Parses scanned QR code data into a HashMap of key-value pairs.
     *
     * Depending on the format of the scanned result, this method will:
     * - Parse as XML data if it's in XML format.
     * - Parse as Secured Big QR format if it matches that structure.
     * - Otherwise, parse as byte-encoded data.
     *
     * @param scannedResult The raw string data scanned from the QR code.
     * @return A HashMap containing the parsed key-value data.
     * @throws QrParsingException if any error occurs during parsing.
     */
    public static HashMap<String, String> parseScannedData(String scannedResult) throws QrParsingException {
        try {
            if (isXmlFormat(scannedResult)) {
                return parseXml(scannedResult);
            } else if (isSecuredBigQRFormat(scannedResult)) {
                return parseSignatureDataBigQR(scannedResult);
            } else {
                return parseByteEncodeData(scannedResult);
            }
        } catch (Exception e) {
            throw new QrParsingException(e);
        }
    }

    private static boolean isXmlFormat(String scannedResult) {
        return scannedResult.trim().startsWith(XML_FORMAT) || scannedResult.trim().contains(XML_FORMAT_ALTERNATE);
    }

    private static boolean isSecuredBigQRFormat(String scannedResult) {
        return scannedResult.trim().startsWith(SIGNATURE_BIG_QR_QPDB_FORMAT) || scannedResult.trim().startsWith(SIGNATURE_BIG_QR_QPDA_FORMAT) || scannedResult.trim().startsWith(SIGNATURE_BIG_QR_QDB_FORMAT) || scannedResult.trim().startsWith(SIGNATURE_BIG_QR_QDA_FORMAT);
    }


    // To parse XML and return as HashMap
    private static HashMap<String, String> parseXml(String scannedResult) throws QrParsingException {
        HashMap<String, String> resultData = new HashMap<>();
        try {
            HashMap<String, String> attributeKeyMapping = new HashMap<>();
            attributeKeyMapping.put("uid", AADHAAR);
            attributeKeyMapping.put("name", NAME);
            attributeKeyMapping.put("gender", GENDER);
            attributeKeyMapping.put("yob", DATE_OF_YEAR);
            attributeKeyMapping.put("co", FATHER);
            attributeKeyMapping.put("gname", FATHER);
            attributeKeyMapping.put("house", HOUSE_NUMBER);
            attributeKeyMapping.put("street", STREET_NAME);
            attributeKeyMapping.put("lm", LANDMARK);
            attributeKeyMapping.put("vtc", VILLAGE_TOWN_CITY);
            attributeKeyMapping.put("po", POST_OFFICE);
            attributeKeyMapping.put("dist", DISTRICT);
            attributeKeyMapping.put("state", STATE);
            attributeKeyMapping.put("pc", POSTAL_CODE);
            attributeKeyMapping.put("dob", DATE_OF_YEAR);


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

            if (resultData.containsKey(DATE_OF_YEAR)) {
                resultData.put(DATE_OF_YEAR, DateUtils.getFormatedDate(resultData.get(DATE_OF_YEAR)));
            }
        } catch (Exception e) {
            throw new QrParsingException(e);
        }

        return resultData;
    }


    // To parse byte-encoded data and return as HashMap
    private static HashMap<String, String> parseByteEncodeData(String scanData) throws QrParsingException {
        HashMap<String, String> resultData = new HashMap<>();

        try {

            if (scanData == null || scanData.trim().isEmpty()) {
                Log.e("scan data is null", "scan data is null");
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


            resultData.put(DATE_OF_YEAR, DateUtils.getFormatedDate(resultData.get(DATE_OF_YEAR)));
        } catch (Exception e) {
            Log.i(TAG, "date format exception" + e);
            throw new QrParsingException(e);
        }

        return resultData;
    }

    private static void extractFields(ByteArrayInputStream bin, Map<String, String> resultData) throws QrParsingException {
        try {
            byte[] result = new byte[5000];
            int count;

            count = getNextValue(bin, result);
            resultData.put(NAME, new String(result, 0, count, java.nio.charset.StandardCharsets.ISO_8859_1).trim());

            count = getNextValue(bin, result);
            resultData.put(DATE_OF_YEAR, new String(result, 0, count, java.nio.charset.StandardCharsets.ISO_8859_1).trim());

            count = getNextValue(bin, result);
            resultData.put(GENDER, new String(result, 0, count, java.nio.charset.StandardCharsets.ISO_8859_1).trim());

            count = getNextValue(bin, result);
            resultData.put(FATHER, new String(result, 0, count, java.nio.charset.StandardCharsets.ISO_8859_1).trim());

            count = getNextValue(bin, result);
            resultData.put(DISTRICT, new String(result, 0, count, java.nio.charset.StandardCharsets.ISO_8859_1).trim());

            count = getNextValue(bin, result);
            resultData.put(LANDMARK, new String(result, 0, count, java.nio.charset.StandardCharsets.ISO_8859_1).trim());

            count = getNextValue(bin, result);
            resultData.put(HOUSE_NUMBER, new String(result, 0, count, java.nio.charset.StandardCharsets.ISO_8859_1).trim());

            count = getNextValue(bin, result);
            resultData.put(LOCATION, new String(result, 0, count, java.nio.charset.StandardCharsets.ISO_8859_1).trim());

            count = getNextValue(bin, result);
            resultData.put(PINCODE, new String(result, 0, count, java.nio.charset.StandardCharsets.ISO_8859_1).trim());

            count = getNextValue(bin, result);
            resultData.put(POST_OFFICE, new String(result, 0, count, java.nio.charset.StandardCharsets.ISO_8859_1).trim());

            count = getNextValue(bin, result);
            resultData.put(STATE, new String(result, 0, count, java.nio.charset.StandardCharsets.ISO_8859_1).trim());

            count = getNextValue(bin, result);
            resultData.put(STREET_NAME, new String(result, 0, count, java.nio.charset.StandardCharsets.ISO_8859_1).trim());

            count = getNextValue(bin, result);
            resultData.put(VILLAGE_TOWN_CITY, new String(result, 0, count, java.nio.charset.StandardCharsets.ISO_8859_1).trim());
        } catch (Exception e) {
            throw new QrParsingException(e);
        }
    }

    private static int getNextValue(ByteArrayInputStream bin, byte[] buffer) {
        int c, index = 0;
        while ((c = bin.read()) != -1 && c != TERMINATOR) {
            buffer[index++] = (byte) c;
        }
        return index;
    }

    private static byte[] decompress(byte[] compressedData) throws QrParsingException {
        try (GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(compressedData));
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = gzipInputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            throw new QrParsingException(e);
        }
    }

    private static HashMap<String, String> parseSignatureDataBigQR(String scannedResult) throws QrParsingException {
        HashMap<String, String> resultData = new HashMap<>();

        try {
        HashMap<String, String> attributeKeyMapping = new HashMap<>();
        attributeKeyMapping.put("n", NAME);   // Name
        attributeKeyMapping.put("g", GENDER); // Gender
        attributeKeyMapping.put("d", DATE_OF_YEAR); // Date of Birth
        attributeKeyMapping.put("a", ADDRESS); // Address


            // Clean the input XML
            scannedResult = scannedResult.trim();

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new java.io.StringReader(scannedResult));

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName();
                if (eventType == XmlPullParser.START_TAG && (QPDB_FORMAT_TAG.equals(tagName) || QDA_FORMAT_TAG.equals(tagName) || QPDA_FORMAT_TAG.equals(tagName) || QDB_FORMAT_TAG.equals(tagName))) {
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
            throw new QrParsingException(e);
        }
        return resultData;
    }

}