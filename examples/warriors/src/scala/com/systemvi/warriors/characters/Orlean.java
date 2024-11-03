package com.systemvi.warriors.characters;

import com.systemvi.warriors.Warrior;
import com.systemvi.warriors.game_classes.Ranger;

public class Orlean extends Warrior implements Ranger {
    public Orlean() {
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
}
