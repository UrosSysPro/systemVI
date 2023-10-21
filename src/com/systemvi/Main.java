package com.systemvi;

import com.systemvi.examples.applicationtest.TextureRendererTest;
import com.systemvi.examples.flappybird.FlappyBird;
import com.systemvi.examples.fluid.Fluid;

public class Main {
    public static void main(String[] args) {

        new Fluid(3,3,60).run();
    }
}
