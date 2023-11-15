package com.gamealike.CSE476_Project;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import GameAlikeApiInterface.ApiInterface;


public class ConfigureGenresActivity extends AppCompatActivity {

    private LinearLayout genresContainer;

    private ArrayList<Integer> selectedGenres = new ArrayList<>();
    private ArrayList<CheckBox> checkBoxList = new ArrayList<>();

    private HashMap<CheckBox, Integer> checkboxMap = new HashMap<>();

    private String cookie = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure_genres);

        this.cookie = getIntent().getStringExtra("cookie");

        genresContainer = findViewById(R.id.genresContainer);

        this.loadGenres();

        // Find the continue button and create logic for moving
        // to the next activity, with input validation (>= 1 box checked)
        Button continueButton = findViewById(R.id.continueButton);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the Continue button click
                // Retrieve selected genres
                // Clear genres so that it does not add duplicates if
                // ConfigureGenre is resumed
                selectedGenres.clear();
                for (CheckBox checkBox : checkBoxList) {
                    if (checkBox.isChecked()) {
                        selectedGenres.add(checkboxMap.get(checkBox));
                    }
                }

                if (selectedGenres.size() == 0) {
                    Toast.makeText(ConfigureGenresActivity.this, getResources().getString(R.string.configGenresToast), Toast.LENGTH_SHORT).show();
                } else {
                    sendGenres();
                }
            }
        });
    }


    // Here just to demonstrate that I can save and restore the
    // activity's state, however this is done automatically for this activity
    //
    // Note: I actually put this here to see if I could save instance state
    // after pressing the back button and the pressing the login button again
    // but apparently, that is not how it works and it is instead using a stack
    // and destroying the activity object when I press the back button
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putIntegerArrayList("savedGenres", selectedGenres);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        selectedGenres = savedInstanceState.getIntegerArrayList("savedGenres");
    }

    private void loadGenres() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            JSONObject data;
            try {
                data = ApiInterface.getGenres(this.cookie);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            final JSONObject finalData = data;
            handler.post(() -> {
                try {
                    JSONArray genres = finalData.getJSONArray("data");
                    for (int i = 0; i < genres.length(); i++) {
                        JSONObject genre = genres.getJSONObject(i);
                        String name = genre.getString("genre");
                        int id = genre.getInt("id");

                        LayoutInflater inflater = (LayoutInflater) getSystemService(this.LAYOUT_INFLATER_SERVICE);
                        View checkBoxRow = inflater.inflate(R.layout.genre_checkbox_row, null);

                        // Create and add the first entry
                        CheckBox checkbox = checkBoxRow.findViewWithTag("checkbox");
                        checkbox.setText(name);
                        checkbox.setId(View.generateViewId());
                        checkBoxList.add(checkbox);

                        checkboxMap.put(checkbox, id);

                        // Check if restored data indicates this
                        // genre checkbox had been selected
                        if (selectedGenres.contains(id)) {
                            checkbox.setChecked(true);
                        }

                        // Add the row to the parent container
                        genresContainer.addView(checkBoxRow);
                    }
                } catch (JSONException e) {
                    Toast.makeText(ConfigureGenresActivity.this, "Unable to load genres.",
                            Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void sendGenres() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            JSONObject data;
            try {
                data = ApiInterface.setUserPreferences(selectedGenres, cookie);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            handler.post(() -> {
                Intent homeActivity = new Intent(ConfigureGenresActivity.this, HomeActivity.class);
                homeActivity.putExtra("cookie", this.cookie);
                startActivity(homeActivity);
            });
        });
    }
}