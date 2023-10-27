package com.gamealike.CSE476_Project;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.LifecycleOwner;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class AddGameActivity extends AppCompatActivity {

    Button buttonTakePicture;
    Button buttonRecommended;
    private PreviewView previewView;
    ImageCapture imageCapture;
    String currentPhotoPath;

    private final ActivityResultLauncher<String> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean o) {
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_game);

        // Find the PreviewView defined in the XML layout
        previewView = findViewById(R.id.previewView);

        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        activityResultLauncher.launch(Manifest.permission.CAMERA);
        activityResultLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);


        // Add a listener to the future
        cameraProviderFuture.addListener(() -> {
        try
        {
            // Get the camera provider instance
            ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

            cameraProvider.unbindAll();
            CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
            // Create a Preview instance and set the PreviewView as its surface provider
            Preview preview = new Preview.Builder().build();
            preview.setSurfaceProvider(previewView.getSurfaceProvider());

            imageCapture = new ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY).build();

            cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imageCapture);
        }
        catch (ExecutionException | InterruptedException e)
        {
            e.printStackTrace();
        }
        }, ContextCompat.getMainExecutor(this)); // Execute the listener on the main thread
        // This code uses the camera to capture an image and display it in PreviewView

        buttonTakePicture = findViewById(R.id.image_capture_button);

        buttonTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                takePhoto();
            }
        });

        buttonRecommended = findViewById(R.id.reccomended_navigation);

        buttonRecommended.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddGameActivity.this, RecommendedGamesActivity.class);
                startActivity(intent);
            }
        });
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void takePhoto()
    {
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException exception)
        {
            Toast.makeText(AddGameActivity.this, "Error saving photo: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
        }

        imageCapture.takePicture(
                new ImageCapture.OutputFileOptions.Builder(photoFile).build(),
                ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageSavedCallback()
                {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults)
                    {
                        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        mediaScanIntent.setData(outputFileResults.getSavedUri());
                        sendBroadcast(mediaScanIntent);
                        Toast.makeText(AddGameActivity.this, "Photo Taken and Saved!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception)
                    {
                        Toast.makeText(AddGameActivity.this, "Error taking photo: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
}