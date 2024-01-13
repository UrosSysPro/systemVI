package com.systemvi;

import com.systemvi.examples.applicationtest.ShapeRendererTest;
import com.systemvi.examples.minecraft.DebugApp;
import com.systemvi.examples.minesweaper.App;
import com.systemvi.examples.tetris.Test;
import com.systemvi.examples.texturetest.TextureDataTest;

public class Main {
    public static void main(String[] args) {
        new TextureDataTest(3,3,60).run();
//        new DebugApp(3,3,60).run();
//        new Test(3,3,60).run();
//        App.start();
//        new WebHook().run();
    }
}
