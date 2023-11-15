package com.gamealike.CSE476_Project.game;

import java.util.ArrayList;
import java.util.HashMap;

public class Genre {
    private final int id;
    private final String name;
    private final HashMap<Integer, Game> games;

    public Genre(int id, String name) {
        this.id = id;
        this.name = name;

        this.games = new HashMap<>();
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public ArrayList<Game> getGames() {
        return new ArrayList<>(games.values());
    }

    public void addGame(Game game) {
        this.games.put(game.getId(), game);
    }
}
