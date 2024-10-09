package com.systemvi.examples;

import com.systemvi.engine.utils.Utils;
import com.systemvi.examples.noise.NoiseTest;

public class Main {
    public static void main(String[] args) throws Exception{
        Utils.assetsFolder="";
        new NoiseTest(3,3,60,800,600,"").run();
    }
}
