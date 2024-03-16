package com.systemvi.examples.fractals;

import com.systemvi.engine.application.Game;
import com.systemvi.engine.camera.Camera;
import com.systemvi.engine.renderers.TextureRenderer;
import com.systemvi.engine.shader.Shader;
import com.systemvi.engine.texture.Format;
import com.systemvi.engine.texture.Texture;
import com.systemvi.engine.utils.Utils;
import com.systemvi.engine.window.Window;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public class Juliaset extends Game {

    public Juliaset(int openglVersionMajor, int openglVersionMinor, int targetFPS, int windowWidth, int windowHeight, String title) {
        super(openglVersionMajor, openglVersionMinor, targetFPS, windowWidth, windowHeight, title);
    }

    TextureRenderer renderer;
    Texture texture;
    Camera camera;
    Shader shader;
    Vector2f position;
    float aspect;
    float zoom;
    boolean up,down,right,left,zoomin,zoomout;
    Vector2f c;
    @Override
    public void setup(Window window) {
        c=new Vector2f(0);
        up=false;
        down=false;
        right=false;
        left=false;
        zoomin=false;
        zoomout=false;
        aspect= (float) window.getWidth() /window.getHeight();
        camera=Camera.default2d(window);
        renderer=new TextureRenderer();
        renderer.setCamera(camera);
        texture=new Texture(1,1, Format.RGB);
        shader= Shader.builder()
                .fragment("examples/fractals/juliaset.glsl")
                .vertex("assets/renderer/textureRenderer/vertex.glsl")
                .build();
        if(!shader.isCompiled()){
            System.out.println(shader.getLog());
        }
        renderer.setShader(shader);
        position=new Vector2f(0);
        zoom=1;
    }

    @Override
    public void loop(float delta) {
        Utils.clear(0,0,0,0, Utils.Buffer.COLOR_BUFFER);

        if(right)position.x+=0.01f*zoom;
        if(left)position.x-=0.01f*zoom;
        if(down)position.y+=0.01f*zoom;
        if(up)position.y-=0.01f*zoom;
        if(zoomin)zoom*=1.01f;
        if(zoomout)zoom/=1.01f;

        shader.use();
        shader.setUniform("c",c);
        shader.setUniform("position",position);
        shader.setUniform("zoom",zoom);
        shader.setUniform("aspect",aspect);
        renderer.draw(texture,0,0,800,600);
        renderer.flush();
    }

    @Override
    public boolean resize(int width, int height) {
        aspect= (float) width /height;
        return super.resize(width, height);
    }

    @Override
    public boolean mouseMove(double x, double y) {
        c.set(x,y);
        c.div(800);
        c.sub(0.5f,0.5f);

        return super.mouseMove(x, y);
    }

    @Override
    public boolean keyDown(int key, int scancode, int mods) {
        if(key==GLFW_KEY_RIGHT)right=true;
        if(key==GLFW_KEY_LEFT)left=true;
        if(key==GLFW_KEY_UP)up=true;
        if(key==GLFW_KEY_DOWN)down=true;
        if(key==GLFW_KEY_D)right=true;
        if(key==GLFW_KEY_A)left=true;
        if(key==GLFW_KEY_W)up=true;
        if(key==GLFW_KEY_S)down=true;
        if(key==GLFW_KEY_Q)zoomin=true;
        if(key==GLFW_KEY_E)zoomout=true;
        return super.keyDown(key, scancode, mods);
    }

    @Override
    public boolean keyUp(int key, int scancode, int mods) {
        if(key==GLFW_KEY_RIGHT)right=false;
        if(key==GLFW_KEY_LEFT)left=false;
        if(key==GLFW_KEY_UP)up=false;
        if(key==GLFW_KEY_DOWN)down=false;
        if(key==GLFW_KEY_D)right=false;
        if(key==GLFW_KEY_A)left=false;
        if(key==GLFW_KEY_W)up=false;
        if(key==GLFW_KEY_S)down=false;
        if(key==GLFW_KEY_Q)zoomin=false;
        if(key==GLFW_KEY_E)zoomout=false;
        return super.keyUp(key, scancode, mods);
    }

    @Override
    public boolean scroll(double offsetX, double offsetY) {
        if(offsetY>0){
            zoom*=1.01f;
        }else{
            zoom/=1.01f;
        }
        return super.scroll(offsetX, offsetY);
    }
}
