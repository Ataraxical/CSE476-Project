package com.gamealike.CSE476_Project;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.Intent;

import java.util.List;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.widget.Toast;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class RecommendedGamesActivity extends AppCompatActivity implements SensorEventListener {

    private LinearLayout gameCardsContainer;
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    static float SHAKE_THRESHOLD = (float)200.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommended_games_activity);

        // Get passed genre data
        List<String> selectedGenres = getIntent().getStringArrayListExtra("genres");

        // Find the correct container to place game cards in
        gameCardsContainer = findViewById(R.id.GameCardsContainer);
        gameCardsContainer.removeAllViews();

        for(String genre : selectedGenres){

            // Create an inflater to generate cards for games
            // in future, this would pull games from a database
            // and populate fields that way, hopefully there is a decent way
            // to do so asynchronously as loading images will likely be slow
            // if there are hundred to be loaded
            LayoutInflater inflater = (LayoutInflater) getSystemService(this.LAYOUT_INFLATER_SERVICE);
            View gameCard = inflater.inflate(R.layout.game_card, null);

            // Find the title TextView
            TextView cardTitle = gameCard.findViewWithTag("title");

            // Set generic text
            cardTitle.setText(genre + " Game");

            // Cast as linear layout and add the new card
            ((LinearLayout) gameCardsContainer).addView(gameCard);
        }

        // Instantiate sensor manager
        sensorManager = (SensorManager)  getSystemService(this.SENSOR_SERVICE);

        // Find the accelerometer
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Instructs the user that shaking the device will
        // refresh the page
        Toast toast = Toast.makeText(RecommendedGamesActivity.this, R.string.shakeInstructionText, Toast.LENGTH_SHORT);
        toast.show();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // Display a toast indicting the activity was refreshed
        Toast toast = Toast.makeText(RecommendedGamesActivity.this, R.string.pageRefreshedText, Toast.LENGTH_SHORT);
        toast.show();

        // Get acceleration data and display
        float x = intent.getFloatExtra("x", 0);
        float y = intent.getFloatExtra("y", 0);
        float z = intent.getFloatExtra("z", 0);
        String accelStr = getResources().getString(R.string.accelData, x, y, z);
        Toast accelToast = Toast.makeText(RecommendedGamesActivity.this, accelStr, Toast.LENGTH_SHORT);
        accelToast.show();
    }
    @Override
    protected void onResume() {
        super.onResume();


        // Using body sensors since there is no permission for the accelerometer
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BODY_SENSORS) != PackageManager.PERMISSION_GRANTED) {
            // Accelerometer permission is not granted; request the permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BODY_SENSORS}, 10101);
        } else {
            // Accelerometer permission is already granted

            // Check that the accelerometer exists on this device
            // Register the listener when the activity is in the foreground
            // No need for greater than normal sampling rate on the sensor, so use normal delay
            if (accelerometerSensor != null) {
                sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
            }
        }


    }

    @Override
    protected void onPause() {
        super.onPause();

        // Unregister the listener to save resources when the activity is paused
        sensorManager.unregisterListener(this);
    }
    @Override
    public void onSensorChanged(SensorEvent e) {
        float[] values = e.values;

        float x = values[0];
        float y = values[1];
        float z = values[2];

        // Square acceleration values to prevent negative values impacting
        // the determination of shaking
        if (x*x + y*y + z*z > SHAKE_THRESHOLD) {
            // Refresh some data in the activity
            Intent intent = new Intent(RecommendedGamesActivity.this, RecommendedGamesActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            // Sending acceleration data to new intent to display
            intent.putExtra("x", x);
            intent.putExtra("y", y);
            intent.putExtra("z", z);
            startActivity(intent);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Handle accuracy changes if needed
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 10101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted; you can access the accelerometer
            } else {
                // Permission is denied; handle accordingly (e.g., show a message or provide an alternative)
            }
        }
    }
}