package com.sayukth.aadhaarOcr;


import static org.junit.Assert.assertEquals;

import com.example.ocrpresenter.MainActivity;
import com.sayukth.aadhaarOcr.Exceptions.PresenterException;
import com.sayukth.aadhaarOcr.ui.DetectAadhaarContract;
import com.sayukth.aadhaarOcr.ui.DetectAadhaarPresenter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 30)
public class SmallQrCodeUnitTestCases {

    DetectAadhaarPresenter presenter;
    DetectAadhaarContract.View detectAadhaarView;
    MainActivity activity;

    @Before
    public void setUp() throws IllegalAccessException, InstantiationException, PresenterException {
        detectAadhaarView = MainActivity.class.newInstance();

        presenter = new DetectAadhaarPresenter(detectAadhaarView, MainActivity.class.newInstance());
    }

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testQRScan_UTOOO1() throws IOException, PresenterException {

        String filePath = "src/test/res/small_qr_scan_content_UT0001.txt";
        String scanContent = new String(Files.readAllBytes(Paths.get(filePath)));

        System.out.println(scanContent);

        HashMap<String, String> expectedResult = new HashMap<>();
        expectedResult.put("HOUSE_NUMBER", "2-105");
        expectedResult.put("STATE", "Andhra Pradesh");
        expectedResult.put("AADHAR", "482250205896");
        expectedResult.put("DATE_OF_YEAR", "05-05-1984");
        expectedResult.put("NAME", "PALA SANKAR");
        expectedResult.put("DISTRICT", "West Godavari");
        expectedResult.put("FATHER", "Pala Chandra Rao");
        expectedResult.put("LANDMARK", "VELIVARRU POST");
        expectedResult.put("VILLAGE_TOWN_CITY", "Velivarru");
        expectedResult.put("POST_OFFICE", "Undi");
        expectedResult.put("POSTAL_CODE", "534199");
        expectedResult.put("STREET_NAME", "VELIVARRU GARUVU");
        expectedResult.put("GENDER", "M");

        HashMap<String, String> actualResult = presenter.handleQrCodeScan(scanContent);

        assertEquals(expectedResult, actualResult);

    }

    @Test
    public void testQRScan_UTOOO2() throws IOException, PresenterException {

        String filePath = "src/test/res/small_qr_scan_content_UT0002.txt";
        String scanContent = new String(Files.readAllBytes(Paths.get(filePath)));

        System.out.println(scanContent);

        HashMap<String, String> expectedResult = new HashMap<>();
        expectedResult.put("HOUSE_NUMBER", "2-78");
        expectedResult.put("STATE", "Andhra Pradesh");
        expectedResult.put("AADHAR", "872334534993");
        expectedResult.put("DATE_OF_YEAR", "06-09-1950");
        expectedResult.put("NAME", "KUDUPUDI SATYANARAYANA");
        expectedResult.put("DISTRICT", "West Godavari");
        expectedResult.put("FATHER", "KUDUPUDI PATTABHI");
        expectedResult.put("LANDMARK", "VELIVARRU POST");
        expectedResult.put("VILLAGE_TOWN_CITY", "Velivarru");
        expectedResult.put("POST_OFFICE", "Undi");
        expectedResult.put("POSTAL_CODE", "534199");
        expectedResult.put("STREET_NAME", "VELIVARRU GARUVU");
        expectedResult.put("GENDER", "M");

        HashMap<String, String> actualResult = presenter.handleQrCodeScan(scanContent);

        assertEquals(expectedResult, actualResult);

    }

    @Test
    public void testQRScan_UTOOO3() throws IOException, PresenterException {

        String filePath = "src/test/res/small_qr_scan_content_UT0003.txt";
        String scanContent = new String(Files.readAllBytes(Paths.get(filePath)));

        System.out.println(scanContent);

        HashMap<String, String> expectedResult = new HashMap<>();
        expectedResult.put("HOUSE_NUMBER", "1-60");
        expectedResult.put("STATE", "Andhra Pradesh");
        expectedResult.put("AADHAR", "254099559880");
        expectedResult.put("DATE_OF_YEAR", "19-05-1988");
        expectedResult.put("NAME", "BODDU LAZARU");
        expectedResult.put("DISTRICT", "West Godavari");
        expectedResult.put("FATHER", "BODDU SURYA PRAKASH");
        expectedResult.put("LANDMARK", "UNDI POST");
        expectedResult.put("VILLAGE_TOWN_CITY", "Velivarru");
        expectedResult.put("POST_OFFICE", "Undi");
        expectedResult.put("POSTAL_CODE", "534199");
        expectedResult.put("STREET_NAME", "-");
        expectedResult.put("GENDER", "M");

        HashMap<String, String> actualResult = presenter.handleQrCodeScan(scanContent);

        assertEquals(expectedResult, actualResult);

    }

