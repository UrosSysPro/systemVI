package com.systemvi.examples;

import com.systemvi.engine.utils.Utils;
import com.systemvi.examples.ecs.Cars;
import com.systemvi.examples.shader.App;

public class Main {
    public static void main(String[] args) throws Exception{
        Utils.assetsFolder="";
        new App().run();
    }
}
