package com.systemvi;

import com.systemvi.examples.compute.Graph;
import com.systemvi.examples.fractals.Mandelbrotset;
import com.systemvi.examples.compute.GameOfLife;

public class Main {
    public static void main(String[] args){
//        new GameOfLife(4,6,60).run();
//        new Mandelbrotset(3,3,60,800,600,"Mandelbrot Set").run();
        new Graph(4,6,60,800,600,"Compute graph").run();
    }
}