    @Test
    public void testQRScan_UTOOO4() throws IOException, PresenterException {

        String filePath = "src/test/res/small_qr_scan_content_UT0004.txt";
        String scanContent = new String(Files.readAllBytes(Paths.get(filePath)));

        System.out.println(scanContent);

        HashMap<String, String> expectedResult = new HashMap<>();
        expectedResult.put("HOUSE_NUMBER", "2-7");
        expectedResult.put("STATE", "Andhra Pradesh");
        expectedResult.put("AADHAR", "277538146178");
        expectedResult.put("DATE_OF_YEAR", "16-11-1964");
        expectedResult.put("NAME", "Balam Lakshmi");
        expectedResult.put("DISTRICT", "West Godavari");
        expectedResult.put("FATHER", "TANGELLA NAGARAJU");
        expectedResult.put("LANDMARK", "VELIVARRU POST");
        expectedResult.put("VILLAGE_TOWN_CITY", "Velivarru");
        expectedResult.put("POST_OFFICE", "Undi");
        expectedResult.put("POSTAL_CODE", "534199");
        expectedResult.put("STREET_NAME", "MAIN ROAD");
        expectedResult.put("GENDER", "F");


        HashMap<String, String> actualResult = presenter.handleQrCodeScan(scanContent);

        assertEquals(expectedResult, actualResult);

    }

    @Test
    public void testQRScan_UTOOO5() throws IOException, PresenterException {

        String filePath = "src/test/res/small_qr_scan_content_UT0005.txt";
        String scanContent = new String(Files.readAllBytes(Paths.get(filePath)));

        System.out.println(scanContent);

        HashMap<String, String> expectedResult = new HashMap<>();
        expectedResult.put("HOUSE_NUMBER", "2-7");
        expectedResult.put("STATE", "Andhra Pradesh");
        expectedResult.put("AADHAR", "277538146178");
        expectedResult.put("DATE_OF_YEAR", "16-11-1964");
        expectedResult.put("NAME", "Balam Lakshmi");
        expectedResult.put("DISTRICT", "West Godavari");
        expectedResult.put("FATHER", "TANGELLA NAGARAJU");
        expectedResult.put("LANDMARK", "VELIVARRU POST");
        expectedResult.put("VILLAGE_TOWN_CITY", "Velivarru");
        expectedResult.put("POST_OFFICE", "Undi");
        expectedResult.put("POSTAL_CODE", "534199");
        expectedResult.put("STREET_NAME", "MAIN ROAD");
        expectedResult.put("GENDER", "F");



        HashMap<String, String> actualResult = presenter.handleQrCodeScan(scanContent);

        assertEquals(expectedResult, actualResult);

    }

    @Test
    public void testQRScan_UTOOO6() throws IOException, PresenterException {

        String filePath = "src/test/res/small_qr_scan_content_UT0006.txt";
        String scanContent = new String(Files.readAllBytes(Paths.get(filePath)));

        System.out.println(scanContent);

        HashMap<String, String> expectedResult = new HashMap<>();
        expectedResult.put("HOUSE_NUMBER", "1-99");
        expectedResult.put("STATE", "Andhra Pradesh");
        expectedResult.put("AADHAR", "703943132206");
        expectedResult.put("DATE_OF_YEAR", "10-07-1968");
        expectedResult.put("NAME", "EELI ADAMU");
        expectedResult.put("DISTRICT", "West Godavari");
        expectedResult.put("FATHER", "EALI KRUPA DANAM");
        expectedResult.put("LANDMARK", "UNDI POST");
        expectedResult.put("VILLAGE_TOWN_CITY", "Velivarru");
        expectedResult.put("POST_OFFICE", "Undi");
        expectedResult.put("POSTAL_CODE", "534199");
        expectedResult.put("STREET_NAME", "-");
        expectedResult.put("GENDER", "M");

        HashMap<String, String> actualResult = presenter.handleQrCodeScan(scanContent);

        assertEquals(expectedResult, actualResult);

    }

