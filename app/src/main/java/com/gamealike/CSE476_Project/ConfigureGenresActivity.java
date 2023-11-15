package com.gamealike.CSE476_Project;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;


public class ConfigureGenresActivity extends AppCompatActivity {

    private ArrayList<String> selectedGenres = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure_genres);


        LinearLayout genresContainer = findViewById(R.id.genresContainer);

        String[] genres = getResources().getStringArray(R.array.genres);

            List<CheckBox> checkBoxList = new ArrayList<>(genres.length);



        // Iterate over each genre to create checkboxes
        for (int i = 0; i < (genres.length + 1)/2; i++) {

            LayoutInflater inflater = (LayoutInflater) getSystemService(this.LAYOUT_INFLATER_SERVICE);
            View checkBoxRow = inflater.inflate(R.layout.genre_checkbox_row, null);

            // Create and add the first entry
            CheckBox checkBox1 = checkBoxRow.findViewWithTag("checkbox_left");
            checkBox1.setText(genres[i * 2]);
            checkBox1.setId(View.generateViewId());
            checkBoxList.add(checkBox1);

            // Check if restored data indicates this
            // genre checkbox had been selected
            if (selectedGenres.contains(genres[i*2])) {
                checkBox1.setChecked(true);
            }

            // If there is a second entry in this row, create and add it
            if (i * 2 + 1 < genres.length) {
                CheckBox checkBox2 = checkBoxRow.findViewWithTag("checkbox_right");
                checkBox2.setText(genres[i * 2 + 1]);
                checkBox2.setId(View.generateViewId());
                //checkBox2.setTextSize(25);
                checkBoxList.add(checkBox2);

                if (selectedGenres.contains(genres[i*2+1])) {
                    checkBox2.setChecked(true);
                }
            }

            else {
                CheckBox checkBox2 = checkBoxRow.findViewWithTag("checkbox_right");

                // Cast the inflated view as a Linear Layout and the empty checkbox from it.
                ((LinearLayout) checkBoxRow).removeView(checkBox2);
            }

            // Add the row to the parent container
            genresContainer.addView(checkBoxRow);
        }



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
                        selectedGenres.add(checkBox.getText().toString());
                    }
                }

                if (selectedGenres.size() == 0) {
                    Toast.makeText(ConfigureGenresActivity.this, getResources().getString(R.string.configGenresToast), Toast.LENGTH_SHORT).show();
                } else {
                    /*
                    selectedGenres.setLength(selectedGenres.length() - 2); // Remove the trailing comma and space
                    Toast.makeText(ConfigureGenresActivity.this, "Selected genres: " + selectedGenres.toString(), Toast.LENGTH_SHORT).show();
                     */

                    // Create new intent when input is valid
                    // pass the list of selected genres to the next activity
                    Intent home = new Intent(ConfigureGenresActivity.this, HomeActivity.class);
                    home.putStringArrayListExtra("genres", selectedGenres);
                    startActivity(home);
//                    Intent recommendedGames = new Intent(ConfigureGenresActivity.this, HomeActivity.class);
//                    recommendedGames.putStringArrayListExtra("genres", selectedGenres);
//                    startActivity(recommendedGames);
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

        savedInstanceState.putStringArrayList("savedGenres", selectedGenres);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        selectedGenres = savedInstanceState.getStringArrayList("savedGenres");
    }
}