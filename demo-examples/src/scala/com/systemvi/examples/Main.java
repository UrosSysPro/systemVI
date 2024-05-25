package com.systemvi.examples;

import com.systemvi.engine.utils.Utils;
import com.systemvi.examples.compute.GameOfLife;
import com.systemvi.examples.compute.raymarching.App;
import com.systemvi.examples.parallel.Concurrent;
import com.systemvi.examples.parallel.Parallel;

public class Main {
    public static void main(String[] args) throws Exception{
        Utils.assetsFolder="";
        new App().run();
    }
}