    @Test
    public void testQRScan_UTOOO7() throws IOException, PresenterException {

        String filePath = "src/test/res/small_qr_scan_content_UT0007.txt";
        String scanContent = new String(Files.readAllBytes(Paths.get(filePath)));

        System.out.println(scanContent);

        HashMap<String, String> expectedResult = new HashMap<>();
        expectedResult.put("HOUSE_NUMBER", "1-99");
        expectedResult.put("STATE", "Andhra Pradesh");
        expectedResult.put("AADHAR", "340207765382");
        expectedResult.put("DATE_OF_YEAR", "07-06-1973");
        expectedResult.put("NAME", "EELI DHANALAKSHMI");
        expectedResult.put("DISTRICT", "West Godavari");
        expectedResult.put("FATHER", "TULASI SURAYYA");
        expectedResult.put("LANDMARK", "UNDI POST");
        expectedResult.put("VILLAGE_TOWN_CITY", "Velivarru");
        expectedResult.put("POST_OFFICE", "Undi");
        expectedResult.put("POSTAL_CODE", "534199");
        expectedResult.put("STREET_NAME", "-");
        expectedResult.put("GENDER", "F");

        HashMap<String, String> actualResult = presenter.handleQrCodeScan(scanContent);

        assertEquals(expectedResult, actualResult);

    }

    @Test
    public void testQRScan_UTOOO8() throws IOException, PresenterException {

        String filePath = "src/test/res/small_qr_scan_content_UT0008.txt";
        String scanContent = new String(Files.readAllBytes(Paths.get(filePath)));

        System.out.println(scanContent);

        HashMap<String, String> expectedResult = new HashMap<>();
        expectedResult.put("HOUSE_NUMBER", "1-66");
        expectedResult.put("STATE", "Andhra Pradesh");
        expectedResult.put("AADHAR", "840796182220");
        expectedResult.put("DATE_OF_YEAR", "16-10-1965");
        expectedResult.put("NAME", "KONALA GEORGE");
        expectedResult.put("DISTRICT", "West Godavari");
        expectedResult.put("FATHER", "KONALA SUNDAR RAO");
        expectedResult.put("LANDMARK", "UNDI POST");
        expectedResult.put("VILLAGE_TOWN_CITY", "Velivarru");
        expectedResult.put("POST_OFFICE", "Undi");
        expectedResult.put("POSTAL_CODE", "534199");
        expectedResult.put("STREET_NAME", "CHURCH STREET");
        expectedResult.put("GENDER", "M");


        HashMap<String, String> actualResult = presenter.handleQrCodeScan(scanContent);

        assertEquals(expectedResult, actualResult);

    }

    @Test
    public void testQRScan_UTOOO9() throws IOException, PresenterException {

        String filePath = "src/test/res/small_qr_scan_content_UT0009.txt";
        String scanContent = new String(Files.readAllBytes(Paths.get(filePath)));

        System.out.println(scanContent);

        HashMap<String, String> expectedResult = new HashMap<>();
        expectedResult.put("HOUSE_NUMBER", "1-66");
        expectedResult.put("STATE", "Andhra Pradesh");
        expectedResult.put("AADHAR", "974283139627");
        expectedResult.put("DATE_OF_YEAR", "01-07-1985");
        expectedResult.put("NAME", "KONALA SUNDARA RAO");
        expectedResult.put("DISTRICT", "West Godavari");
        expectedResult.put("FATHER", "KONALA GEORGE");
        expectedResult.put("LANDMARK", "UNDI POST");
        expectedResult.put("VILLAGE_TOWN_CITY", "Velivarru");
        expectedResult.put("POST_OFFICE", "Undi");
        expectedResult.put("POSTAL_CODE", "534199");
        expectedResult.put("STREET_NAME", "CHURCH STREET");
        expectedResult.put("GENDER", "M");



        HashMap<String, String> actualResult = presenter.handleQrCodeScan(scanContent);

        assertEquals(expectedResult, actualResult);

    }

