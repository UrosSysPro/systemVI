package com.systemvi.examples;

import com.systemvi.engine.utils.Utils;
import com.systemvi.examples.compute.GameOfLife;
import com.systemvi.examples.compute.fluid.App;
import com.systemvi.examples.fluid.Fluid;
import com.systemvi.examples.fractals.Juliaset;
import com.systemvi.examples.noise.NoiseTest;

public class Main {
    public static void main(String[] args){
        Utils.assetsFolder="../";
//        new WidgetTest().run();
//        new Fluid(3,3,60).run();
//        new Juliaset(3,3,60,800,600,"Julia set").run();
//        new App(3,3,60).run();
//        new com.systemvi.examples.test3d.normalmapping.App(3,3,60).run();
//        new App(3,3,60).run();
//        new Gradient(4,6,60,800,600,"Gradient").run();
//        new GameOfLife(4,6,60).run();
//        new App().run();
        new NoiseTest(3,3,60,800,600,"Noise functions").run();
//        new NoiseTest(3,3,60,800,600,"ref").run();
    }
}
