package com.systemvi.examples.test3d;

import com.systemvi.engine.application.Application;
import com.systemvi.engine.camera.Camera;
import com.systemvi.engine.model.Mesh;
import com.systemvi.engine.model.VertexAttribute;
import com.systemvi.engine.shader.Shader;
import com.systemvi.engine.window.Window;
import org.joml.Matrix4f;
import static org.lwjgl.opengl.GL33.*;

public class App extends Application {

    public App(int openglVersionMajor, int openglVersionMinor, int targetFPS) {
        super(openglVersionMajor, openglVersionMinor, targetFPS);
    }
    public Mesh mesh;
    public Shader shader;
    public Camera camera;
    public CameraController controller;
    public Window window;
    float x=0,y=0,z=50;

    @Override
    public void setup() {

        float width=800,height=600;

        window=new Window(800,600,"Test 3d");
        mesh=new Mesh(
            new VertexAttribute("position",3),
            new VertexAttribute("normal", 3),
            new VertexAttribute("uv",2)
        );
        float size=1;
        mesh.setVertexData(new float[]{
            //position      normal      uv
            -size,  size, 0,      0,0,1,      0,1,
             size,  size, 0,      0,0,1,      1,1,
            -size, -size, 0,      0,0,1,      0,0,
             size, -size, 0,      0,0,1,      0,1,
        });
        mesh.setIndices(new int[]{
            0,1,2,
            1,2,3
        });
        camera=new Camera();
        camera.setPerspectiveProjection((float)Math.toRadians(60),width/height,0.1f,1000);
//        camera.setOrthographicProjection(-width/height,width/height,height/height,-height/height,0.1f,100);
        camera.update();

        controller=new CameraController(0,0,2,0,0,-(float)Math.PI/2);
        controller.camera=camera;

        shader=new Shader(
            "assets/examples/test3d/vertex.glsl",
            "assets/examples/test3d/fragment.glsl"
        );
        if(!shader.isCompiled()){
            System.out.println(shader.getLog());
        }
        window.addOnKeyPressListener((key, scancode, mods) -> controller.keyDown(key));
        window.addOnKeyReleaseListener((key, scancode, mods) -> controller.keyUp(key));
        window.addOnMouseMoveListener((x1, y1) -> controller.mouseMove((float) x1, 600-(float) y1));
        window.addOnMouseDownListener((button, mods) -> controller.mouseDown());
        window.addOnMouseUpListener((button, mods) -> controller.mouseUp());
    }

    @Override
    public void loop(float delta) {
        if(window.shouldClose())close();
        window.pollEvents();

        glClearColor(0,0,0,1);
        glClear(GL_COLOR_BUFFER_BIT);

        controller.update(delta);

        shader.use();
        shader.setUniform("view",camera.getView());
        shader.setUniform("projection",camera.getProjection());

        shader.setUniform("model",new Matrix4f().identity().translate(0,0,0));
        mesh.drawElements(2);

        shader.setUniform("model",new Matrix4f().identity().translate(2,2,2));
        mesh.drawElements(2);

        window.swapBuffers();
    }
}
