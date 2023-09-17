package com.systemvi.examples.datastructures;

public class ArrayList<T> {
    private Object[] niz;
    private int n;

    public ArrayList(){
        niz=new Object[1];
        n=0;
    }
    public int getSize(){
        return n;
    }
    public void set(int i,T v){
        niz[i]=v;
    }
    public T get(int i){
        return (T)niz[i];
    }
    public void remove(){
        n--;
    }
    public void add(T v){
        if(n>=niz.length){
            Object[] noviNiz=new Object[n*2];
            for(int i=0;i<n;i++)
                noviNiz[i]=niz[i];
            niz=noviNiz;
        }
        niz[n]=v;
        n++;
    }
}
