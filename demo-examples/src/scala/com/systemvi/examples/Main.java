package com.systemvi.examples;

import com.systemvi.engine.utils.Utils;
import com.systemvi.examples.parallel.Concurrent;
import com.systemvi.examples.parallel.Parallel;
import com.systemvi.examples.test3d.modelloading.App;

public class Main {
    public static void main(String[] args) throws Exception{
        Utils.assetsFolder="";
        new App().run();

//        new Concurrent();
    }
}
