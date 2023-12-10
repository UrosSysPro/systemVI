package com.systemvi.examples.minesweaper;

import java.util.Random;

public class Table {
    public Field[][] map;
    public int size;
    public int bombs;

    public Table(int size,int bombs){
        this.size=size;
        this.bombs=bombs;

        map = new Field[size][size];
        restart();
    }
    public void restart(){
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                map[i][j] = new Field();
            }
        }
        Random r=new Random();
        for(int i=0;i<bombs;i++){
            while (true){
                int x=r.nextInt(size);
                int y=r.nextInt(size);
                if(!map[x][y].bomb) {
                    map[x][y].bomb=true;
                    break;
                }
            }
        }
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if(map[i][j].bomb)continue;
                //levo desno gore dole
                if(i-1>=0&&map[i-1][j].bomb)map[i][j].number++;
                if(i+1<size&&map[i+1][j].bomb)map[i][j].number++;
                if(j-1>=0&&map[i][j-1].bomb)map[i][j].number++;
                if(j+1<size&&map[i][j+1].bomb)map[i][j].number++;
                //dijagonalno
                if(i-1>=0   && j-1>=0   && map[i-1][j-1].bomb)map[i][j].number++;
                if(i-1>=0   && j+1<size && map[i-1][j+1].bomb)map[i][j].number++;
                if(i+1<size && j-1>=0   && map[i+1][j-1].bomb)map[i][j].number++;
                if(i+1<size && j+1<size && map[i+1][j+1].bomb)map[i][j].number++;
            }
        }
    }
    public void open(int x,int y){
        map[x][y].open=true;
    }

    public void print(){
        for (int j = 0; j < size; j++) {
            for (int i = 0; i < size; i++) {
                System.out.print(map[i][j].toString());
            }
            System.out.println();
        }
    }
    public void debugPrint(){
        for (int j = 0; j < size; j++) {
            for (int i = 0; i < size; i++) {
                if(!map[i][j].bomb) {
                    System.out.print(" "+map[i][j].number);
                }
                else {
                    System.out.print(" #");
                }
            }
            System.out.println();
        }
    }
}
