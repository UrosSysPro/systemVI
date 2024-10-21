package com.systemvi.java;

public class ConstructorAndMethods {
    int value;
    //konstruktor ima isto ime kao ime klase i nema povratni tip
    public ConstructorAndMethods(int value) {
        //parametri i atributi mogu da imaju isto ime, razlikuju se sa this.
        this.value = value;
    }
    //metode imaju potpis 
    // public/private/protected povratniTipIliVoid imeFunkcije(tipParametra imeParametra){
    //      implementacija
    // }
    //pozivaju se kao
    //String s=new String();
    //s.charAt(1)
    public void print(){
        System.out.println("Hello World");
    }
    //method overloading
    public void print(int n){
        for (int i = 0; i < n; i++) System.out.println("Hello World");
    }
    public int value(int value){
        return value;
    }
    
    
    //metode mogu da budu static
    //to znaci da se pozivaju kao 
    //ConstructorAndMethods.create()
    //ConstructorAndMethods.increment()
    public static ConstructorAndMethods create(){
        return new ConstructorAndMethods(2);
    }
    private static int counter=0;
    public static void increment(){
        counter++;
    }
    public static int counter(){
        return counter;
    }
}
