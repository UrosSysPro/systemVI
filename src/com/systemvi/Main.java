package com.systemvi;

import com.systemvi.examples.fractals.Mandelbrotset;
import com.systemvi.examples.minecraft.DebugApp;
import com.systemvi.examples.texturetest.Paint;

public class Main {
    public static void main(String[] args){
//        new DebugApp(3,3,60).run();
//        new GameOfLife(4,6,60).run();
//        new Paint(3,3,60,800,600,"Paint").run();
        new Mandelbrotset(3,3,60,800,600,"Mandelbrot Set").run();
    }
}
