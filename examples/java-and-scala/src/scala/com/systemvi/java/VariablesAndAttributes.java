package com.systemvi.java;

import java.io.StreamCorruptedException;
import java.util.Scanner;

public class VariablesAndAttributes {
    //postoje dva tipa tipova u javi:
    // - primitive (int, float, char, short, long, double, byte...)
    //      --alocirani na steku
    //      --imena pocinju malim slovima
    // - references (reference na objekte)
    //      --pokazuju na objekte alocirane na heap-u
    //      --imena pocinju velikim slovom

    //public - vidljivo svuda
    //protected - vidljivo unutar klase, i klasa koje nasledjuju ovu klasu
    //protected - vidljivo samo unutar ove klase
    public int a;
    private float b;
    protected short c;

    public String d;
    private Scanner s;

    //postoji jedna static promenljiva po klasi, obicne promenljive postoje za svaki objekat
    //  u javi je standard da klase koriste pascal case ime (prvo slovo veliko, svaka sledeca rec veliko)
    //  a promenljive camel case (prvo slovo malo, svaka sledeca rec veliko)
    public static int count;
    public static short numberOfSomething;
    public static StreamCorruptedException exception;
}
