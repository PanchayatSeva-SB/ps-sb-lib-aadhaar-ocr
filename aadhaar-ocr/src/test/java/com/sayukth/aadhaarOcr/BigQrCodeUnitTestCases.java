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
public class BigQrCodeUnitTestCases {

    DetectAadhaarPresenter presenter;
    DetectAadhaarContract.View detectAadhaarView;
    MainActivity activity;

    @Before
    public void setUp() throws IllegalAccessException, InstantiationException, PresenterException {
        detectAadhaarView = MainActivity.class.newInstance();

        presenter = new DetectAadhaarPresenter(detectAadhaarView, MainActivity.class.newInstance());
    }

    @Test
    public void testQRScan_UTOO20() throws IOException, PresenterException {

        String filePath = "src/test/res/big_qr_scan_content_UT0020.txt";
        String scanContent = new String(Files.readAllBytes(Paths.get(filePath)));

        System.out.println(scanContent);

        HashMap<String, String> expectedResult = new HashMap<>();
        expectedResult.put("ADDRESS", "2-92,GARUVU NEAR CHERUVU,UNDI MANDALAM,VELIVARRU,Velivarru,West Godavari,Andhra Pradesh,534199");
        expectedResult.put("GENDER", "F");
        expectedResult.put("DATE_OF_YEAR", "19-03-1999");
        expectedResult.put("NAME", "Petchetti Durga Bhavani");


        HashMap<String, String> actualResult = presenter.handleQrCodeScan(scanContent);

        assertEquals(expectedResult, actualResult);

    }

    @Test
    public void testQRScan_UTOO21() throws IOException, PresenterException {

        String filePath = "src/test/res/big_qr_scan_content_UT0021.txt";
        String scanContent = new String(Files.readAllBytes(Paths.get(filePath)));

        System.out.println(scanContent);

        HashMap<String, String> expectedResult = new HashMap<>();
        expectedResult.put("ADDRESS", "1-89,MAIN ROAD,UNDI MANDALAM,S C COLONY,Velivarru,West Godavari,Andhra Pradesh,534199");
        expectedResult.put("GENDER", "M");
        expectedResult.put("DATE_OF_YEAR", "05-07-1983");
        expectedResult.put("NAME", "Eeli Ajay Ravi Sudheer");



        HashMap<String, String> actualResult = presenter.handleQrCodeScan(scanContent);

        assertEquals(expectedResult, actualResult);

    }

    @Test
    public void testQRScan_UTOO22() throws IOException, PresenterException {

        String filePath = "src/test/res/big_qr_scan_content_UT0022.txt";
        String scanContent = new String(Files.readAllBytes(Paths.get(filePath)));

        System.out.println(scanContent);

        HashMap<String, String> expectedResult = new HashMap<>();
        expectedResult.put("ADDRESS", "1-88,Main Road,UNDI MANDALAM,S C Colony,Velivarru,West Godavari,Andhra Pradesh,534199");
        expectedResult.put("GENDER", "M");
        expectedResult.put("DATE_OF_YEAR", "01-01-1981");
        expectedResult.put("NAME", "KORUKOLLU YESURATNAM");


        HashMap<String, String> actualResult = presenter.handleQrCodeScan(scanContent);

        assertEquals(expectedResult, actualResult);

    }

    @Test
    public void testQRScan_UTOO23() throws IOException, PresenterException {

        String filePath = "src/test/res/big_qr_scan_content_UT0023.txt";
        String scanContent = new String(Files.readAllBytes(Paths.get(filePath)));

        System.out.println(scanContent);

        HashMap<String, String> expectedResult = new HashMap<>();
        expectedResult.put("ADDRESS", "1-73,KOTHA PETA,VELIVARRU UDNI MANDALAM,NEAR CHURCH,Velivarru,West Godavari,Andhra Pradesh,534199");
        expectedResult.put("GENDER", "M");
        expectedResult.put("DATE_OF_YEAR", "10-05-1992");
        expectedResult.put("NAME", "Yelamadurthi Sekhar");


        HashMap<String, String> actualResult = presenter.handleQrCodeScan(scanContent);

        assertEquals(expectedResult, actualResult);

    }

