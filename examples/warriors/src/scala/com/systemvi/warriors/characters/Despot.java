package com.systemvi.warriors.characters;

import com.systemvi.warriors.Warrior;
import com.systemvi.warriors.game_classes.Knight;

public class Despot extends Warrior implements Knight {
    public Despot() {
        setDefaultStats();
    }

    @Override
    public void setDefaultStats() {
        damage = 1;
        movementSpeed = 1;
        health = 4;
        magicResistance = 2;
        armor = 4;
        lightCooldown = 2;
        heavyCooldown = 2;
        specialCooldown = 2;
        utilityCooldown = 2;
    }

    @Override
    public void lightAttack() {

    }

    @Override
    public void heavyAttack() {

    }

    @Override
    public void specialAttack() {

    }

    @Override
    public void utility() {

    }

    @Override
    public String toString() {
        return "Despot{" +
                "\n\tdamage=" + damage +
                "\n\tmovementSpeed=" + movementSpeed +
                "\n\thealth=" + health +
                "\n\tmagicResistance=" + magicResistance +
                "\n\tarmor=" + armor +
                "\n}";
    }
}
