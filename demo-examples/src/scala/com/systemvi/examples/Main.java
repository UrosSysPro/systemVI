package com.systemvi.examples;

import com.systemvi.engine.utils.Utils;
import com.systemvi.examples.sdf.App;

public class Main {
    public static void main(String[] args){
        Utils.assetsFolder="";
        new App().run();
    }
}