    @Test
    public void testQRScan_UTOO24() throws IOException, PresenterException {

        String filePath = "src/test/res/big_qr_scan_content_UT0024.txt";
        String scanContent = new String(Files.readAllBytes(Paths.get(filePath)));

        System.out.println(scanContent);

        HashMap<String, String> expectedResult = new HashMap<>();
        expectedResult.put("ADDRESS", "1-61,KOTHA PETA,VELIVARRU UNDI MANDALAM,NEAR CHURCH,Velivarru,West Godavari,Andhra Pradesh,534199");
        expectedResult.put("GENDER", "F");
        expectedResult.put("DATE_OF_YEAR", "22-11-2017");
        expectedResult.put("NAME", "Konala Athira Angel");


        HashMap<String, String> actualResult = presenter.handleQrCodeScan(scanContent);

        assertEquals(expectedResult, actualResult);

    }

    @Test
    public void testQRScan_UTOO25() throws IOException, PresenterException {

        String filePath = "src/test/res/big_qr_scan_content_UT0025.txt";
        String scanContent = new String(Files.readAllBytes(Paths.get(filePath)));

        System.out.println(scanContent);

        HashMap<String, String> expectedResult = new HashMap<>();
        expectedResult.put("ADDRESS", "1-58,CHURCH ROAD,VELIVARRU UNDI MANDALAM,NEAR CHURCH,Velivarru,West Godavari,Andhra Pradesh,534199");
        expectedResult.put("GENDER", "M");
        expectedResult.put("DATE_OF_YEAR", "20-11-1954");
        expectedResult.put("NAME", "Eeli Daniel");



        HashMap<String, String> actualResult = presenter.handleQrCodeScan(scanContent);

        assertEquals(expectedResult, actualResult);

    }

    @Test
    public void testQRScan_UTOO26() throws IOException, PresenterException {

        String filePath = "src/test/res/big_qr_scan_content_UT0026.txt";
        String scanContent = new String(Files.readAllBytes(Paths.get(filePath)));

        System.out.println(scanContent);

        HashMap<String, String> expectedResult = new HashMap<>();
        expectedResult.put("ADDRESS", "1-58,KOTHAPETA,UNDI MANDALAM,VELIVARRU,Velivarru,West Godavari,Andhra Pradesh,534199");
        expectedResult.put("GENDER", "M");
        expectedResult.put("DATE_OF_YEAR", "19-11-1982");
        expectedResult.put("NAME", "ELI MOHAN SUBHASH VIKRAM");

        HashMap<String, String> actualResult = presenter.handleQrCodeScan(scanContent);

        assertEquals(expectedResult, actualResult);

    }

    @Test
    public void testQRScan_UTOO27() throws IOException, PresenterException {

        String filePath = "src/test/res/big_qr_scan_content_UT0027.txt";
        String scanContent = new String(Files.readAllBytes(Paths.get(filePath)));

        System.out.println(scanContent);

        HashMap<String, String> expectedResult = new HashMap<>();
        expectedResult.put("ADDRESS", "H N-1-45/2,Rajula Peta,Undi Mandalam,Panchaythi Near,Velivarru,West Godavari,Andhra Pradesh,534199");
        expectedResult.put("GENDER", "M");
        expectedResult.put("DATE_OF_YEAR", "06-06-1969");
        expectedResult.put("NAME", "Mudunuri Venkatapathi Raju");


        HashMap<String, String> actualResult = presenter.handleQrCodeScan(scanContent);

        assertEquals(expectedResult, actualResult);

    }

