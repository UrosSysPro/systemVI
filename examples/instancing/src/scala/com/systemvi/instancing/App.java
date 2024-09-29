package com.systemvi.instancing;

import com.systemvi.engine.application.Application;
import com.systemvi.engine.camera.Camera;
import com.systemvi.engine.model.Mesh;
import com.systemvi.engine.model.VertexAttribute;
import com.systemvi.engine.shader.Shader;
import com.systemvi.engine.window.Window;
import org.lwjgl.glfw.GLFW;

import static org.lwjgl.opengl.GL33.*;

public class App extends Application {
    public App() {
        super(3,3,60);
    }
    Window window;
    Mesh mesh;
    Camera camera;
    Shader shader;
    int instancesToDraw=0;
    @Override
    public void setup() {
        window=new Window(800,600,"instancing test");
        mesh=new Mesh(
            new VertexAttribute("position",2),
            new VertexAttribute("color",4)
        );
        int size=10;
//        mesh.setVertexData(new float[]{
//            -size,  size, 1.0f, 0.0f, 0.0f, 1.0f,
//            size,  size, 0.0f, 0.0f, 1.0f, 1.0f,
//            -size, -size, 1.0f, 0.0f, 0.0f, 1.0f,
//            size,  size, 0.0f, 0.0f, 1.0f, 1.0f,
//            -size, -size, 1.0f, 0.0f, 0.0f, 1.0f,
//            size, -size, 0.0f, 0.0f, 1.0f, 1.0f,
//        });
        mesh.setVertexData(new float[]{
            -size,  size, 1.0f, 0.0f, 0.0f, 1.0f,
             size,  size, 0.0f, 0.0f, 1.0f, 1.0f,
            -size, -size, 1.0f, 0.0f, 0.0f, 1.0f,
             size, -size, 0.0f, 0.0f, 1.0f, 1.0f,
        });
        mesh.setIndices(new int[]{
            0,1,2,
            1,2,3
        });
        mesh.enableInstancing(
            new VertexAttribute("offset",2),
            new VertexAttribute("tint",1)
        );
        int n=5;
        float[] instanceData=new float[3*n*n];
        for(int i=0;i<n;i++){
            for(int j=0;j<n;j++){
                int index=j*n+i;
                instanceData[index*3+0]=i*30;
                instanceData[index*3+1]=j*30;
                instanceData[index*3+2]=(float)index/n/n;
            }
        }
        mesh.setInstanceData(instanceData);
        camera=new Camera();
        camera.setScreenSize(800,600);
        camera.setPosition(0,0,0);
        camera.setScale(1,-1,1);
        camera.update();

        shader= Shader.builder()
            .fragment("fragment.glsl")
            .vertex("vertex.glsl")
            .build();
        if(!shader.isCompiled()){
            System.out.println(shader.getLog());
        }
        window.addOnKeyPressListener((key, scancode, mods) -> {
            if(key== GLFW.GLFW_KEY_UP)instancesToDraw+=5;
            if(key== GLFW.GLFW_KEY_DOWN)instancesToDraw-=5;
            System.out.println(instancesToDraw);
        });
    }

    @Override
    public void loop(float delta) {
        if(window.shouldClose())close();
        window.pollEvents();
        glClearColor(0,0,0,1);
        glClear(GL_COLOR_BUFFER_BIT);

        shader.use();
        shader.setUniform("view",camera.getView());
        shader.setUniform("projection",camera.getProjection());
//        mesh.drawInstanced(6,24);
        mesh.drawInstancedElements(2,25);

        window.swapBuffers();
    }
}
