package com.systemvi;

import com.systemvi.examples.applicationtest.GameTest;
import com.systemvi.examples.minecraft.DebugApp;
import com.systemvi.examples.texturetest.CubeMapTest;
import com.systemvi.examples.texturetest.FrameBufferTest;
import com.systemvi.examples.texturetest.TextureDataTest;

public class Main {
    public static void main(String[] args) {
//        new FrameBufferTest(3,3,60,800,600,"Game test").run();
//        new CubeMapTest(3,3,60).run();
        new DebugApp(3,3,60).run();
    }
}
