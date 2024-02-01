package com.sayukth.aadhaar_ocr.ui;

import static androidx.core.content.FileProvider.getUriForFile;
import static com.sayukth.aadhaar_ocr.constants.AadhaarOcrConstants.AADHAAR_BIGQR_OCR;
import static com.sayukth.aadhaar_ocr.constants.AadhaarOcrConstants.AADHAAR_OCR_BACK_SIDE;
import static com.sayukth.aadhaar_ocr.constants.AadhaarOcrConstants.AADHAAR_OCR_FRONT_SIDE;
import static com.sayukth.aadhaar_ocr.constants.AadhaarOcrConstants.BIG_QR_CODE;
import static com.sayukth.aadhaar_ocr.constants.AadhaarOcrConstants.OCR;
import static com.sayukth.aadhaar_ocr.constants.AadhaarOcrConstants.QRCODE;
import static com.sayukth.aadhaar_ocr.constants.AadhaarOcrConstants.SMALL_QR_CODE;
import static com.sayukth.aadhaar_ocr.ocrpreferences.AadhaarOcrPreferences.Key.AADHAAR_INPUT_TYPE;
import static com.sayukth.aadhaar_ocr.ocrpreferences.AadhaarOcrPreferences.Key.AADHAAR_OCR_SCAN_SIDE;
import static com.sayukth.aadhaar_ocr.ocrpreferences.AadhaarOcrPreferences.Key.QR_CODE_SCAN_TYPE_KEY;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.sayukth.aadhaar_ocr.R;
import com.sayukth.aadhaar_ocr.ocrpreferences.AadhaarOcrPreferences;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.List;

public class DetectAadhaarActivity extends AppCompatActivity {

    public static final String INTENT_IMAGE_PICKER_OPTION = "image_picker_option";
    public static final String INTENT_ASPECT_RATIO_X = "aspect_ratio_x";
    public static final String INTENT_ASPECT_RATIO_Y = "aspect_ratio_Y";
    public static final String INTENT_LOCK_ASPECT_RATIO = "lock_aspect_ratio";
    public static final String INTENT_IMAGE_COMPRESSION_QUALITY = "compression_quality";
    public static final String INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT = "set_bitmap_max_width_height";
    public static final String INTENT_BITMAP_MAX_WIDTH = "max_width";
    public static final String INTENT_BITMAP_MAX_HEIGHT = "max_height";
    public static final int REQUEST_IMAGE_CAPTURE = 0;
    public static final int REQUEST_GALLERY_IMAGE = 1;
    private static final String TAG = DetectAadhaarActivity.class.getSimpleName();
    public static String fileName;
    private boolean lockAspectRatio = false, setBitmapMaxWidthHeight = false;
    private int ASPECT_RATIO_X = 16, ASPECT_RATIO_Y = 9, bitmapMaxWidth = 1000, bitmapMaxHeight = 1000;
    private int IMAGE_COMPRESSION = 80;
    static boolean aadharInputTypeFlag = false;

    AadhaarOcrPreferences aadhaarOcrPreferences;

