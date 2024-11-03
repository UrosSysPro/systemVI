package com.systemvi.warriors;

public abstract class Warrior {
    public int damage;
    public int movementSpeed;
    public int health, magicResistance, armor;
    public int lightCooldown, heavyCooldown,specialCooldown,utilityCooldown;
    
    public abstract void setDefaultStats();
    public abstract void lightAttack();
    public abstract void heavyAttack();
    public abstract void specialAttack();
    public abstract void utility();
}
