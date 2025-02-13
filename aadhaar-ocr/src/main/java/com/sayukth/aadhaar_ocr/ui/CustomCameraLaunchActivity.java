package com.sayukth.aadhaar_ocr.ui;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.sayukth.aadhaar_ocr.R;
import com.sayukth.aadhaar_ocr.ocrpreferences.AadhaarOcrPreferences;
import com.sayukth.aadhaar_ocr.utils.DateUtils;

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

    private static final String JPG = ".jpg";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        promptView = LayoutInflater.from(this).inflate(R.layout.activity_custom_camera_launch, null);
        setContentView(promptView);

        // Initialize views
        cameraPreview = findViewById(R.id.camera_preview);
        capturePhotoButton = findViewById(R.id.capture_photo);
        frontBackGif = findViewById(com.sayukth.aadhaar_ocr.R.id.front_back_gif);
        ocrTextView = findViewById(com.sayukth.aadhaar_ocr.R.id.ocr_text);
        progressBar = findViewById(R.id.progressBar);
        gifImageView = findViewById(R.id.gifImageView);
        overlay = findViewById(R.id.overlay);
        flipTextView = findViewById(R.id.flip_text);
        distanceImage = findViewById(R.id.distance_image);

        // Initialize camera executor
        cameraExecutor = Executors.newSingleThreadExecutor();

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, 101);
        }


        // Retrieve the scan type from the intent
        String scanType = getIntent().getStringExtra(getString(R.string.scan_type));

        if (getString(R.string.front_side).equals(scanType)) {

            // Load GIF using Glide
            Glide.with(this)
                    .asGif()
                    .load(R.drawable.aadhar_front_scan)
                    .into(frontBackGif);

        } else if (getString(R.string.back_side).equals(scanType)) {

            // Load GIF using Glide
            Glide.with(this)
                    .asGif()
                    .load(R.drawable.aadhar_back)
                    .into(frontBackGif);

            ocrTextView.setText(getString(R.string.back_side_focus_request));
        } else if (getString(R.string.big_qr_ocr).equals(scanType)) {
            // Load GIF using Glide
            Glide.with(this)
                    .asGif()
                    .load(R.drawable.aadhar_num_scan)
                    .into(frontBackGif);
            ocrTextView.setText(getString(R.string.aadhaar_number_focus_request));
        }

        if (AadhaarOcrPreferences.getInstance().getBoolean(AadhaarOcrPreferences.Key.IS_FLIP_GIF_SHOW)) {
            AadhaarOcrPreferences.getInstance().put(AadhaarOcrPreferences.Key.IS_FLIP_GIF_SHOW, false);

            // Show the flip GIF
            gifImageView.setVisibility(View.VISIBLE);
            flipTextView.setVisibility(View.VISIBLE);
            cameraPreview.setVisibility(View.GONE);
            capturePhotoButton.setVisibility(View.GONE);
            frontBackGif.setVisibility(View.GONE);
            overlay.setVisibility(View.GONE);
            ocrTextView.setVisibility(View.GONE);
            distanceImage.setVisibility(View.GONE);


            // Load GIF using Glide
            Glide.with(this).asGif().load(R.drawable.fip_aadhar_1).into(gifImageView);

            // Delay for 2 seconds, then show camera preview
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                gifImageView.setVisibility(View.GONE);
                flipTextView.setVisibility(View.GONE);

                // Re-initialize the camera view
                startCamera();

                cameraPreview.setVisibility(View.VISIBLE);
                capturePhotoButton.setVisibility(View.VISIBLE);
                frontBackGif.setVisibility(View.VISIBLE);
                overlay.setVisibility(View.VISIBLE);
                ocrTextView.setVisibility(View.VISIBLE);
                distanceImage.setVisibility(View.VISIBLE);

            }, 3000); // 2000ms = 2 seconds
        }


        // Set up photo capture
        capturePhotoButton.setOnClickListener(v -> capturePhoto());


    }


//    Starts the Camera for capture
    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

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
                camera = cameraProvider.bindToLifecycle(
                        this, cameraSelector, preview, imageCapture);

                // Set initial zoom level (e.g., 2x zoom)
                float zoomRatio = 2.0f; // Adjust as needed
                camera.getCameraControl().setZoomRatio(zoomRatio);

            } catch (Exception e) {
                Toast.makeText(this, "Failed to start camera: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
                Log.e("failed",""+e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    /**
     * Captures a photo.
     */

    private void capturePhoto() {
        if (imageCapture == null) return;

        runOnUiThread(() -> {
//            progressBar.setVisibility(View.VISIBLE);
            DateUtils.showLoading(CustomCameraLaunchActivity.this);
            capturePhotoButton.setVisibility(View.GONE);
        });

        // Get the cache directory and create the 'camera' subfolder if it doesn't exist
        File cacheDir = new File(getCacheDir(), "camera");
        if (!cacheDir.exists() && !cacheDir.mkdirs()) {
            return;
        }

        // Create a unique file name for the photo
        File photoFile = new File(cacheDir, "photo-" + System.currentTimeMillis() + JPG);

        ImageCapture.OutputFileOptions outputFileOptions =
                new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        // Capture the photo and save it in the 'camera' folder
        imageCapture.takePicture(outputFileOptions, cameraExecutor,
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        runOnUiThread(() -> {

                            Toast.makeText(CustomCameraLaunchActivity.this,
                                    "Photo saved: " + photoFile.getAbsolutePath(),
                                    Toast.LENGTH_SHORT).show();

                            Intent resultIntent = new Intent();
                            resultIntent.putExtra(getString(R.string.path), photoFile.getAbsolutePath());
                            setResult(RESULT_OK, resultIntent);
                            finish();
                        });
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        runOnUiThread(() -> Toast.makeText(CustomCameraLaunchActivity.this,
                                "Photo capture failed: " + exception.getMessage(),
                                Toast.LENGTH_SHORT).show());
                    }
                });
    }


    private boolean allPermissionsGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
        // Dismiss dialog if activity is closing
        DateUtils.hideLoading();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101 && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
        }
    }

}

