package com.systemvi;

import com.systemvi.examples.applicationtest.ShapeRendererTest;
import com.systemvi.examples.httpexamples.discord.WebHook;
import com.systemvi.examples.minecraft.DebugApp;
import com.systemvi.examples.minesweaper.App;

public class Main {
    public static void main(String[] args) {
        new ShapeRendererTest(3,3,60).run();
//        App.start();
//        new WebHook().run();
    }
}
