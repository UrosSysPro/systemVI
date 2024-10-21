package com.systemvi.java;

interface Drawable {
    void draw();
}
interface Disposable{
    void dispose();
}
interface Player{
    void play();
    void pause();
    void restart();
}

class Test implements Drawable, Disposable,Player{
    public Test(){
        //load resources 
    }

    @Override
    public void dispose() {
        //free resources
    }

    @Override
    public void play() {
        //start something
    }

    @Override
    public void pause() {
        //pause something
    }

    @Override
    public void restart() {
        
    }

    @Override
    public void draw() {
        
    }

    public static void main(String[] args) {
        Test test = new Test();
        Drawable drawable = test;
        Player player = test;
        Disposable disposable = test;
    }
}


