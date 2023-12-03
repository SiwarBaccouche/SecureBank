package com.example.securebank;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScanActivity extends AppCompatActivity {

    private static final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA};
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String TAG = "ScanActivity";
    private PreviewView cameraView;
    private ExecutorService cameraExecutor;
    private TessBaseAPI tessBaseAPI;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        cameraView = findViewById(R.id.cameraViewId);

        // Initialize camera executor
        cameraExecutor = Executors.newSingleThreadExecutor();

        if (allPermissionsGranted()) {
            // Copy trained data if needed
            copyTrainedDataIfNeeded();
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
    }

    private void startCamera() {
        try {
            // Initialize camera provider
            ProcessCameraProvider cameraProvider = ProcessCameraProvider.getInstance(this).get();

            // Build preview use case
            Preview preview = new Preview.Builder().build();
            CameraSelector cameraSelector = new CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build();

            preview.setSurfaceProvider(cameraView.getSurfaceProvider());

            // Build image analysis use case
            ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                    .setTargetResolution(new Size(1280, 720))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build();

            imageAnalysis.setAnalyzer(cameraExecutor, image -> {
                try {
                    // Convert image to bitmap
                    ImageProxy.PlaneProxy plane = image.getPlanes()[0];
                    ByteBuffer buffer = plane.getBuffer();

                    // Log image size and byte array size
                    Log.d(TAG, "Image size: " + buffer.remaining());
                    byte[] bytes = new byte[buffer.remaining()];
                    buffer.get(bytes);
                    Log.d(TAG, "Byte array size: " + bytes.length);

                    // Save byte array to a temporary file
                    File tempFile = File.createTempFile("tempImage", ".jpg", getCacheDir());
                    try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                        fos.write(bytes);
                    }

                    // Decode the temporary file into a bitmap using BitmapFactory
                    Bitmap bitmap = BitmapFactory.decodeFile(tempFile.getAbsolutePath());

                    if (bitmap != null) {
                        // Process the bitmap using Tesseract OCR
                        processOCR(bitmap);
                    } else {
                        Log.e(TAG, "Bitmap is null after decoding byte array");
                    }

                    // Delete the temporary file
                    tempFile.delete();

                } catch (Exception e) {
                    Log.e(TAG, "Error during image conversion or OCR processing", e);
                } finally {
                    // Close the image to avoid memory leaks
                    image.close();
                }
            });

            // Bind use cases to the camera lifecycle
            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);

        } catch (Exception e) {
            Log.e(TAG, "Error initializing camera provider", e);
        }
    }

    private void processOCR(Bitmap bitmap) {
        try {
            if (bitmap == null) {
                Log.e(TAG, "Bitmap is null");
                return;
            }

            // Initialize Tesseract OCR
            TessBaseAPI tessBaseAPI = new TessBaseAPI();
            String language = "eng"; // Use the language code for the language you want to recognize (e.g., English)
            String dataPath = getFilesDir().getPath() + File.separator + "tessdata";
            File dataPathDir = new File(dataPath);
            if (!dataPathDir.exists() || !dataPathDir.isDirectory()) {
                Log.e(TAG, "Data path does not exist or is not a directory: " + dataPath);
                return;
            }

            // Check if Tesseract initialization is successful
            if (!tessBaseAPI.init(dataPath, language)) {
                Log.e(TAG, "Tesseract initialization failed");
                return;
            }

            // Process the bitmap using Tesseract OCR
            tessBaseAPI.setImage(bitmap);
            String result = tessBaseAPI.getUTF8Text();

            // Log the OCR result
            Log.d(TAG, "OCR Result: " + result);

            // Display result using a Toast
            runOnUiThread(() -> {
                Toast.makeText(ScanActivity.this, "OCR Result: " + result, Toast.LENGTH_SHORT).show();
            });
        } catch (Exception e) {
            Log.e(TAG, "Error during OCR processing", e);
        } finally {
            // Release Tesseract OCR resources
            if (tessBaseAPI != null) {
                tessBaseAPI.end();
            }
        }
    }


    private void copyTrainedDataIfNeeded() {
        String language = "eng";
        String dataPath = getFilesDir().getPath() + File.separator + "tessdata";
        String trainedDataFileName = language + ".traineddata";

        File dataPathDir = new File(dataPath);
        if (!dataPathDir.exists() || !dataPathDir.isDirectory()) {
            if (dataPathDir.mkdirs()) {
                try (InputStream inputStream = getAssets().open("assets/tessdata/" + trainedDataFileName);
                     OutputStream outputStream = new FileOutputStream(new File(dataPath, trainedDataFileName))) {

                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = inputStream.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, length);
                    }

                } catch (IOException e) {
                    Log.e(TAG, "Error copying trained data", e);
                }
            } else {
                Log.e(TAG, "Failed to create the data path directory");
            }
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Release Tesseract OCR resources
        if (tessBaseAPI != null) {
            tessBaseAPI.end();
        }

        // Shutdown the camera executor
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }
    }
}
