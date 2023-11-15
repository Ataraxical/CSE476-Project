package com.gamealike.CSE476_Project.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class HomeViewModel extends ViewModel {
    private MutableLiveData<List<String>> configuredGenres = new MutableLiveData<>();

    public void setConfiguredGenres(List<String> genres) {
        configuredGenres.setValue(genres);
    }

    // This part was recommended by GPT to observe within lifecycle
    public LiveData<List<String>> getConfiguredGenres() {
        return configuredGenres;
    }
}