    @Test
    public void testQRScan_UTOO28() throws IOException, PresenterException {

        String filePath = "src/test/res/big_qr_scan_content_UT0028.txt";
        String scanContent = new String(Files.readAllBytes(Paths.get(filePath)));

        System.out.println(scanContent);

        HashMap<String, String> expectedResult = new HashMap<>();
        expectedResult.put("ADDRESS", "h n-1-45/2,undi mandalam,Velivarru,West Godavari,Andhra Pradesh,534199");
        expectedResult.put("GENDER", "F");
        expectedResult.put("DATE_OF_YEAR", "01-01-1974");
        expectedResult.put("NAME", "Mudunuri Tara Krishnaveni");


        HashMap<String, String> actualResult = presenter.handleQrCodeScan(scanContent);

        assertEquals(expectedResult, actualResult);

    }

    @Test
    public void testQRScan_UTOO29() throws IOException, PresenterException {

        String filePath = "src/test/res/big_qr_scan_content_UT0029.txt";
        String scanContent = new String(Files.readAllBytes(Paths.get(filePath)));

        System.out.println(scanContent);

        HashMap<String, String> expectedResult = new HashMap<>();
        expectedResult.put("ADDRESS", "1-126/2,ramalayam Street,undi mandalam,Velivarru,West Godavari,Andhra Pradesh,534199");
        expectedResult.put("GENDER", "M");
        expectedResult.put("DATE_OF_YEAR", "15-08-1972");
        expectedResult.put("NAME", "Mudunuri Kondaraju");

        HashMap<String, String> actualResult = presenter.handleQrCodeScan(scanContent);

        assertEquals(expectedResult, actualResult);

    }

    @Test
    public void testQRScan_UTOO30() throws IOException, PresenterException {

        String filePath = "src/test/res/big_qr_scan_content_UT0030.txt";
        String scanContent = new String(Files.readAllBytes(Paths.get(filePath)));

        System.out.println(scanContent);

        HashMap<String, String> expectedResult = new HashMap<>();
        expectedResult.put("ADDRESS", "1-27,MUDUNURI VARI VEEDI,UNDI MANDALAM,NEAR KOTHA CHARAVU,Velivarru,West Godavari,Andhra Pradesh,534199");
        expectedResult.put("GENDER", "M");
        expectedResult.put("DATE_OF_YEAR", "12-07-1948");
        expectedResult.put("NAME", "Mudunuri China Satyanarayana Raju");


        HashMap<String, String> actualResult = presenter.handleQrCodeScan(scanContent);

        assertEquals(expectedResult, actualResult);

    }

    @Test
    public void testQRScan_UTOO31() throws IOException, PresenterException {

        String filePath = "src/test/res/big_qr_scan_content_UT0031.txt";
        String scanContent = new String(Files.readAllBytes(Paths.get(filePath)));

        System.out.println(scanContent);

        HashMap<String, String> expectedResult = new HashMap<>();
        expectedResult.put("ADDRESS", "1-58,KOTHAPETA,UNDI MANDALAM,VELIVARRU,Velivarru,West Godavari,Andhra Pradesh,534199");
        expectedResult.put("GENDER", "F");
        expectedResult.put("DATE_OF_YEAR", "16-05-1988");
        expectedResult.put("NAME", "ELI SWETHA");


        HashMap<String, String> actualResult = presenter.handleQrCodeScan(scanContent);

        assertEquals(expectedResult, actualResult);

    }

    @Test
    public void testQRScan_UTOO32() throws IOException, PresenterException {

        String filePath = "src/test/res/big_qr_scan_content_UT0032.txt";
        String scanContent = new String(Files.readAllBytes(Paths.get(filePath)));

        System.out.println(scanContent);

        HashMap<String, String> expectedResult = new HashMap<>();
        expectedResult.put("ADDRESS", "1-87,KOTHA PETA,UNDI MANDALAM,VELIVARRU,Velivarru,West Godavari,Andhra Pradesh,534199");
        expectedResult.put("GENDER", "F");
        expectedResult.put("DATE_OF_YEAR", "22-06-1990");
        expectedResult.put("NAME", "Merigi Amala Rani");

        HashMap<String, String> actualResult = presenter.handleQrCodeScan(scanContent);

        assertEquals(expectedResult, actualResult);

    }
}