    @Test
    public void testQRScan_UTOO10() throws IOException, PresenterException {

        String filePath = "src/test/res/small_qr_scan_content_UT0010.txt";
        String scanContent = new String(Files.readAllBytes(Paths.get(filePath)));

        System.out.println(scanContent);

        HashMap<String, String> expectedResult = new HashMap<>();
        expectedResult.put("HOUSE_NUMBER", "1-66");
        expectedResult.put("STATE", "Andhra Pradesh");
        expectedResult.put("AADHAR", "908895691113");
        expectedResult.put("DATE_OF_YEAR", "10-06-1990");
        expectedResult.put("NAME", "KONALA PRASANNA");
        expectedResult.put("DISTRICT", "West Godavari");
        expectedResult.put("FATHER", "G Kutumba Rao");
        expectedResult.put("LANDMARK", "UNDI POST");
        expectedResult.put("VILLAGE_TOWN_CITY", "Velivarru");
        expectedResult.put("POST_OFFICE", "Undi");
        expectedResult.put("POSTAL_CODE", "534199");
        expectedResult.put("STREET_NAME", "CHURCH STREET");
        expectedResult.put("GENDER", "F");



        HashMap<String, String> actualResult = presenter.handleQrCodeScan(scanContent);

        assertEquals(expectedResult, actualResult);

    }

    @Test
    public void testQRScan_UTOO11() throws IOException, PresenterException {

        String filePath = "src/test/res/small_qr_scan_content_UT0011.txt";
        String scanContent = new String(Files.readAllBytes(Paths.get(filePath)));

        System.out.println(scanContent);

        HashMap<String, String> expectedResult = new HashMap<>();
        expectedResult.put("HOUSE_NUMBER", "1-61");
        expectedResult.put("STATE", "Andhra Pradesh");
        expectedResult.put("AADHAR", "854610792834");
        expectedResult.put("DATE_OF_YEAR", "11-10-1957");
        expectedResult.put("NAME", "KONALA PRAKASAM");
        expectedResult.put("DISTRICT", "West Godavari");
        expectedResult.put("FATHER", "KONALA SUNDARA RAO");
        expectedResult.put("LANDMARK", "UNDI POST");
        expectedResult.put("VILLAGE_TOWN_CITY", "Velivarru");
        expectedResult.put("POST_OFFICE", "Undi");
        expectedResult.put("POSTAL_CODE", "534199");
        expectedResult.put("STREET_NAME", "-");
        expectedResult.put("GENDER", "M");

        HashMap<String, String> actualResult = presenter.handleQrCodeScan(scanContent);

        assertEquals(expectedResult, actualResult);

    }

    @Test
    public void testQRScan_UTOO12() throws IOException, PresenterException {

        String filePath = "src/test/res/small_qr_scan_content_UT0012.txt";
        String scanContent = new String(Files.readAllBytes(Paths.get(filePath)));

        System.out.println(scanContent);

        HashMap<String, String> expectedResult = new HashMap<>();
        expectedResult.put("HOUSE_NUMBER", "1-14");
        expectedResult.put("STATE", "Andhra Pradesh");
        expectedResult.put("AADHAR", "761350178792");
        expectedResult.put("DATE_OF_YEAR", "20-04-1988");
        expectedResult.put("NAME", "Kallepalle Durga Bhavani");
        expectedResult.put("DISTRICT", "West Godavari");
        expectedResult.put("FATHER", "Vegesna Venkateswara Raju");
        expectedResult.put("LANDMARK", "UNDI POST");
        expectedResult.put("VILLAGE_TOWN_CITY", "Velivarru");
        expectedResult.put("POST_OFFICE", "Undi");
        expectedResult.put("POSTAL_CODE", "534199");
        expectedResult.put("STREET_NAME", "KALLEPALLI VARI STREET");
        expectedResult.put("GENDER", "F");

        HashMap<String, String> actualResult = presenter.handleQrCodeScan(scanContent);

        assertEquals(expectedResult, actualResult);

    }

