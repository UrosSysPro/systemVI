package com.systemvi;

import com.systemvi.examples.breakout.BreakOut;
import com.systemvi.examples.flappybird.FlappyBird;
import com.systemvi.examples.texturetest.App;

public class Main {
    public static void main(String[] args) {

        new BreakOut(3,3,60).run();
    }
}
