package com.systemvi.examples.minesweaper;

public class Field {
    boolean bomb;
    boolean open;
    int number;
    public Field(){
        open = false;
        bomb=false;
        number=0;
    }

    public String toString(){
        String s = " ■ ";

        if(open){
            if(bomb) s = " X ";
            else if (number>0) s= " " + number + " ";
            else s="   ";
        }

        return s;
    }
}