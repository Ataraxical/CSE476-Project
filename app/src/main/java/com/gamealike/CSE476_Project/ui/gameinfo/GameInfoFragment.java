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

import com.gamealike.CSE476_Project.R;
import com.gamealike.CSE476_Project.databinding.FragmentGameinfoBinding;
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

    private String title = "";
    private String imageUrl = "";
    private String price = "";
    private String date = "";
    private String description = "";
    private ArrayList<String> tags = new ArrayList<>();
    private TextView textPrice;
    private TextView textDate;
    private TextView textDescription;
    private ImageView image;

    private FlexboxLayout gameTags;

    public void setGameInfo(String title, String price, String date, String description,
                            String imageUrl, String[] tags) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.price = price;
        this.date = date;
        this.description = description;
        this.tags = new ArrayList<>(Arrays.asList(tags));
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPrice(int price) {
        if (price == 0) {
            this.price = "free";
        } else {
            this.price = "$" + (float) price / 100;
        }
    }

    public void setDate(int month, int day, int year) {
        this.date = month + "/" + day + "/" + year;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageUrl(String url) {
        this.imageUrl = url;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GameInfoViewModel profileViewModel =
                new ViewModelProvider(this).get(GameInfoViewModel.class);

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

        this.loadAllInfo();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void loadAllInfo() {
        textPrice.setText(price);
        String date = getString(R.string.game_date_prefix) + this.date;
        textDate.setText(date);
        textDescription.setText(description);
        this.loadTags();
        this.loadImage();
    }

    private void loadTags() {
        for (String tag : tags) {
            View tagView = inflater.inflate(R.layout.game_tag, null, false);
            TextView tagText = tagView.findViewById(R.id.text_name);

            tagText.setText(tag);

            gameTags.addView(tagView);
        }
    }

    private void loadImage() {
        if (this.imageUrl.equals(""))
            return;

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            Bitmap image = null;
            try {
                URL url = new URL(this.imageUrl);
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
