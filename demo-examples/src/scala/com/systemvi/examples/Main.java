package com.systemvi.examples;

import com.systemvi.engine.utils.Utils;
import com.systemvi.examples.compute.raymarching.App;
import jni.SDF;
import jni.Tools;

public class Main {
    public static void main(String[] args) throws Exception{
        Utils.assetsFolder="";
//        new App().run();
        Tools t=new Tools();
        System.out.println(t.bar()+t.foo());
        System.out.println(SDF.sphere(1,1,1,1));
    }
}
