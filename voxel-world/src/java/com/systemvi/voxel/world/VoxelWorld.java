package com.systemvi.voxel.world;
import com.systemvi.Main2;
import com.systemvi.engine.application.Game;
import com.systemvi.engine.utils.Utils;
import com.systemvi.engine.window.Window;

public class VoxelWorld{
    public static void main(String[] args) {
        Utils.assetsFolder="";
        new DebugApp(3,3,60).run();
    }
}
