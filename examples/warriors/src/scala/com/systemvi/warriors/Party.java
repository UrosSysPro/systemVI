package com.systemvi.warriors;

import org.lwjgl.glfw.GLFW;

public class Party {
    public Warrior[] warriors;
    public int selected;
    public boolean up=false,down=false,left=false,right=false;
    
    public Party(Warrior[] warriors) {
        selected=0;
        this.warriors = warriors;
    }
    public Warrior getCurrentWarrior(){
        return warriors[selected];
    }
    public void selectNextWarrior(){
        selected=selected+1%warriors.length;
    }
    public void selectPreviousWarrior(){
        selected=selected-1;
        if(selected<0) selected=warriors.length-1;
    }
}
