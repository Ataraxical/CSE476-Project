package com.gamealike.CSE476_Project.game;

public class Game {
    private final int id;
    private final String name;
    private final String image;
    private final String price;
    private final String description;
    private final String date;

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

    public String getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }
}
