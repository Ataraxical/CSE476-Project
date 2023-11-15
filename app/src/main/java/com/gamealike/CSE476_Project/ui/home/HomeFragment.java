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

    private HashMap<Integer, Genre> loadedListings;
    private HashMap<Integer, Game> loadedGames;

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

    private void launchGameInfoFragment() {
        // Switch HomeFragment with GameInfoFragment
        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_home).navigate(R.id.navigation_game_info);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.loadGames();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void loadGames() {
        HomeActivity activity = (HomeActivity) getActivity();
        if (activity == null)
            return;

        this.cookie = activity.getCookie();
        if (this.cookie.equals(""))
            return;

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            JSONObject data;
            try {
                data = ApiInterface.getRecommendations(0, this.cookie);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            final JSONObject finalData = data;
            handler.post(() -> {
                try {
                    JSONArray games = finalData.getJSONArray("data");

                    loadedListings = new HashMap<>();
                    loadedGames = new HashMap<>();

                    for (int i = 0; i < games.length(); i++) {
                        JSONObject game = games.getJSONObject(i);
                        int id = Integer.parseInt(game.getString("game_id"));
                        JSONArray genres = game.getJSONArray("game_genres");
                        String name = game.getString("game_name");
                        String image = game.getString("game_cover");
                        String price = game.getString("game_price");
                        String description = game.getString("game_description");
                        String date = game.getString("release_date");

                        for (int j = 0; j < genres.length(); j++) {
                            int genreID = genres.getJSONObject(j).getInt("id");
                            if (!loadedListings.containsKey(genreID))
                                loadedListings.put(genreID, new Genre(genreID, genres.getJSONObject(j).getString("genre")));

                            Game newGame = new Game(
                                    id,
                                    name,
                                    image,
                                    price,
                                    description,
                                    date
                            );

                            if (!loadedGames.containsKey(id)) {
                                loadedListings.get(genreID).addGame(newGame);
                                loadedGames.put(id, newGame);
                            }
                        }
                    }

                    makeListings();

                } catch (JSONException e) {
                    Toast.makeText(activity, "Unable to load account.",
                            Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void makeListings() {
        // Find correct container to set genres rows in
        LinearLayout genresContainer = binding.llHomeGenresContainer;

        // Create inflater for activity this fragment is associated with
        LayoutInflater layoutInflater = requireActivity().getLayoutInflater();

        for (Genre genre : loadedListings.values()) {
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
}

