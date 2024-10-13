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
import org.lwjgl.glfw.GLFW;
import java.io.*;
import java.lang.Thread;
import java.util.ArrayList;
import java.util.Iterator;

class MovingSquare {
    public float x, y;
    public float directionX, directionY;
    public float velocityY;
    public long timestamp;

    public MovingSquare(float x, float y, float directionX, float directionY, float velocityY, long timestamp) {
        this.x = x;
        this.y = y;
        this.directionX = directionX;
        this.directionY = directionY;
        this.velocityY = velocityY;
        this.timestamp = timestamp;
    }
}


public class Triangle extends Game {
    private ArrayList<MovingSquare> squares;
    private static final long SQUARE_LIFETIME = 1600;
    private static final float GRAVITY = .2f;
    private static final float INITIAL_VELOCITY = -4.0f;
    private static final long SPAWN_COOLDOWN = 150;
    private long lastSpawnTime;
    public Triangle() {
        super(3, 3, 60, 800, 600, "Square");
        squares = new ArrayList<>();
        lastSpawnTime = 0;
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
                0, 0,
                1, 0,
                1, 1,
                0, 1
        });

        elementsBuffer.setData(new int[]{
                0, 1, 2,
                0, 2, 3
        });

        shader = Shader.builder()
                .vertex("vertex.glsl")
                .fragment("fragment.glsl")
                .build();

        System.out.println(shader.getLog());
        System.out.println(shader.isCompiled());
    }

    private float[] generateQuadrilateralVertices(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
        return new float[]{
                x1, y1,
                x2, y2,
                x3, y3,
                x4, y4
        };
    }

    @Override
    public void loop(float delta) {
        long startTime = System.nanoTime();
        Window window = getWindow();
        window.pollEvents();

        if (window.shouldClose()) close();
        Utils.clear(Colors.black(), Utils.Buffer.COLOR_BUFFER);

        double[] mouseX = new double[1];
        double[] mouseY = new double[1];

        GLFW.glfwGetCursorPos(window.getId(), mouseX, mouseY);

        long currentTime = System.currentTimeMillis();
        if ((currentTime - lastSpawnTime) > SPAWN_COOLDOWN) {
            lastSpawnTime = currentTime;
            for (int i = 0; i < 4; i++) {
                double angle = Math.toRadians(100 * i);
                float directionX = (float) Math.cos(angle);
                float directionY = (float) Math.sin(angle);
                squares.add(new MovingSquare(
                        (float) mouseX[0],
                        (float) mouseY[0],
                        directionX,
                        directionY,
                        INITIAL_VELOCITY,
                        System.currentTimeMillis()
                ));
            }
        }

        Iterator<MovingSquare> iterator = squares.iterator();
        while (iterator.hasNext()) {
            MovingSquare square = iterator.next();
            if (System.currentTimeMillis() - square.timestamp > SQUARE_LIFETIME) {
                iterator.remove();
            } else {
                square.velocityY += GRAVITY;
                square.x += square.directionX * 1.8;
                square.y += square.velocityY;

                if (square.y < 0) {
                    iterator.remove();
                }
            }
        }

        for (MovingSquare square : squares) {
            float squareSize = 8.0f;
            float halfSize = squareSize / 2.0f;

            float[] squareVertices = generateQuadrilateralVertices(
                    square.x - halfSize, square.y - halfSize,
                    square.x + halfSize, square.y - halfSize,
                    square.x + halfSize, square.y + halfSize,
                    square.x - halfSize, square.y + halfSize
            );

            arrayBuffer.setData(squareVertices);
            shader.use();
            shader.setUniform("view", camera.view());
            shader.setUniform("projection", camera.projection());
            vertexArray.bind();
            shader.drawElements(Primitive.TRIANGLES, 1, ElementsDataType.UNSIGNED_INT, 6);
        }

        long endTime = System.nanoTime();
        long frameTime = endTime - startTime;
        long nano = frameTime % 1000;
        long micro = frameTime / 1000 % 1000;
        long milli = frameTime / 1000000;
        long fps = 1000 / (milli == 0 ? 1 : milli);
        System.out.printf("\r %3d %3d %3d fps: %3d", milli, micro, nano, fps);
    }
}
