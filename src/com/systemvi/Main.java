package com.systemvi;

import com.systemvi.examples.applicationtest.TextureRendererTest;
import com.systemvi.examples.flappybird.FlappyBird;
import com.systemvi.examples.fluid.Fluid;
import com.systemvi.examples.physics2d.Physics;

public class Main {
    public static void main(String[] args) {

        new Physics(3,3,60).run();
    }
}
