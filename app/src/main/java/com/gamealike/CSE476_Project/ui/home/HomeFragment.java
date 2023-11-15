package com.gamealike.CSE476_Project.ui.home;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import com.gamealike.CSE476_Project.R;
import com.gamealike.CSE476_Project.databinding.FragmentHomeBinding;
import com.gamealike.CSE476_Project.ui.gameinfo.GameInfoFragment;

import GameAlikeApiInterface.ApiInterface;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    // ViewModel for data storing from HomeActivity
    private HomeViewModel homeViewModel;
// selectedGenres from configured genres
    private List<String> selectedGenres = new ArrayList<>();


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

        // Observe changes in configured genres
        homeViewModel.getConfiguredGenres().observe(getViewLifecycleOwner(), new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> genres) {
                // Handle changes in configured genres
                selectedGenres = genres;

                // Clear existing views before adding updated views
                genresContainer.removeAllViews();

                //
                // Dynamic views
                //
                // Iterate through each saved genre the user has configured
                for (String genre : selectedGenres) {
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

        // view was the return from binding.getRoot from above
        return view;
    }

    private void launchGameInfoFragment() {
        // Switch HomeFragment with GameInfoFragment
        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_home).navigate(R.id.navigation_game_info);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}

