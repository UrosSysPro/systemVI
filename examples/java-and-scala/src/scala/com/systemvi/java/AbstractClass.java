package com.systemvi.java;

public abstract class AbstractClass {
    public int counter;
    public AbstractClass() {
        counter=0;
    }
    public abstract void increment();
    public int get(){
        return counter;
    }
}

class Counter extends AbstractClass{
    public Counter() {
        super();
    }
    @Override
    public void increment() {
        counter++;
    }
}

class Doubler extends AbstractClass{
    public Doubler() {
        super();
        counter=10;
    }

    @Override
    public void increment() {
        counter++;
    }

    @Override
    public int get() {
        return super.get()*2;
    }
}
