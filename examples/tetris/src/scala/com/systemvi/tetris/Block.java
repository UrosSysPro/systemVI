package com.systemvi.tetris;

import com.googlecode.lanterna.TextColor;

public class Block {
    private final TextColor[][][] data;
    private final TextColor color;
    private final int currentRotation;

    public Block(TextColor[][][] data,TextColor color){
        this.color=color;
        currentRotation=0;
        this.data=data;
    }
    public int getWidth(){
        return data[currentRotation].length;
    }
    public int getHeight(){
        return data[currentRotation][0].length;
    }
    public TextColor get(int x,int y){
        return data[currentRotation][x][y];
    }
}
