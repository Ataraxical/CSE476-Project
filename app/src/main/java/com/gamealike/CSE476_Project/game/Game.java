package com.gamealike.CSE476_Project.game;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class Game {
    private final int id;
    private final String name;
    private final String image;
    private final String price;
    private final String description;
    private final String date;

    private Bitmap loadedImage = null;

    private ArrayList<Integer> genres;

    public Game(
            int id,
            String name,
            String image,
            String price,
            String description,
            String date
    ) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.price = price;
        this.description = description;
        this.date = date;

        this.genres = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public Bitmap getLoadedImage() {
        return this.loadedImage;
    }

    public String getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public ArrayList<Integer> getGenres() {
        return new ArrayList<>(this.genres);
    }

    public void setGenres(ArrayList<Integer> genres) {
        this.genres = genres;
    }

    public void setLoadedImage(Bitmap image) {
        this.loadedImage = image;
    }
}
