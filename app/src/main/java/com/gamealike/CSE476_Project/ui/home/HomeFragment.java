package com.gamealike.CSE476_Project.ui.home;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.gamealike.CSE476_Project.R;
import com.gamealike.CSE476_Project.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        // Create view object to call which is root of this fragment's layout
        // This view will be returned at the end of onCreateView
        View view = binding.getRoot();
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        //
        // Initialize dynamic views
        //

        String[] genres = getResources().getStringArray(R.array.genres); // TEMPORARY
        // genres data from user configured genres, originally implemented
        // as pulling intent data from ConfigureGenresActivity
        List<String> selectedGenres = Arrays.asList(genres);

        // Find correct container to set genres rows in
        LinearLayout genresContainer = binding.llHomeGenresContainer;

        // Create inflater for activity this fragment is associated with
        LayoutInflater layoutInflater = requireActivity().getLayoutInflater();

        // Iterate through each save genre the user has configured
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
                gameTitle.setText(genre + " Game " + gameNum); // currently just set game title to genre and iteration

                // Cast as linear layout and add new card
                ((LinearLayout) genreRow).addView(gameCard);
            }

            // Add genre row to scrolling container
            genreRowScroll.addView(genreRow);
            // Add row scrolling container to parent vertical container
            genresContainer.addView(genreRowScroll);
        }

        // view was the return from binding.getRoot from above
        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