    @Test
    public void testQRScan_UTOO13() throws IOException, PresenterException {

        String filePath = "src/test/res/small_qr_scan_content_UT0013.txt";
        String scanContent = new String(Files.readAllBytes(Paths.get(filePath)));

        System.out.println(scanContent);

        HashMap<String, String> expectedResult = new HashMap<>();
        expectedResult.put("HOUSE_NUMBER", "1-48");
        expectedResult.put("STATE", "Andhra Pradesh");
        expectedResult.put("AADHAR", "440111164555");
        expectedResult.put("DATE_OF_YEAR", "08-08-1983");
        expectedResult.put("NAME", "ROKKALA JAKRAIAH");
        expectedResult.put("DISTRICT", "West Godavari");
        expectedResult.put("FATHER", "Rokkala Karnelu");
        expectedResult.put("LANDMARK", "UNDI POST");
        expectedResult.put("VILLAGE_TOWN_CITY", "Velivarru");
        expectedResult.put("POST_OFFICE", "Undi");
        expectedResult.put("POSTAL_CODE", "534199");
        expectedResult.put("STREET_NAME", "-");
        expectedResult.put("GENDER", "M");


        HashMap<String, String> actualResult = presenter.handleQrCodeScan(scanContent);

        assertEquals(expectedResult, actualResult);

    }

    @Test
    public void testQRScan_UTOO14() throws IOException, PresenterException {

        String filePath = "src/test/res/small_qr_scan_content_UT0014.txt";
        String scanContent = new String(Files.readAllBytes(Paths.get(filePath)));

        System.out.println(scanContent);

        HashMap<String, String> expectedResult = new HashMap<>();
        expectedResult.put("HOUSE_NUMBER", "1-58");
        expectedResult.put("STATE", "Andhra Pradesh");
        expectedResult.put("AADHAR", "220858482604");
        expectedResult.put("DATE_OF_YEAR", "06-10-1985");
        expectedResult.put("NAME", "GANTA DEVID RAJU");
        expectedResult.put("DISTRICT", "West Godavari");
        expectedResult.put("FATHER", "GANTA SAMSONU");
        expectedResult.put("LANDMARK", "UNDI POST");
        expectedResult.put("VILLAGE_TOWN_CITY", "Velivarru");
        expectedResult.put("POST_OFFICE", "Undi");
        expectedResult.put("POSTAL_CODE", "534199");
        expectedResult.put("STREET_NAME", "-");
        expectedResult.put("GENDER", "M");


        HashMap<String, String> actualResult = presenter.handleQrCodeScan(scanContent);

        assertEquals(expectedResult, actualResult);

    }

    @Test
    public void testQRScan_UTOO15() throws IOException, PresenterException {

        String filePath = "src/test/res/small_qr_scan_content_UT0015.txt";
        String scanContent = new String(Files.readAllBytes(Paths.get(filePath)));

        System.out.println(scanContent);

        HashMap<String, String> expectedResult = new HashMap<>();
        expectedResult.put("HOUSE_NUMBER", "1-96");
        expectedResult.put("STATE", "Andhra Pradesh");
        expectedResult.put("AADHAR", "895626582540");
        expectedResult.put("DATE_OF_YEAR", "09-11-1990");
        expectedResult.put("NAME", "PILLI SAROJINI");
        expectedResult.put("DISTRICT", "West Godavari");
        expectedResult.put("FATHER", "Mangabathula China Marthin");
        expectedResult.put("LANDMARK", "UNDI POST");
        expectedResult.put("VILLAGE_TOWN_CITY", "Velivarru");
        expectedResult.put("POST_OFFICE", "Undi");
        expectedResult.put("POSTAL_CODE", "534199");
        expectedResult.put("STREET_NAME", "-");
        expectedResult.put("GENDER", "F");



        HashMap<String, String> actualResult = presenter.handleQrCodeScan(scanContent);

        assertEquals(expectedResult, actualResult);

    }

    @Test
    public void testQRScan_UTOO16() throws IOException, PresenterException {

        String filePath = "src/test/res/small_qr_scan_content_UT0016.txt";
        String scanContent = new String(Files.readAllBytes(Paths.get(filePath)));

        System.out.println(scanContent);

        HashMap<String, String> expectedResult = new HashMap<>();
        expectedResult.put("HOUSE_NUMBER", "2-24");
        expectedResult.put("STATE", "Andhra Pradesh");
        expectedResult.put("AADHAR", "819845570495");
        expectedResult.put("DATE_OF_YEAR", "22-10-1959");
        expectedResult.put("NAME", "RUDDARRAJU VIJAYA LAKSHMI");
        expectedResult.put("DISTRICT", "West Godavari");
        expectedResult.put("FATHER", "VEGESNA SUBBA RAJU");
        expectedResult.put("LANDMARK", "VELIVARRU POST");
        expectedResult.put("VILLAGE_TOWN_CITY", "Velivarru");
        expectedResult.put("POST_OFFICE", "Undi");
        expectedResult.put("POSTAL_CODE", "534199");
        expectedResult.put("STREET_NAME", "OPPOSITE RAMALAYAM");
        expectedResult.put("GENDER", "F");

        HashMap<String, String> actualResult = presenter.handleQrCodeScan(scanContent);

        assertEquals(expectedResult, actualResult);

    }

