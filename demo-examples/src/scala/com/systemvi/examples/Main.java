package com.systemvi.examples;

import com.systemvi.engine.utils.Utils;
import com.systemvi.examples.ecs.App;
import com.systemvi.examples.ecs.Cars;

public class Main {
    public static void main(String[] args) throws Exception{
        Utils.assetsFolder="";
        new Cars().run();
    }
}
