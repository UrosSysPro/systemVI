package com.systemvi.examples.mazegame;

import com.systemvi.engine.application.Application;
import com.systemvi.engine.camera.Camera;
import com.systemvi.engine.renderers.ShapeRenderer;
import com.systemvi.engine.renderers.TextureRenderer;
import com.systemvi.engine.texture.Texture;
import com.systemvi.engine.texture.TextureRegion;
import com.systemvi.engine.window.Window;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL33.*;

public class Maze extends Application {

    public Maze(int openglVersionMajor, int openglVersionMinor, int targetFPS) {
        super(openglVersionMajor, openglVersionMinor, targetFPS);
    }

    public Window window;
    public ShapeRenderer renderer;
    public TextureRenderer textureRenderer;
    public Texture texture;
    public TextureRegion[][] sprites;
    public Camera camera;
    public Map map;

    @Override
    public void setup() {
        window=new Window(800,600,"Maze game");

        camera=new Camera();
        camera.setScreenSize(800,600);
        camera.setScale(1,-1,1);
        camera.setPosition(400,300,0);
        camera.update();

        renderer=new ShapeRenderer();
        renderer.setCamera(camera);

        map=new Map(80,60);

        texture=new Texture("assets/examples/textureTest/tiles.png");
        sprites=TextureRegion.split(texture,18,18);
        textureRenderer=new TextureRenderer();
        textureRenderer.setCamera(camera);
    }

    @Override
    public void loop(float delta) {
        if(window.shouldClose())close();
        window.pollEvents();

        glClearColor(0,0,0,1);
        glClear(GL_COLOR_BUFFER_BIT);

        Vector4f white=new Vector4f(1);
        Vector4f black=new Vector4f(0);
        for(int i=0;i<map.width;i++){
            for(int j=0;j<map.height;j++){
//                if(map.mat[i][j]){
//                    renderer.rect(i*10,j*10,10,10,black);
//                }else{
//                    renderer.rect(i*10,j*10,10,10,white);
//                }
                if(map.mat[i][j]){
                    textureRenderer.draw(sprites[2][2],i*10,j*10,10,10);
                }
            }
        }
//        renderer.flush();
        textureRenderer.flush();

        window.swapBuffers();
    }
}