    public static void showImagePickerOptions(Context context, PickerOptionListener listener) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.aadhaar_scan_options_dialog_layout, null);

        final AlertDialog alertD = new AlertDialog.Builder(context).create();

        alertD.setTitle(context.getString(R.string.choose_aadhaar_options_title));

        Button btnSmallQrCodeScan = (Button) promptView.findViewById(R.id.small_qr_scan_btn);

        Button btnBigQrCodeScan = (Button) promptView.findViewById(R.id.big_qr_scan_btn);

        Button btnBigQrCodeOCR =(Button)  promptView.findViewById(R.id.big_qr_ocr_btn);

        Button btnFrontAadhaarCapture = (Button) promptView.findViewById(R.id.front_aadhaar_capture_btn);

        Button btnBackAadhaarCapture = (Button) promptView.findViewById(R.id.back_aadhaar_capture_btn);

        btnSmallQrCodeScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AadhaarOcrPreferences.getInstance().put(QR_CODE_SCAN_TYPE_KEY, SMALL_QR_CODE);

                listener.onChooseAadhaarQrCodeScanner();
                alertD.dismiss();
                aadharInputTypeFlag = false;

            }

        });

        btnBigQrCodeScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AadhaarOcrPreferences.getInstance().put(QR_CODE_SCAN_TYPE_KEY, BIG_QR_CODE);

                listener.onChooseAadhaarQrCodeScanner();
                alertD.dismiss();
                aadharInputTypeFlag = false;

            }

        });

        btnBigQrCodeOCR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AadhaarOcrPreferences.getInstance().put(AADHAAR_OCR_SCAN_SIDE, AADHAAR_BIGQR_OCR);

                listener.onTakeCameraSelected();
                alertD.dismiss();
                aadharInputTypeFlag = true;
            }
        });

        btnFrontAadhaarCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AadhaarOcrPreferences.getInstance().put(AADHAAR_OCR_SCAN_SIDE, AADHAAR_OCR_FRONT_SIDE);

                listener.onTakeCameraSelected();
                alertD.dismiss();
                aadharInputTypeFlag = true;


            }


        });

        btnBackAadhaarCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AadhaarOcrPreferences.getInstance().put(AADHAAR_OCR_SCAN_SIDE, AADHAAR_OCR_BACK_SIDE);

                listener.onTakeCameraSelected();
                alertD.dismiss();
                aadharInputTypeFlag = true;

            }
        });


        alertD.setView(promptView);

        alertD.show();

    }

    private static String queryName(ContentResolver resolver, Uri uri) {
        Cursor returnCursor =
                resolver.query(uri, null, null, null, null);
        assert returnCursor != null;
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        String name = returnCursor.getString(nameIndex);
        returnCursor.close();
        Log.i("query name : ", name);
        return name;
    }

    /**
     * Calling this will delete the images from cache directory
     * useful to clear some memory
     */
    public static void clearCache(Context context) {
        File path = new File(context.getExternalCacheDir(), "camera");
        if (path.exists() && path.isDirectory()) {
            for (File child : path.listFiles()) {
                child.delete();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect_aadhaar);

        aadhaarOcrPreferences = AadhaarOcrPreferences.getInstance(this);

        Intent intent = getIntent();
        if (intent == null) {
            Toast.makeText(getApplicationContext(), getString(R.string.toast_image_intent_null), Toast.LENGTH_LONG).show();
            return;
        }

        ASPECT_RATIO_X = intent.getIntExtra(INTENT_ASPECT_RATIO_X, ASPECT_RATIO_X);
        ASPECT_RATIO_Y = intent.getIntExtra(INTENT_ASPECT_RATIO_Y, ASPECT_RATIO_Y);
        IMAGE_COMPRESSION = intent.getIntExtra(INTENT_IMAGE_COMPRESSION_QUALITY, IMAGE_COMPRESSION);
        lockAspectRatio = intent.getBooleanExtra(INTENT_LOCK_ASPECT_RATIO, false);
        setBitmapMaxWidthHeight = intent.getBooleanExtra(INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, false);
        bitmapMaxWidth = intent.getIntExtra(INTENT_BITMAP_MAX_WIDTH, bitmapMaxWidth);
        bitmapMaxHeight = intent.getIntExtra(INTENT_BITMAP_MAX_HEIGHT, bitmapMaxHeight);

        int requestCode = intent.getIntExtra(INTENT_IMAGE_PICKER_OPTION, -1);
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            takeCameraImage();
        } else {
            chooseImageFromGallery();
        }
    }

    private void takeCameraImage() {
        /*fileName = System.currentTimeMillis() + ".jpg";
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, getCacheImagePath(fileName));
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
        }*/
//        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);

        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            fileName = System.currentTimeMillis() + ".jpg";
                            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, getCacheImagePath(fileName));
                            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                            }
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void chooseImageFromGallery() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(pickPhoto, REQUEST_GALLERY_IMAGE);
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE:
                if (resultCode == RESULT_OK) {
                    cropImage(getCacheImagePath(fileName));
                } else {
                    setResultCancelled();
                }
                break;
            case REQUEST_GALLERY_IMAGE:
                if (resultCode == RESULT_OK) {
                    Uri imageUri = data.getData();
                    cropImage(imageUri);
                } else {
                    setResultCancelled();
                }
                break;
            case UCrop.REQUEST_CROP:
                if (resultCode == RESULT_OK) {
                    handleUCropResult(data);
                } else {
                    setResultCancelled();
                }
                break;
            case UCrop.RESULT_ERROR:
                final Throwable cropError = UCrop.getError(data);
                Log.e(TAG, "Crop error: " + cropError);
                setResultCancelled();
                break;
            default:
                setResultCancelled();
        }
    }

    private void cropImage(Uri sourceUri) {
        Uri destinationUri = Uri.fromFile(new File(getCacheDir(), queryName(getContentResolver(), sourceUri)));
        UCrop.Options options = new UCrop.Options();
        options.setCompressionQuality(IMAGE_COMPRESSION);

        // applying UI theme
        options.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        options.setActiveControlsWidgetColor(ContextCompat.getColor(this, R.color.colorPrimary));

        if (lockAspectRatio)
            options.withAspectRatio(ASPECT_RATIO_X, ASPECT_RATIO_Y);

        if (setBitmapMaxWidthHeight)
            options.withMaxResultSize(bitmapMaxWidth, bitmapMaxHeight);

        UCrop.of(sourceUri, destinationUri)
                .withOptions(options)
                .start(this);
    }

    private void handleUCropResult(Intent data) {
        if (data == null) {
            setResultCancelled();
            return;
        }
        final Uri resultUri = UCrop.getOutput(data);
        setResultOk(resultUri);
    }

    private void setResultOk(Uri imagePath) {
        Intent intent = new Intent();
        intent.putExtra("path", imagePath);
        Log.e("setResultOk : path", imagePath.toString());

        setResult(Activity.RESULT_OK, intent);

        if (aadharInputTypeFlag == true) {
            AadhaarOcrPreferences.getInstance().put(AADHAAR_INPUT_TYPE, OCR);

        } else {
            AadhaarOcrPreferences.getInstance().put(AADHAAR_INPUT_TYPE, QRCODE);
        }

        finish();
    }

    private void setResultCancelled() {
        Intent intent = new Intent();
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }

    private Uri getCacheImagePath(String fileName) {
        File path = new File(getExternalCacheDir(), "camera");
        if (!path.exists()) path.mkdirs();
        File image = new File(path, fileName);
        return getUriForFile(DetectAadhaarActivity.this, getPackageName() + ".provider", image);
    }

    public interface PickerOptionListener {
        void onTakeCameraSelected();

        void onChooseAadhaarQrCodeScanner();
    }
}