package com.systemvi.examples;

import com.systemvi.engine.utils.Utils;
import com.systemvi.examples.compute.reactiondiffusion.App;

public class Main {
    public static void main(String[] args){
        Utils.assetsFolder="";
//        new WidgetTest().run();
//        new ContainerTest().run();
        new App().run();
//        new NoiseTest(3,3,60,800,600,"ref").run();
    }
}
