package com.systemvi.examples;

import com.systemvi.engine.utils.Utils;
import com.systemvi.examples.fluid.Fluid;

public class Main {
    public static void main(String[] args) throws Exception{
        Utils.assetsFolder="";
        new Fluid(3,3,60).run();
    }
}
