package com.systemvi.examples;

import com.systemvi.engine.utils.Utils;
import com.systemvi.examples.compute.raymarching.App;
import com.systemvi.examples.uitest.WidgetRenderer2Test;
import com.systemvi.examples.uitest.WidgetTest;

public class Main {
    public static void main(String[] args){
        Utils.assetsFolder="";
//        new WidgetTest().run();
//        new ContainerTest().run();
//        new WidgetTest().run();
//        new App().run();
        new WidgetRenderer2Test().run();
//        new NoiseTest(3,3,60,800,600,"ref").run();
    }
}
