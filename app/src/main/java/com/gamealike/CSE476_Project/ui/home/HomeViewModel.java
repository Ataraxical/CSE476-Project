package com.gamealike.CSE476_Project.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.HashMap;
import java.util.List;

public class HomeViewModel extends ViewModel {
    private MutableLiveData<HashMap<Integer, String>> configuredGenres = new MutableLiveData<>();

    public void setConfiguredGenres(List<Integer> ids, List<String> names) {
        HashMap<Integer, String> values = new HashMap<>();
        for (int i = 0; i < ids.size(); i++)
            values.put(ids.get(i), names.get(i));

        configuredGenres.setValue(values);
    }

    // This part was recommended by GPT to observe within lifecycle
    public LiveData<HashMap<Integer, String>> getConfiguredGenres() {
        return configuredGenres;
    }
}