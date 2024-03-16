package com.systemvi;


import com.systemvi.engine.utils.Utils;

public class Main {
    public static void main(String[] args){
        System.out.println("core package");
        System.out.println(Utils.readInternal("applicationtest/fragment.glsl"));
    }
}
