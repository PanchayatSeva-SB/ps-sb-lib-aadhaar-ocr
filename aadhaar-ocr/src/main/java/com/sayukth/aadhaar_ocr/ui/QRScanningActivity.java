package com.sayukth.aadhaar_ocr.ui;

import static com.sayukth.aadhaar_ocr.constants.AadhaarOcrConstants.RESULT_TIMEOUT;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.Result;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.sayukth.aadhaar_ocr.R;

public class QRScanningActivity extends AppCompatActivity {

    private static CaptureManager captureManager; // Manages scanning functionality
    private static DecoratedBarcodeView barcodeScannerView;

    // Timeout related variables
    private static final int SCAN_TIMEOUT = 10000; // Timeout in milliseconds (3 seconds)
    private static Handler timeoutHandler; // Handler for timeout
    private static Runnable timeoutRunnable; // Runnable to execute timeout logic
    View promptView;
    CountDownTimer countDownTimer;
    private static final int TIMER_DURATION = 15000;
    TextView timerTextView;
    ImageView gifImage;
    ImageView torchToggle;
    private static final String SCANNED_AADHAAR = "SCANNED_AADHAAR";
    private static final String CONST_ZERO = "0";
    private boolean isTorchOn = false; // Track torch state

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the custom layout and set it as the content view
        promptView = LayoutInflater.from(this).inflate(R.layout.activity_qr_scan, null);
        setContentView(promptView);

        // Initialize UI components
        timerTextView = promptView.findViewById(R.id.timerTextView);
        torchToggle = promptView.findViewById(R.id.torchToggle);

       // Launch the custom QR scanner UI
        launchScannerCustomUi(this);

        // Load and display a GIF animation above the scanner
        gifImage = promptView.findViewById(R.id.gifAbove);
        Glide.with(this).asGif().load(R.drawable.aadhar_qr_scan_v1).into(gifImage);

        startCountDownTimer();

        setupTorchToggle();
    }

    /**
     * Initializes and launches the barcode scanner UI with a custom layout.
     * @param activity The current activity instance.
     */

    public void launchScannerCustomUi(Activity activity) {


        // Initialize the barcode scanner view
        barcodeScannerView = promptView.findViewById(R.id.barcode_scanner);

        // Initialize CaptureManager to handle scanning functionality
        captureManager = new CaptureManager(activity, barcodeScannerView);

        // Initialize CaptureManager with the Bundle extracted from the Intent
        Bundle bundle = activity.getIntent().getExtras(); // Extract Bundle from Intent
        captureManager.initializeFromIntent(activity.getIntent(), bundle);

        // Start decoding the barcode
        captureManager.decode();

        // Set up result handler for barcode scanning (decodeSingle)
        barcodeScannerView.decodeSingle(new BarcodeCallback() {
            public void barcodeResult(Result result) {
                handleScanResult(result); // Handle the scanned result
            }

            @Override
            public void barcodeResult(BarcodeResult result) {
                handleScanResult(result.getResult());
            }

            @Override
            public void possibleResultPoints(java.util.List<com.google.zxing.ResultPoint> resultPoints) {
                // Optional: Handle possible result points (for more advanced use cases)
            }
        });


        // Initialize timeout handler
        timeoutHandler = new Handler();
        timeoutRunnable = new Runnable() {
            @Override
            public void run() {
                // Timeout occurred, stop the scanner and notify the user
                Toast.makeText(activity, "Scanner Timeout", Toast.LENGTH_SHORT).show();

                // Create an intent to return the timeout result code
                Intent data = new Intent();
                activity.setResult(RESULT_TIMEOUT, data);

                activity.finish();
            }
        };
    }

    /**
     * Handles the result of a successful QR scan.
     * @param result The scanned QR code result.
     */
    private void handleScanResult(Result result) {

        // Remove timeout if scanning is successful
        timeoutHandler.removeCallbacks(timeoutRunnable);

        if (result != null) {
            // Get the scanned data from the QR code
            String scannedData = result.getText();

            Log.e("Scanned Data", "Data: " + scannedData);

            // Create an intent to return the scanned data
            Intent data = new Intent();
            data.putExtra(SCANNED_AADHAAR, scannedData);

            // Set the result and finish the activity
            setResult(RESULT_OK, data);
            finish(); // Close the activity after scanning
        } else {
            Toast.makeText(this, "No data scanned", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (captureManager != null) {
            captureManager.onResume();
        }

        // Post the timeout runnable
        timeoutHandler.postDelayed(timeoutRunnable, TIMER_DURATION);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (captureManager != null) {
            captureManager.onPause();
        }

        // Cancel timeout handler when activity is paused
        timeoutHandler.removeCallbacks(timeoutRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (captureManager != null) {
            captureManager.onDestroy();
        }

        // Cancel timeout handler when activity is destroyed
        timeoutHandler.removeCallbacks(timeoutRunnable);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (captureManager != null) {
            captureManager.onDestroy();
        }
    }

    /**
     * Starts a countdown timer for the scanning process.
     * Displays the remaining time on screen and resets when completed.
     */
    private void startCountDownTimer() {
        countDownTimer = new CountDownTimer(TIMER_DURATION, 1000) { // Countdown interval of 1 second
            @Override
            public void onTick(long millisUntilFinished) {
                // Update the timer text with the remaining seconds
                timerTextView.setVisibility(View.VISIBLE);
                int secondsRemaining = (int) (millisUntilFinished / 1000);
                timerTextView.setText(String.valueOf(secondsRemaining));
            }

            @Override
            public void onFinish() {
                // Timer has finished, you can handle timeout logic here if needed
                timerTextView.setText(CONST_ZERO);
            }
        };

        // Start the countdown timer
        countDownTimer.start();
    }

    /**
     * Configures the flashlight toggle button.
     * Checks if the device has a flash and toggles the flashlight on/off.
     */
    private void setupTorchToggle() {
        boolean hasFlash = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        if (hasFlash) {
            torchToggle.setVisibility(View.VISIBLE);
        } else {
            torchToggle.setVisibility(View.GONE);
            return;
        }

        torchToggle.setOnClickListener(v -> {
            if (isTorchOn) {
                barcodeScannerView.setTorchOff();
                torchToggle.setImageResource(R.drawable.ic_flashlight_off_24);
            } else {
                barcodeScannerView.setTorchOn();
                torchToggle.setImageResource(R.drawable.ic_flashlight_on_24);
            }
            isTorchOn = !isTorchOn;
        });
    }

}

