package com.gamealike.CSE476_Project.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.gamealike.CSE476_Project.ConfigureGenresActivity;
import com.gamealike.CSE476_Project.HomeActivity;
import com.gamealike.CSE476_Project.LoginActivity;
import com.gamealike.CSE476_Project.R;
import com.gamealike.CSE476_Project.databinding.FragmentProfileBinding;
import com.gamealike.CSE476_Project.game.Game;
import com.gamealike.CSE476_Project.game.Genre;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import GameAlikeApiInterface.ApiInterface;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);

        setupUI();

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setupUI() {
        loadUsername();
        setupButtons();
    }

    private void loadUsername() {
        HomeActivity activity = (HomeActivity) getActivity();
        String cookie = activity.getCookie();
        if (cookie == null)
            return;
        if (cookie.equals(""))
            return;

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            JSONObject data;
            try {
                data = ApiInterface.getUserInfo(cookie);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            final JSONObject finalData = data;
            handler.post(() -> {
                try {
                    JSONObject fd = finalData.getJSONObject("data");
                    String username = fd.getString("username");
                    String text = getString(R.string.email_username_prefix) + username;
                    binding.textEmail.setText(text);
                } catch (JSONException e) {
                    Toast.makeText(activity, "Unable to load username.",
                            Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void setupButtons() {
        binding.buttonGenres.setOnClickListener(v -> {
            HomeActivity activity = (HomeActivity) getActivity();
            Intent intent = new Intent(activity, ConfigureGenresActivity.class);
            String cookie = activity.getCookie();
            intent.putExtra("cookie", cookie);
            startActivity(intent);
            activity.finish();
        });

        binding.buttonSignout.setOnClickListener(v -> {
            HomeActivity activity = (HomeActivity) getActivity();
            Intent intent = new Intent(activity, LoginActivity.class);
            startActivity(intent);
            activity.finish();
        });
    }
}