    @Test
    public void testQRScan_UTOO17() throws IOException, PresenterException {

        String filePath = "src/test/res/small_qr_scan_content_UT0017.txt";
        String scanContent = new String(Files.readAllBytes(Paths.get(filePath)));

        System.out.println(scanContent);

        HashMap<String, String> expectedResult = new HashMap<>();
        expectedResult.put("HOUSE_NUMBER", "14-138/2");
        expectedResult.put("DISTRICT", "East Godavari");
        expectedResult.put("FATHER", "Govind");
        expectedResult.put("VILLAGE_TOWN_CITY", "Rajahmundry (Urban)");
        expectedResult.put("POST_OFFICE", "Danavaipeta");
        expectedResult.put("POSTAL_CODE", "533103");
        expectedResult.put("STREET_NAME", "SANTHIPURAM");
        expectedResult.put("GENDER", "F");
        expectedResult.put("STATE", "Andhra Pradesh");
        expectedResult.put("AADHAR", "300018377489");
        expectedResult.put("DATE_OF_YEAR", "01-01-1984");
        expectedResult.put("NAME", "Gudipalli Riya");


        HashMap<String, String> actualResult = presenter.handleQrCodeScan(scanContent);

        assertEquals(expectedResult, actualResult);

    }

    @Test
    public void testQRScan_UTOO18() throws IOException, PresenterException {

        String filePath = "src/test/res/small_qr_scan_content_UT0018.txt";
        String scanContent = new String(Files.readAllBytes(Paths.get(filePath)));

        System.out.println(scanContent);

        HashMap<String, String> expectedResult = new HashMap<>();
        expectedResult.put("HOUSE_NUMBER", "14-138/2");
        expectedResult.put("DISTRICT", "East Godavari");
        expectedResult.put("FATHER", "Mahendra");
        expectedResult.put("VILLAGE_TOWN_CITY", "Rajahmundry (Urban)");
        expectedResult.put("POST_OFFICE", "Danavaipeta");
        expectedResult.put("POSTAL_CODE", "533103");
        expectedResult.put("STREET_NAME", "SANTHIPURAM");
        expectedResult.put("GENDER", "F");
        expectedResult.put("STATE", "Andhra Pradesh");
        expectedResult.put("AADHAR", "300002141504");
        expectedResult.put("DATE_OF_YEAR", "01-01-1985");
        expectedResult.put("NAME", "Mudhe Kajal");



        HashMap<String, String> actualResult = presenter.handleQrCodeScan(scanContent);

        assertEquals(expectedResult, actualResult);

    }

    @Test
    public void testQRScan_UTOO19() throws IOException, PresenterException {

        String filePath = "src/test/res/small_qr_scan_content_UT0019.txt";
        String scanContent = new String(Files.readAllBytes(Paths.get(filePath)));

        System.out.println(scanContent);

        HashMap<String, String> expectedResult = new HashMap<>();
        expectedResult.put("HOUSE_NUMBER", "14-138/2");
        expectedResult.put("DISTRICT", "East Godavari");
        expectedResult.put("FATHER", "Father Gopi");
        expectedResult.put("VILLAGE_TOWN_CITY", "Rajahmundry (Urban)");
        expectedResult.put("POST_OFFICE", "Danavaipeta");
        expectedResult.put("POSTAL_CODE", "533103");
        expectedResult.put("STREET_NAME", "SANTHIPURAM");
        expectedResult.put("GENDER", "M");
        expectedResult.put("STATE", "Andhra Pradesh");
        expectedResult.put("AADHAR", "300007536729");
        expectedResult.put("DATE_OF_YEAR", "01-01-1985");
        expectedResult.put("NAME", "Kaveri Gopi");

        HashMap<String, String> actualResult = presenter.handleQrCodeScan(scanContent);

        assertEquals(expectedResult, actualResult);

    }




}
