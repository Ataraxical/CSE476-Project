package com.gamealike.CSE476_Project.ui.home;


import androidx.lifecycle.ViewModelProvider;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.gamealike.CSE476_Project.HomeActivity;
import com.gamealike.CSE476_Project.R;
import com.gamealike.CSE476_Project.databinding.FragmentHomeBinding;
import com.gamealike.CSE476_Project.game.Game;
import com.gamealike.CSE476_Project.game.Genre;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import GameAlikeApiInterface.ApiInterface;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private String cookie = "";


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    private void launchGameInfoFragment(int id) {
        // Switch HomeFragment with GameInfoFragment
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_home).navigate(R.id.navigation_game_info, bundle);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        HomeActivity activity = (HomeActivity) getActivity();
        if (activity.getLoadedListings() == null) {
            activity.loadGames(this);
        } else {
            makeListings();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void makeListings() {
        if (!this.isVisible())
            return;

        // Find correct container to set genres rows in
        LinearLayout genresContainer = binding.llHomeGenresContainer;

        // Create inflater for activity this fragment is associated with
        LayoutInflater layoutInflater = requireActivity().getLayoutInflater();

        HomeActivity activity = (HomeActivity) getActivity();

        for (Genre genre : activity.getLoadedListings().values()) {
            if (genre.getGames().size() == 0)
                continue;

            // Create a scroll view for each genre row
            HorizontalScrollView genreRowScroll = new HorizontalScrollView(requireContext());

            // Inflate rows
            View genreRow = layoutInflater.inflate(R.layout.genre_row, null);
            LinearLayout ll = genreRow.findViewById(R.id.row);
            TextView genreTitle = genreRow.findViewById(R.id.text_title);
            genreTitle.setText(genre.getName());

            // Create 3 game card entries for each genre
            for (Game game : genre.getGames()) {
                // Use inflater to generate cards for games
                View gameCard = layoutInflater.inflate(R.layout.game_card, null);

                // Find game's title
                TextView gameTitle = gameCard.findViewWithTag("title");
                gameTitle.setText(game.getName());

                // Create Button to launch GameInfoFragment
                // Bind game's button and add to list of buttons
                Button gameButton = gameCard.findViewWithTag("info");
                gameButton.setText("Game Info");

                ImageView imageView = gameCard.findViewWithTag("image");
                loadImage(game, imageView);

                gameButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Handle button click to launch GameInfoFragment
                        launchGameInfoFragment(game.getId());
                    }
                });

                // Cast as linear layout and add new card
                ll.addView(gameCard);
            }

            // Add genre row to scrolling container
            genreRowScroll.addView(genreRow);
            // Add row scrolling container to parent vertical container
            genresContainer.addView(genreRowScroll);
        }
    }

    private void loadImage(Game game, ImageView imageView) {
        if (game.getImage().equals(""))
            return;

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            Bitmap image = null;
            try {
                URL url = new URL(game.getImage());
                image = BitmapFactory.decodeStream(url.openStream());
            } catch(IOException e) {
                e.printStackTrace();
            }

            final Bitmap finalImage = image;
            handler.post(() -> {
                if (finalImage != null) {
                    imageView.setImageBitmap(finalImage);
                    game.setLoadedImage(finalImage);
                } else {
                    Toast.makeText(getContext(), "Unable to load image.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}

