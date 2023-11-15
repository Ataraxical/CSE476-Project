package com.gamealike.CSE476_Project.ui.gameinfo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.helper.widget.Flow;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.gamealike.CSE476_Project.HomeActivity;
import com.gamealike.CSE476_Project.R;
import com.gamealike.CSE476_Project.databinding.FragmentGameinfoBinding;
import com.gamealike.CSE476_Project.game.Game;
import com.gamealike.CSE476_Project.game.Genre;
import com.google.android.flexbox.FlexboxLayout;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameInfoFragment extends Fragment {
    private FragmentGameinfoBinding binding;

    private LayoutInflater inflater;
    private Game game;
    private ArrayList<String> tags = new ArrayList<>();
    private TextView textPrice;
    private TextView textDate;
    private TextView textDescription;
    private ImageView image;

    private FlexboxLayout gameTags;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GameInfoViewModel profileViewModel =
                new ViewModelProvider(this).get(GameInfoViewModel.class);

        this.inflater = inflater;

        binding = FragmentGameinfoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textPrice = view.findViewById(R.id.text_price);
        textDate = view.findViewById(R.id.text_date);
        textDescription = view.findViewById(R.id.text_description);

        image = view.findViewById(R.id.image_cover);

        gameTags = view.findViewById(R.id.game_tags);

        if (getArguments() != null) {
            HomeActivity activity = (HomeActivity) getActivity();
            this.game = activity.getGame(getArguments().getInt("id"));
        }

        this.loadAllInfo();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void loadAllInfo() {
        textPrice.setText(game.getPrice());
        String date = getString(R.string.game_date_prefix) + game.getDate();
        textDate.setText(date);
        textDescription.setText(game.getDescription());
        this.loadTags();
        this.loadImage();
    }

    private void loadTags() {
        HomeActivity activity = (HomeActivity) getActivity();

        for (int id : this.game.getGenres()) {
            Genre genre = activity.getGenre(id);
            String tag = genre.getName();
            View tagView = inflater.inflate(R.layout.game_tag, null, false);
            TextView tagText = tagView.findViewById(R.id.text_name);

            tagText.setText(tag);

            gameTags.addView(tagView);
        }
    }

    private void loadImage() {
        if (game.getLoadedImage() != null) {
            this.image.setImageBitmap(game.getLoadedImage());
            return;
        }
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
                    this.image.setImageBitmap(finalImage);
                } else {
                    Toast.makeText(getContext(), "Unable to load image.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void returnHome()
    {
        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_home).navigate(R.id.navigation_home);
    }

}
