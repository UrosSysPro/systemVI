package com.systemvi.examples;

import com.systemvi.engine.utils.Utils;
import com.systemvi.examples.uitest.TextWidgetTest;
import com.systemvi.examples.uitest.WidgetRenderer2Test;
import com.systemvi.examples.uitest.WidgetTest;
import com.systemvi.examples.uitest.material.App;

public class Main {
    public static void main(String[] args){
        Utils.assetsFolder="";
//        new WidgetTest().run();
//        new TextWidgetTest().run();
        new App().run();
//        new ContainerTest().run();
//        new WidgetTest().run();
//        new App().run();
//        new WidgetRenderer2Test().run();
//        new NoiseTest(3,3,60,800,600,"ref").run();
    }
}
