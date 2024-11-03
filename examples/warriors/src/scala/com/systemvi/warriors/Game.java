package com.systemvi.warriors;

import com.systemvi.warriors.characters.Despot;
import com.systemvi.warriors.characters.Orlean;

public class Game {
    public Party party;

    public void setup() {
        party = new Party(new Warrior[]{
                new Despot(),
                new Orlean()
        });
    }

    public void loop() {
        input();
        update();
        draw();
    }

    public void input() {

    }

    public void update() {

    }

    public void draw() {

    }
}
