package com.sayukth.aadhaarOcr.ui;


import static android.content.ContentValues.TAG;
import static com.sayukth.aadhaarOcr.constants.AadhaarOcrConstants.GENERIC_EXCEPTION_MSSG;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.common.util.concurrent.ListenableFuture;
import com.sayukth.aadhaarOcr.R;
import com.sayukth.aadhaarOcr.ocrpreferences.AadhaarOcrPreferences;
import com.sayukth.aadhaarOcr.utils.DateUtils;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class CustomCameraLaunchActivity extends AppCompatActivity {

    private PreviewView cameraPreview;
    private Button capturePhotoButton;

    private ImageCapture imageCapture;
    private Camera camera;
    private ExecutorService cameraExecutor;
    private ImageView frontBackGif;
    private TextView ocrTextView;// This is also fine.
    View promptView;
    private ProgressBar progressBar;
    ImageView gifImageView;
    View overlay;
    TextView flipTextView;
    ImageView distanceImage;
    TextView textViewBigQR;

    private ScaleGestureDetector scaleGestureDetector;
    private float currentZoomRatio = 1f;
    private static final String JPG = ".jpg";

    private static final String CAMERA = "camera";

    private static final String PHOTO = "photo-";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        promptView = LayoutInflater.from(this).inflate(R.layout.activity_custom_camera_launch, null);
        setContentView(promptView);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        try {
            initializeViews();
            initializeCameraExecutor();
            handlePermissions();
            handleScanType();
            handleFlipGif();
            handleBigQrOcr();
            handleSignatureDataBigQrOcr();

            capturePhotoButton.setOnClickListener(v -> capturePhoto());

            scaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                @Override
                public boolean onScale(ScaleGestureDetector detector) {
                    if (camera != null) {
                        currentZoomRatio *= detector.getScaleFactor();

                        // Clamp zoom ratio between min and max
                        float minZoom = camera.getCameraInfo().getZoomState().getValue().getMinZoomRatio();
                        float maxZoom = camera.getCameraInfo().getZoomState().getValue().getMaxZoomRatio();

                        currentZoomRatio = Math.max(minZoom, Math.min(currentZoomRatio, maxZoom));
                        camera.getCameraControl().setZoomRatio(currentZoomRatio);
                    }
                    return true;
                }
            });

            cameraPreview.setOnTouchListener((v, event) -> {
                scaleGestureDetector.onTouchEvent(event);
                return true;
            });

        } catch (Exception e) {
            Log.e(TAG, e.getMessage() != null ? e.getMessage() : GENERIC_EXCEPTION_MSSG, e);
        }
    }

    /**
     * Initializes all the views used in the activity.
     */
    private void initializeViews() {
        cameraPreview = findViewById(R.id.camera_preview);
        capturePhotoButton = findViewById(R.id.capture_photo);
        frontBackGif = findViewById(com.sayukth.aadhaarOcr.R.id.front_back_gif);
        ocrTextView = findViewById(com.sayukth.aadhaarOcr.R.id.ocr_text);
        progressBar = findViewById(R.id.progressBar);
        gifImageView = findViewById(R.id.gifImageView);
        overlay = findViewById(R.id.overlay);
        flipTextView = findViewById(R.id.flip_text);
        distanceImage = findViewById(R.id.distance_image);
        textViewBigQR = findViewById(R.id.textViewBigQR);
    }

    /**
     * Initializes the camera executor to handle camera-related tasks in a separate thread.
     */
    private void initializeCameraExecutor() {
        cameraExecutor = Executors.newSingleThreadExecutor();
    }

    /**
     * Checks for camera permissions and requests them if not granted.
     * If permissions are already granted, the camera is started.
     */
    private void handlePermissions() {
        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 101);
        }
    }

    /**
     * Handles the scan type received from the intent and updates the UI accordingly.
     */
    private void handleScanType() {
        String scanType = getIntent().getStringExtra(getString(R.string.scan_type));

        if (getString(R.string.front_side).equals(scanType)) {
            Glide.with(this).asGif().load(R.drawable.aadhar_front_scan).into(frontBackGif);
        } else if (getString(R.string.back_side).equals(scanType)) {
            Glide.with(this).asGif().load(R.drawable.aadhar_back).into(frontBackGif);
            ocrTextView.setText(getString(R.string.back_side_focus_request));
        } else if (getString(R.string.big_qr_ocr).equals(scanType)) {
            Glide.with(this).asGif().load(R.drawable.aadhar_num_scan).into(frontBackGif);
            ocrTextView.setText(getString(R.string.aadhaar_number_focus_request));
        }
    }

    /**
     * Handles the case where the flip GIF should be displayed before scanning.
     * Temporarily hides other UI components and displays the GIF.
     */
    private void handleFlipGif() {
        if (AadhaarOcrPreferences.getInstance().getBoolean(AadhaarOcrPreferences.Key.IS_FLIP_GIF_SHOW)) {
            AadhaarOcrPreferences.getInstance().put(AadhaarOcrPreferences.Key.IS_FLIP_GIF_SHOW, false);

            toggleViewsForGif();
            Glide.with(this).asGif().load(R.drawable.fip_aadhar_1).into(gifImageView);

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                resetViewsAfterGif();
                startCamera();
            }, 3000);
        }
    }

    /**
     * Handles the case where a big QR OCR process has been detected.
     * Displays a loading GIF and message before allowing the user to capture the Aadhaar number.
     */
    private void handleBigQrOcr() {
        if (AadhaarOcrPreferences.getInstance().getBoolean(AadhaarOcrPreferences.Key.IS_BIG_QR_OCR)) {
            toggleViewsForGif();
            Glide.with(this).asGif().load(R.drawable.aadhar_num_scan).into(gifImageView);
            flipTextView.setText(getString(R.string.big_qr_ocr_capture_text));

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                resetViewsAfterGif();
                textViewBigQR.setVisibility(View.GONE);
                startCamera();
            }, 3000);
        }
    }

    /**
     * Handles the case where a signature QR code has been detected.
     * Displays a loading GIF and message before allowing the user to proceed.
     */
    private void handleSignatureDataBigQrOcr() {
        if (AadhaarOcrPreferences.getInstance().getBoolean(AadhaarOcrPreferences.Key.IS_SIGNATURE_DATA_BIG_QR_OCR)) {
            toggleViewsForGif();
            Glide.with(this).asGif().load(R.drawable.aadhar_back).into(gifImageView);
            flipTextView.setText(getString(R.string.signature_qr_data_captured));

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                resetViewsAfterGif();
                textViewBigQR.setVisibility(View.GONE);
                startCamera();
            }, 3000);
        }
    }

    /**
     * Hides all UI elements except for the GIF and flip text view when displaying a loading animation.
     */
    private void toggleViewsForGif() {
        gifImageView.setVisibility(View.VISIBLE);
        flipTextView.setVisibility(View.VISIBLE);
        cameraPreview.setVisibility(View.GONE);
        capturePhotoButton.setVisibility(View.GONE);
        frontBackGif.setVisibility(View.GONE);
        overlay.setVisibility(View.GONE);
        ocrTextView.setVisibility(View.GONE);
        distanceImage.setVisibility(View.GONE);
    }

    /**
     * Restores the UI elements after the GIF has finished playing.
     */
    private void resetViewsAfterGif() {
        gifImageView.setVisibility(View.GONE);
        flipTextView.setVisibility(View.GONE);
        cameraPreview.setVisibility(View.VISIBLE);
        capturePhotoButton.setVisibility(View.VISIBLE);
        frontBackGif.setVisibility(View.VISIBLE);
        overlay.setVisibility(View.VISIBLE);
        ocrTextView.setVisibility(View.VISIBLE);
        distanceImage.setVisibility(View.VISIBLE);
    }


    //    Starts the Camera for capture
    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        try {

            cameraProviderFuture.addListener(() -> {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                    // Unbind all use cases before rebinding
                    cameraProvider.unbindAll();

                    // Set up the preview
                    Preview preview = new Preview.Builder().build();
                    preview.setSurfaceProvider(cameraPreview.getSurfaceProvider());

                    // Set up the image capture
                    imageCapture = new ImageCapture.Builder().build();

                    // Select back camera as default
                    CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                    // Bind to lifecycle
                    camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);

                    // Set initial zoom level (e.g., 2x zoom)
                    float zoomRatio = 2.0f; // Adjust as needed
                    camera.getCameraControl().setZoomRatio(zoomRatio);

                } catch (Exception e) {
                    Toast.makeText(this, getString(R.string.failed_to_start_camera) + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }, ContextCompat.getMainExecutor(this));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage() != null ? e.getMessage() : GENERIC_EXCEPTION_MSSG, e);
        }
    }

    /**
     * Captures a photo.
     */
    private void capturePhoto() {
        if (imageCapture == null) return;

        try {

            runOnUiThread(() -> {
//            progressBar.setVisibility(View.VISIBLE);
                DateUtils.showLoading(CustomCameraLaunchActivity.this);
                capturePhotoButton.setVisibility(View.GONE);
            });

            // Get the cache directory and create the 'camera' subfolder if it doesn't exist
            File cacheDir = new File(getCacheDir(), CAMERA);
            if (!cacheDir.exists() && !cacheDir.mkdirs()) {
                return;
            }

            // Create a unique file name for the photo
            File photoFile = new File(cacheDir, PHOTO + System.currentTimeMillis() + JPG);

            ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();

            // Capture the photo and save it in the 'camera' folder
            imageCapture.takePicture(outputFileOptions, cameraExecutor, new ImageCapture.OnImageSavedCallback() {
                @Override
                public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                    runOnUiThread(() -> {
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra(getString(R.string.path), photoFile.getAbsolutePath());
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    });
                }

                @Override
                public void onError(@NonNull ImageCaptureException exception) {
                    runOnUiThread(() -> Toast.makeText(CustomCameraLaunchActivity.this, getString(R.string.photo_catured_failed) + exception.getMessage(), Toast.LENGTH_SHORT).show());
                }
            });
        } catch (Exception e) {
            Log.e(TAG, e.getMessage() != null ? e.getMessage() : GENERIC_EXCEPTION_MSSG, e);

        }
    }


    private boolean allPermissionsGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
        // Dismiss dialog if activity is closing
        DateUtils.hideLoading();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // Perform any necessary actions before exiting
            onBackPressed(); // Close the activity
            return true; // Indicate that the event has been handled
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}

