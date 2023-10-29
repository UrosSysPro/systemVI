package com.systemvi;

import com.systemvi.examples.breakout.BreakOut;
import com.systemvi.examples.fluid.Fluid;
import com.systemvi.examples.inversekinematics.Fabrik;
import com.systemvi.examples.inversekinematics.Vector;
import com.systemvi.examples.mazegame.Maze;

public class Main {
    public static void main(String[] args) {
        new Fabrik(3,3,60).run();
    }
}
