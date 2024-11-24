package com.systemvi.character;

import com.systemvi.engine.application.Game;
import com.systemvi.engine.camera.Camera3;
import com.systemvi.engine.renderers.Shape2Renderer2;
import com.systemvi.engine.renderers.ShapeRenderer;
import com.systemvi.engine.renderers.Square;
import com.systemvi.engine.renderers.TextureRenderer;
import com.systemvi.engine.texture.Texture;
import com.systemvi.engine.texture.TextureRegion;
import com.systemvi.engine.ui.utils.data.Colors;
import com.systemvi.engine.utils.Utils;
import com.systemvi.engine.window.Window;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;
import org.w3c.dom.UserDataHandler;

public class Main extends Game {
    
    public Main(){
        super(3,3,60,800,600,"Character");
    }
    
    Shape2Renderer2 renderer2;
    Camera3 camera;
    Texture texture;
    TextureRegion[][] regions;
    TextureRegion character,plant;

    @Override
    public void setup(Window window) {
        texture=new Texture("assets/tiles.png");
        regions=TextureRegion.split(texture, 18,18);
        character=regions[5][0];
        plant=regions[10][6];
        camera=Camera3.builder2d()
                .size(window.getWidth(),window.getHeight())
                .position(window.getWidth()/2,window.getHeight()/2)
                .build();
        
        renderer2=new Shape2Renderer2();
    }

    @Override
    public void loop(float delta) {
        Utils.clear(Colors.black(), Utils.Buffer.COLOR_BUFFER);
        renderer2.view(camera.view());
        renderer2.projection(camera.projection());
        renderer2.texture_$eq(texture);
        
        
        renderer2.draw(new Square(100,100,100,Colors.gray50(),character, new Matrix4f()));
        renderer2.flush();
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
