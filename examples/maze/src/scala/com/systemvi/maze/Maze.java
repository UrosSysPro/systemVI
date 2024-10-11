package com.systemvi.maze;

import com.systemvi.engine.application.Application;
import com.systemvi.engine.application.Game;
import com.systemvi.engine.camera.Camera;
import com.systemvi.engine.renderers.ShapeRenderer;
import com.systemvi.engine.renderers.TextureRenderer;
import com.systemvi.engine.texture.Texture;
import com.systemvi.engine.texture.TextureRegion;
import com.systemvi.engine.window.Window;

import static org.lwjgl.opengl.GL33.*;

public class Maze extends Game {

    public Maze() {
        super(3,3,60,800,600,"Maze");
    }

    public ShapeRenderer renderer;
    public TextureRenderer textureRenderer;
    public Texture texture;
    public TextureRegion[][] sprites;
    public Camera camera;
    public Map map;
    public int cellSize;
    public TextureRegion[][] spriteMap;

    @Override
    public void setup(Window window) {

        camera=new Camera();
        camera.setScreenSize(800,600);
        camera.setScale(1,-1,1);
        camera.setPosition(400,300,0);
        camera.update();

        renderer=new ShapeRenderer();
        renderer.setCamera(camera);

        texture=new Texture("assets/tiles.png");
        sprites=TextureRegion.split(texture,18,18);
        textureRenderer=new TextureRenderer();
        textureRenderer.setCamera(camera);

        cellSize=40;
        map=new Map(800/cellSize,600/cellSize);
        spriteMap=new TextureRegion[map.width][map.height];
        System.out.println(sprites[13][1]);
        for(int i=0;i<spriteMap.length;i++){
            for(int j=0;j<spriteMap[i].length;j++){
                boolean up= j - 1 >= 0 && map.mat[i][j - 1];
                boolean down=j+1<map.height&&map.mat[i][j+1];
                boolean left=i-1>=0&&map.mat[i-1][j];
                boolean right=i+1<map.width&&map.mat[i+1][j];

                if(!map.mat[i][j]){continue;}
                //sve
                if( up&& down&& left&& right){spriteMap[i][j]=sprites[14][1];}
                //gore dole levo desno
                if(!up&& down&& left&& right){spriteMap[i][j]=sprites[14][0];}
                if( up&&!down&& left&& right){spriteMap[i][j]=sprites[14][2];}
                if( up&& down&&!left&& right){spriteMap[i][j]=sprites[13][1];}
                if(up&&down&&left&&!right){spriteMap[i][j]=sprites[15][1];}
                //coskovi
                if(!up&&down&&!left&&right){spriteMap[i][j]=sprites[13][0];}
                if(!up&&down&&left&&!right){spriteMap[i][j]=sprites[15][0];}
                if(up&&!down&&!left&&right){spriteMap[i][j]=sprites[13][2];}
                if(up&&!down&&left&&!right){spriteMap[i][j]=sprites[15][2];}
                //po tri
                if(!up&&down&&!left&&!right){spriteMap[i][j]=sprites[12][1];}
                if(up&&!down&&!left&&!right){spriteMap[i][j]=sprites[12][3];}
                if(!up&&!down&&!left&&right){spriteMap[i][j]=sprites[13][3];}
                if(!up&&!down&&left&&!right){spriteMap[i][j]=sprites[15][3];}
                //po dva
                if(!up&&!down&&left&&right){spriteMap[i][j]=sprites[14][3];}
                if(up&&down&&!left&&!right){spriteMap[i][j]=sprites[12][2];}
            }
        }
    }

    @Override
    public void loop(float delta) {

        glClearColor(0.3f,0.5f,0.9f,1);
        glClear(GL_COLOR_BUFFER_BIT);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA,GL_ONE_MINUS_SRC_ALPHA);
        for(int i=0;i<map.width;i++){
            for(int j=0;j<map.height;j++){
                if(spriteMap[i][j]!=null){
                    textureRenderer.draw(spriteMap[i][j],i*cellSize,j*cellSize,cellSize,cellSize);
                }
            }
        }
        textureRenderer.flush();
        glDisable(GL_BLEND);
    }
}
