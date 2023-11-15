package com.gamealike.CSE476_Project;

import android.os.Bundle;

import com.gamealike.CSE476_Project.ui.gameinfo.GameInfoFragment;
import com.gamealike.CSE476_Project.ui.home.HomeViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import java.util.List;

import com.gamealike.CSE476_Project.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get configured genres data
        List<String> configuredGenres = getIntent().getStringArrayListExtra("genres");

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get the ViewModel for HomeActivity to store genres for fragment access
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        // Set the passed in configured genres for the ViewModel
        homeViewModel.setConfiguredGenres(configuredGenres);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_home,
                R.id.navigation_profile, R.id.navigation_game_info).build();
        NavController navController = Navigation.findNavController(this,
                R.id.nav_host_fragment_activity_home);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    private Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.container);
    }
}