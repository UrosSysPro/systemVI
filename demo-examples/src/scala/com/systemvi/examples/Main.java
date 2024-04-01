package com.systemvi.examples;

import com.systemvi.engine.utils.Utils;
import com.systemvi.examples.compute.GameOfLife;
import com.systemvi.examples.compute.raymarching.App;
import com.systemvi.examples.fluid.Fluid;
import com.systemvi.examples.fractals.Juliaset;
import com.systemvi.examples.inversekinematics.Fabrik;
import com.systemvi.examples.noise.NoiseTest;
import com.systemvi.examples.uitest.ContainerTest;
import com.systemvi.examples.uitest.WidgetTest;

public class Main {
    public static void main(String[] args){
        Utils.assetsFolder="../";
//        new WidgetTest().run();
        new ContainerTest().run();
//        new NoiseTest(3,3,60,800,600,"ref").run();
    }
}
