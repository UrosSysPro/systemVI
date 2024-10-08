package com.systemvi.particle;

import com.systemvi.engine.application.Game;
import com.systemvi.engine.buffer.ArrayBuffer;
import com.systemvi.engine.buffer.ElementsBuffer;
import com.systemvi.engine.buffer.VertexArray;
import com.systemvi.engine.camera.Camera3;
import com.systemvi.engine.model.VertexAttribute;
import com.systemvi.engine.shader.ElementsDataType;
import com.systemvi.engine.shader.Primitive;
import com.systemvi.engine.shader.Shader;
import com.systemvi.engine.ui.utils.data.Colors;
import com.systemvi.engine.utils.Utils;
import com.systemvi.engine.window.Window;

public class Triangle  extends Game {
    public Triangle(){
        super(3,3,60,800,600,"Triangle");
    }
    VertexArray vertexArray;
    ArrayBuffer arrayBuffer;
    ElementsBuffer elementsBuffer;
    Shader shader;
    Camera3 camera;

    @Override
    public void setup(Window window) {
        vertexArray = new VertexArray();
        arrayBuffer = new ArrayBuffer();
        elementsBuffer = new ElementsBuffer();


            float width = window.getWidth();
            float height = window.getHeight();
            camera = Camera3.builder2d()
                .size(width, height)
                .position(width / 2, height / 2)
                .build();

        vertexArray.bind();
        arrayBuffer.bind();
        elementsBuffer.bind();


        arrayBuffer.setVertexAttributes(new VertexAttribute[]{
                new VertexAttribute("position", 2)
        });

        arrayBuffer.setData(new float[]{
                400,150,
                200,450,
                600,450
        });

        elementsBuffer.setData(new int[]{0,1,2});

        shader = Shader.builder()
                .vertex("vertex.glsl")
                .fragment("fragment.glsl")
                .build();

        System.out.println(shader.getLog());
        System.out.println(shader.isCompiled());
    }
    @Override
     public void loop(float delta) {
        long startTime = System.nanoTime();

        Window window = getWindow();
        window.pollEvents();
        if (window.shouldClose()) close();
        Utils.clear(Colors.green400(), Utils.Buffer.COLOR_BUFFER);

//        Utils.enableLines(2);
        shader.use();
        shader.setUniform("view", camera.view());
        shader.setUniform("projection", camera.projection());
        vertexArray.bind();
        shader.drawElements(Primitive.TRIANGLES, 1, ElementsDataType.UNSIGNED_INT, 3);
//        Utils.disableLines();

        long endTime = System.nanoTime();
        long frameTime = endTime - startTime;
        long nano = frameTime % 1000;
        long micro = frameTime / 1000 % 1000;
        long milli = frameTime / 1000000;
        long fps = 1000 / ( milli == 0 ? 1 : milli);
        System.out.printf("\r %3d %3d %3d fps: %3d", milli, micro, nano, fps);
    }
}
