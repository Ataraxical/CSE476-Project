package com.gamealike.CSE476_Project.ui.recommended;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.gamealike.CSE476_Project.databinding.FragmentRecommendedBinding;

public class RecommendedFragment extends Fragment {

    private FragmentRecommendedBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        RecommendedViewModel recommendedViewModel =
                new ViewModelProvider(this).get(RecommendedViewModel.class);

        binding = FragmentRecommendedBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}