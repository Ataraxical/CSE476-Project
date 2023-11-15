package com.gamealike.CSE476_Project.ui.home;


import androidx.lifecycle.ViewModelProvider;

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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

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

    // ViewModel for data storing from HomeActivity
    private HomeViewModel homeViewModel;

    private String cookie = "";

    // selectedGenres from configured genres


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        // Create view object to call which is root of this fragment's layout
        // This view will be returned at the end of onCreateView
        View view = binding.getRoot();

        // Get the ViewModel
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        // Find correct container to set genres rows in
        LinearLayout genresContainer = binding.llHomeGenresContainer;

        // Create inflater for activity this fragment is associated with
        LayoutInflater layoutInflater = requireActivity().getLayoutInflater();

        /*
        // Observe changes in configured genres
        homeViewModel.getConfiguredGenres().observe(getViewLifecycleOwner(), new Observer<HashMap<Integer, String>>() {
            @Override
            public void onChanged(HashMap<Integer, String> genres) {
                // Handle changes in configured genres
                selectedGenres = genres;

                // Clear existing views before adding updated views
                genresContainer.removeAllViews();

                //
                // Dynamic views
                //
                // Iterate through each saved genre the user has configured
                for (int genre : selectedGenres) {
                    // Create a scroll view for each genre row
                    HorizontalScrollView genreRowScroll = new HorizontalScrollView(requireContext());

                    // Inflate rows
                    View genreRow = layoutInflater.inflate(R.layout.genre_row, null);

                    // Create 3 game card entries for each genre
                    for (int i = 0; i < 3; i++) {
                        // Use inflater to generate cards for games
                        View gameCard = layoutInflater.inflate(R.layout.game_card, null);

                        // Find game's title
                        TextView gameTitle = gameCard.findViewWithTag("title");
                        String gameNum = Integer.toString(i); // Remove later, this is to show change
                        gameTitle.setText(genre + " Game " + gameNum);

                        // Create Button to launch GameInfoFragment
                        // Bind game's button and add to list of buttons
                        Button gameButton = gameCard.findViewWithTag("info");
                        gameButton.setText("Game Info");

                        gameButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Handle button click to launch GameInfoFragment
                                launchGameInfoFragment();
                            }
                        });

                        // Cast as linear layout and add new card
                        ((LinearLayout) genreRow).addView(gameCard);
                    }

                    // Add genre row to scrolling container
                    genreRowScroll.addView(genreRow);
                    // Add row scrolling container to parent vertical container
                    genresContainer.addView(genreRowScroll);
                }
            }
        });
         */

        // view was the return from binding.getRoot from above
        return view;
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

                gameButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Handle button click to launch GameInfoFragment
                        launchGameInfoFragment(game.getId());
                    }
                });

                // Cast as linear layout and add new card
                ((LinearLayout) genreRow).addView(gameCard);
            }

            // Add genre row to scrolling container
            genreRowScroll.addView(genreRow);
            // Add row scrolling container to parent vertical container
            genresContainer.addView(genreRowScroll);
        }
    }
}

