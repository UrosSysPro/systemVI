package com.systemvi.character;

import com.systemvi.engine.application.Game;
import com.systemvi.engine.camera.Camera3;
import com.systemvi.engine.renderers.ShapeRenderer2;
import com.systemvi.engine.renderers.Square;
import com.systemvi.engine.texture.Texture;
import com.systemvi.engine.texture.TextureRegion;
import com.systemvi.engine.ui.utils.data.Colors;
import com.systemvi.engine.utils.Utils;
import com.systemvi.engine.window.Input;
import com.systemvi.engine.window.Window;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Main extends Game {

    public Main() {
        super(3, 3, 60, 800, 600, "Character");
    }

    ShapeRenderer2 renderer2;
    Camera3 camera;
    Texture texture;
    TextureRegion[][] regions;
    TextureRegion character, plant;

    int tileSize = 40;

    Vector2f position = new Vector2f(0, 0),
        velocity = new Vector2f(0, 0),
        acceleration = new Vector2f(0, 0),
        mousePosition = new Vector2f(0, 0);


    List<List<Integer>> map;
    boolean up = false, down = false, left = false, right = false;
    boolean cameraUp = false, cameraDown = false, cameraLeft = false, cameraRight = false;

    @Override
    public void setup(Window window) {
        texture = Texture.builder().
            file("assets/tiles.png")
            .borderColor(Colors.white())
            .horizontalRepeat(Texture.Repeat.CLAMP_BORDER)
            .verticalRepeat(Texture.Repeat.CLAMP_BORDER)
            .build();
        regions = TextureRegion.split(texture, 18, 18);
        character = regions[5][0];
        plant = regions[10][6];
        camera = Camera3.builder2d()
            .size(window.getWidth(), window.getHeight())
            .position(window.getWidth() / 2, window.getHeight() / 2)
            .build();

        renderer2 = new ShapeRenderer2();

        map = new LinkedList<>();
        try {
            File file = new File("assets/level.txt");
            Scanner scanner = new Scanner(file);

            int row = 0;
            while (scanner.hasNextLine()) {
                map.add(new LinkedList<>());
                row++;
                String line = scanner.nextLine();
                String[] data = line.split(" ");
                for (String s : data) {
                    map.get(row - 1).add(Integer.parseInt(s));
                }
            }

            scanner.close();
        } catch (Exception e) {
            System.out.println("error reading level file");
            e.printStackTrace();
        }
    }

    @Override
    public void loop(float delta) {
        Utils.clear(Colors.black(), Utils.Buffer.COLOR_BUFFER);
        renderer2.view(camera.view());
        renderer2.projection(camera.projection());
        renderer2.texture_$eq(texture);

        Utils.enableBlending();

        acceleration.set(0);
        float acc = 2000;
        float maxSpeed = 300;
        float dragCoefficient = 0.05f;

        if (up) acceleration.y = -acc;
        if (down) acceleration.y = acc;
        if (left) acceleration.x = -acc;
        if (right) acceleration.x = acc;

        velocity
            .add(
                acceleration.x * delta,
                acceleration.y * delta
            )
            .add(
                new Vector2f(velocity).mul(-1).mul(dragCoefficient)
            )
        ;

        float intensity = velocity.length();
        if (intensity > maxSpeed) {
            velocity.normalize().mul(maxSpeed);
        }

        position.add(
            velocity.x * delta,
            velocity.y * delta
        );
        
        float a=0.2f;
        camera.position().set(new Vector2f()
                .add(new Vector2f(position).mul(1f-a))
                .add(new Vector2f(mousePosition).add(position).mul(a))
            , 0);
        camera.update();

        TextureRegion[] palette = new TextureRegion[]{
            new TextureRegion(texture, -1, -1, 0, 0),
            regions[2][2],
            regions[1][2],
            regions[3][2],
            regions[2][1],
        };

        renderer2.draw(new Square(
            0, 0, 800,
            Colors.blue400(),
            palette[0],
            new Matrix4f()
        ));

        for (int i = 0; i < map.get(0).size(); i++) {
            for (int j = 0; j < map.size(); j++) {
                int tileId = map.get(j).get(i);
                if (tileId == 0) continue;
                renderer2.draw(new Square(
                    i * tileSize,
                    j * tileSize,
                    tileSize,
                    Colors.white(),
                    palette[tileId],
                    new Matrix4f()
                ));
            }
        }

        renderer2.draw(new Square(
            (position.x),
            (position.y),
            tileSize,
            Colors.white(),
            character,
            new Matrix4f()
        ));
        renderer2.flush();

        Utils.disableBlending();
    }

    @Override
    public boolean keyDown(int key, int scancode, int mods) {
        if (key == Input.Keys.keyD()) right = true;
        if (key == Input.Keys.A()) left = true;
        if (key == Input.Keys.W()) up = true;
        if (key == Input.Keys.S()) down = true;

        if (key == GLFW.GLFW_KEY_RIGHT) cameraRight = true;
        if (key == GLFW.GLFW_KEY_LEFT) cameraLeft = true;
        if (key == GLFW.GLFW_KEY_UP) cameraUp = true;
        if (key == GLFW.GLFW_KEY_DOWN) cameraDown = true;

        return true;
    }

    @Override
    public boolean keyUp(int key, int scancode, int mods) {
        if (key == GLFW.GLFW_KEY_D) right = false;
        if (key == GLFW.GLFW_KEY_A) left = false;
        if (key == GLFW.GLFW_KEY_W) up = false;
        if (key == GLFW.GLFW_KEY_S) down = false;

        if (key == GLFW.GLFW_KEY_RIGHT) cameraRight = false;
        if (key == GLFW.GLFW_KEY_LEFT) cameraLeft = false;
        if (key == GLFW.GLFW_KEY_UP) cameraUp = false;
        if (key == GLFW.GLFW_KEY_DOWN) cameraDown = false;
        return true;
    }

    @Override
    public boolean mouseMove(double x, double y) {
        mousePosition.set(x-400, y-300);
        return true;
    }

    public static void main(String[] args) {
        new Main().run();
    }
}
