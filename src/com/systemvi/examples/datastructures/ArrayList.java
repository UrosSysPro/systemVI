package com.systemvi.examples.datastructures;

public class ArrayList {
    private int[] niz;
    private int n;

    public ArrayList(){
        niz=new int[1];
        n=0;
    }
    public int getSize(){
        return n;
    }
    public void set(int i,int v){
        niz[i]=v;
    }
    public int get(int i){
        return niz[i];
    }
    public void remove(){
        n--;
    }
    public void add(int v){
        if(n>=niz.length){
            int[] noviNiz=new int[n*2];
            for(int i=0;i<n;i++)
                noviNiz[i]=niz[i];
            niz=noviNiz;
        }
        niz[n]=v;
        n++;
    }
}
