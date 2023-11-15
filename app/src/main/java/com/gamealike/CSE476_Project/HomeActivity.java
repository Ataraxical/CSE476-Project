package com.gamealike.CSE476_Project;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.gamealike.CSE476_Project.game.Game;
import com.gamealike.CSE476_Project.game.Genre;
import com.gamealike.CSE476_Project.ui.gameinfo.GameInfoFragment;
import com.gamealike.CSE476_Project.ui.home.HomeFragment;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.gamealike.CSE476_Project.databinding.ActivityHomeBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import GameAlikeApiInterface.ApiInterface;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;

    private String cookie = "";

    private HashMap<Integer, Genre> loadedListings;
    private HashMap<Integer, Game> loadedGames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get configured genres data
        cookie = getIntent().getStringExtra("cookie");

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get the ViewModel for HomeActivity to store genres for fragment access
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        //homeViewModel.setConfiguredGenres(genres);
        // Set the passed in configured genres for the ViewModel
        //homeViewModel.setConfiguredGenres(configuredGenres);

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

    public String getCookie() {
        return this.cookie;
    }

    public HashMap<Integer, Genre> getLoadedListings() {
        return this.loadedListings;
    }

    public HashMap<Integer, Game> getLoadedGames() {
        return this.loadedGames;
    }

    public void loadGames(HomeFragment fragment) {
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

                            ArrayList<Integer> gameGenres = new ArrayList<>();
                            for (int k = 0; k < genres.length(); k++)
                                gameGenres.add(genres.getJSONObject(k).getInt("id"));

                            newGame.setGenres(gameGenres);

                            if (!loadedGames.containsKey(id)) {
                                loadedListings.get(genreID).addGame(newGame);
                                loadedGames.put(id, newGame);
                            }
                        }
                    }

                    fragment.makeListings();

                } catch (JSONException e) {
                    Toast.makeText(HomeActivity.this, "Unable to load recommendations.",
                            Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    public Game getGame(int id) {
        if (this.loadedGames == null)
            return null;

        return this.loadedGames.get(id);
    }

    public Genre getGenre(int id) {
        if (this.loadedListings == null)
            return null;

        return this.loadedListings.get(id);
    }
}