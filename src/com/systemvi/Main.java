package com.systemvi;

import com.systemvi.examples.breakout.BreakOut;
import com.systemvi.examples.fluid.Fluid;
import com.systemvi.examples.inversekinematics.Fabrik;
import com.systemvi.examples.inversekinematics.Vector;
import com.systemvi.examples.mazegame.Maze;
import com.systemvi.examples.test3d.App;

public class Main {
    public static void main(String[] args) {
        new App(3,3,60).run();
    }
}
