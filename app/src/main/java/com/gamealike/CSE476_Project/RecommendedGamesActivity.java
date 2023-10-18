package com.gamealike.CSE476_Project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class RecommendedGamesActivity extends AppCompatActivity {

    private LinearLayout gameCardsContainer;

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

    }
}