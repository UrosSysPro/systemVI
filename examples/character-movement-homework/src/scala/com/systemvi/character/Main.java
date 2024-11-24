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

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Main extends Game {
    
    public Main(){
        super(3,3,60,800,600,"Character");
    }

    Shape2Renderer2 renderer2;
    Camera3 camera;
    Texture texture;
    TextureRegion[][] regions;
    TextureRegion character,plant;

    int tileSize=40;

    List<List<Integer>> map;

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

        map=new LinkedList<>();
        try{
            File file=new File("assets/level.txt");
            Scanner scanner=new Scanner(file);

            int row=0;
            while(scanner.hasNextLine()){
                map.add(new LinkedList<>());
                row++;
                String line=scanner.nextLine();
                String[] data=line.split(" ");
                for(String s:data){
                    map.get(row-1).add(Integer.parseInt(s));
                }
            }

            scanner.close();
        }catch (Exception e){
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

        TextureRegion[] palette=new TextureRegion[]{
                regions[5][3],
                regions[2][2],
                regions[1][2],
                regions[3][2],
                regions[2][1],
        };

        for(int i=0;i<map.get(0).size();i++){
            for(int j=0;j<map.size();j++){
                int tileId=map.get(j).get(i);
                if(tileId==0)continue;
                renderer2.draw(new Square(
                        i*tileSize,
                        j*tileSize,
                        tileSize,
                        Colors.white(),
                        palette[tileId],
                        new Matrix4f()
                ));
            }
        }

        renderer2.draw(new Square(
                100,
                100,
                tileSize,
                Colors.gray50(),
                character,
                new Matrix4f()
        ));
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
