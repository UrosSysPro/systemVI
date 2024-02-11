package com.systemvi;

import com.systemvi.examples.compute.Gradient;
import com.systemvi.examples.compute.Graph;
import com.systemvi.examples.fluid.Fluid;
import com.systemvi.examples.fractals.Juliaset;
import com.systemvi.examples.fractals.Mandelbrotset;
import com.systemvi.examples.compute.GameOfLife;
import com.systemvi.examples.minecraft.DebugApp;
import com.systemvi.examples.shadertest.TriangleStrip;
import com.systemvi.examples.uitest.Test;
import com.systemvi.examples.uitest.WidgetTest;

public class Main {
    public static void main(String[] args){
//        new GameOfLife(4,6,60).run();
//        new Mandelbrotset(3,3,60,800,600,"Mandelbrot Set").run();
//        new Juliaset(3,3,60,800,600,"Mandelbrot Set").run();
//        new Graph(4,6,60,800,600,"Compute graph").run();
//        new Fluid(3,3,60).run();
//        new Gradient(4,3,60,800,600,"Gradient").run();
//        new DebugApp(3,3,60).run();
//        new TriangleStrip().run();
//        new Test(3,3,60,800,600,"Widget renderer test").run();
        new WidgetTest().run();
    }
}
