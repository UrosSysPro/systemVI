package com.systemvi.examples.tetris;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;

public class Tetris {
    private TextColor[][] table;
    private Block[] blocks;
    private BlockInstance player;
    private int width,height,frameCounter,dx;


    public Tetris(int width,int height){
        dx=0;
        frameCounter=0;
        this.width=width;
        this.height=height;
        table=new TextColor[width][height];
        for(int i=0;i<width;i++){
            for(int j=0;j<height;j++){
                table[i][j]=TextColor.ANSI.BLACK;
            }
        }
        TextColor orange=new TextColor.RGB(240,150,170);
        TextColor green=new TextColor.RGB(100,250,150);
        TextColor blue=new TextColor.RGB(100,150,250);
        blocks=new Block[]{
            new Block(new TextColor[][][]{
                {
                    {blue,blue},
                    {blue,blue}
                },
            },blue),
            new Block(new TextColor[][][]{
                {
                    {orange,orange,orange,orange},
                },
                {
                    {orange},
                    {orange},
                    {orange},
                    {orange},
                },
            },orange),
        };
        player=new BlockInstance(blocks[0],width/2-blocks[0].getWidth()/2,0);
    }

    public void draw(TextGraphics graphics){
        for(int i=0;i<width;i++){
            for(int j=0;j<height;j++){
                graphics.setBackgroundColor(table[i][j]);
                graphics.fillRectangle(
                    new TerminalPosition(i*2,j),
                    new TerminalSize(2,1),
                    ' '
                );
            }
        }
        for(int i=0;i< player.block.getWidth();i++){
            for(int j=0;j< player.block.getHeight();j++){
                graphics.setBackgroundColor(player.block.get(i,j));
                graphics.fillRectangle(
                    new TerminalPosition((player.x+i)*2, player.y+j),
                    new TerminalSize(2,1),
                    ' '
                );
            }
        }
    }
    public void input(Screen screen)throws Exception{
        while(true){
            KeyStroke keyStroke=screen.pollInput();
            if(keyStroke==null)return;
            if(keyStroke.getKeyType()== KeyType.Character){
                if(keyStroke.getCharacter()=='d'){
                    dx=1;
                }
                if(keyStroke.getCharacter()=='a'){
                    dx=-1;
                }
            }
        }
    }
    public void update(){
        frameCounter++;
        if(frameCounter>10){
            frameCounter=0;
            player.x+=dx;
            player.y++;
            dx=0;
        }
    }
}
