package com.gamealike.CSE476_Project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.HorizontalScrollView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

//import GameAlikeApiInterface.ApiInterface;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private LinearLayout genresContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // genres data from ConfigureGenresActivity
        List<String> selectedGenres = getIntent().getStringArrayListExtra("genres");

        // Find the correct container to set genres rows in
        genresContainer = findViewById(R.id.ll_home_genres_container);

        // Create inflater
        LayoutInflater inflater = (LayoutInflater) getSystemService(this.LAYOUT_INFLATER_SERVICE);

        for (String genre : selectedGenres) {
            // Create a scroll view for each genre row
            HorizontalScrollView genreRowScroll = new HorizontalScrollView(this);

            // Inflate rows
            View genreRow = inflater.inflate(R.layout.genre_row, null);

            // Create 3 game card entries for each genre
            for (int i = 0; i < 3; i++) {
                // Use inflater to generate cards for games
                View gameCard = inflater.inflate(R.layout.game_card, null);

                // Find game's title
                TextView gameTitle = gameCard.findViewWithTag("title");
                gameTitle.setText(genre + " Game " + i); // currently just set game title to genre and iteration

                // Cast as linear layout and add new card
                ((LinearLayout) genreRow).addView(gameCard);
            }

            // Add genre row to scrolling container
            genreRowScroll.addView(genreRow);

            // Add row scrolling container to parent vertical container
            genresContainer.addView(genreRowScroll);

        }

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

}