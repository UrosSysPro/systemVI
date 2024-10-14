package com.systemvi.character;

import com.systemvi.engine.application.Game;
import com.systemvi.engine.camera.Camera3;
import com.systemvi.engine.renderers.ShapeRenderer;
import com.systemvi.engine.renderers.TextureRenderer;
import com.systemvi.engine.texture.Texture;
import com.systemvi.engine.texture.TextureRegion;
import com.systemvi.engine.ui.utils.data.Colors;
import com.systemvi.engine.utils.Utils;
import com.systemvi.engine.window.Window;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;

public class Main extends Game {
    
    public Main(){
        super(3,3,60,800,600,"Character");
    }
    
    TextureRenderer renderer;
    Camera3 camera;
    Texture texture;
    TextureRegion[][] regions;
    TextureRegion character,plant;
    Vector2i characterPosition,plantPosition;
    int size=100;

    @Override
    public void setup(Window window) {
        texture=new Texture("assets/tiles.png");
        regions=TextureRegion.split(texture, 18,18);
        character=regions[5][0];
        characterPosition=new Vector2i(350,250);
        plant=regions[10][6];
        plantPosition=new Vector2i(450,250);
        camera=Camera3.builder2d()
                .size(window.getWidth(),window.getHeight())
                .position(window.getWidth()/2,window.getHeight()/2)
                .build();
        renderer = new TextureRenderer();
        renderer.view(camera.view());
        renderer.projection(camera.projection());
    }

    @Override
    public void loop(float delta) {
        Utils.clear(Colors.black(), Utils.Buffer.COLOR_BUFFER);
        renderer.draw(character,characterPosition.x,characterPosition.y,size,size);
        renderer.draw(plant,plantPosition.x,plantPosition.y,size,size);
        renderer.flush();
    }
    
    @Override
    public boolean keyDown(int key, int scancode, int mods) {
        if(key == GLFW.GLFW_KEY_D){
            //skreni desno 
        }
        if(key == GLFW.GLFW_KEY_A){
            //skreni levo 
        }
        return true;
    }

    @Override
    public boolean keyUp(int key, int scancode, int mods) {
        if(key == GLFW.GLFW_KEY_D){
            //skreni desno 
        }
        if(key == GLFW.GLFW_KEY_A){
            //skreni levo 
        }
        return true;
    }

    public static void main(String[] args) {
        new Main().run();
    }
}
