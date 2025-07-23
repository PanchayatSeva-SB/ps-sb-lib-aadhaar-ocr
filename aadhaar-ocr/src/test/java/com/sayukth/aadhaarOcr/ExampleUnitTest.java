package com.sayukth.aadhaarOcr;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

import com.example.ocrpresenter.MainActivity;
import com.sayukth.aadhaarOcr.Exceptions.PresenterException;
import com.sayukth.aadhaarOcr.ui.DetectAadhaarContract;
import com.sayukth.aadhaarOcr.ui.DetectAadhaarPresenter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 30)
public class ExampleUnitTest {

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
    public void testQRScan_UTOO1() throws IOException, PresenterException {

        String filePath = "src/test/res/qr_scan_content_UT001.txt";
        String scanContent = new String(Files.readAllBytes(Paths.get(filePath)));

        System.out.println(scanContent);

        HashMap<String, String> expectedResult = new HashMap<>();
        expectedResult.put("VTC", "Mandasa");
        expectedResult.put("Post Office", "Suvarnapuram");
        expectedResult.put("Reference ID", "2");
        expectedResult.put("Landmark", "mandasa mandalam");
        expectedResult.put("House", "1-138");
        expectedResult.put("DATE_OF_YEAR", "12-08-2003");
        expectedResult.put("NAME", "Dokkara Vandana");
        expectedResult.put("FATHER", "D/O: Apparao");
        expectedResult.put("State", "Andhra Pradesh");
        expectedResult.put("GENDER", "F");
        expectedResult.put("Street", "peddaveedhi");
        expectedResult.put("Reference ID 1", "425720240130144740290");
        expectedResult.put("Pin Code", "532243");
        expectedResult.put("District", "Srikakulam");
        expectedResult.put("Location", "guddibadra");

        HashMap<String, String> actualResult = presenter.handleQrCodeScan(scanContent);

        assertEquals(expectedResult, actualResult);

    }

    @Test
    public void testQRScan_UTOO2() throws IOException, PresenterException {

        String filePath = "src/test/res/qr_scan_content_UT002.txt";
        String scanContent = new String(Files.readAllBytes(Paths.get(filePath)));

        System.out.println(scanContent);

        HashMap<String, String> expectedResult = new HashMap<>();
        expectedResult.put("StreetName", "CHURCH STREET");
        expectedResult.put("HouseNumber", "1-66");
        expectedResult.put("VillageTownCity", "Velivarru");
        expectedResult.put("Landmark", "UNDI POST");
        expectedResult.put("PostOffice", "Undi");
        expectedResult.put("PostalCode", "534199");
        expectedResult.put("AADHAR", "840796182220");
        expectedResult.put("DATE_OF_YEAR", "01-01-1965");
        expectedResult.put("NAME", "KONALA GEORGE");
        expectedResult.put("FATHER", "KONALA SUNDAR RAO");
        expectedResult.put("State", "Andhra Pradesh");
        expectedResult.put("GENDER", "M");
        expectedResult.put("District", "West Godavari");
        expectedResult.put("DATE_OF_BIRTH", "16/10/1965");


        HashMap<String, String> actualResult = presenter.handleQrCodeScan(scanContent);

        assertEquals(expectedResult, actualResult);

    }
}
