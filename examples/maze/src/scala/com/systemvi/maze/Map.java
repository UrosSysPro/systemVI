package com.systemvi.maze;

import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class Map {
    public boolean[][] mat;
    public int width,height;

    public Map(int width,int height){
        this.width=width;
        this.height=height;
        mat=new boolean[width][height];
        generate();
    }
    public void generate(){

        Random random=new Random();
        //postaviti sva polja na true (sve je zid)
        for(int i=0;i<width;i++){
            for(int j=0;j<height;j++){
                mat[i][j]=true;
            }
        }
        //napraviti stek koji cuva pozicije Point(x,y)
        Stack<Vector2i> stack=new Stack<>();
        //dodati prvu tacku u stek
        mat[0][0]=false;
        stack.push(new Vector2i(0,0));
        //while(stek nije prazan)
        while(!stack.isEmpty()){
            Vector2i p=stack.peek();
            //proverimo da li ima mesta u stranu da se ide
            ArrayList<Vector2i> list=new ArrayList<>();
            if(p.x-2>=0 && mat[p.x-2][p.y])list.add(new Vector2i(p.x-2,p.y));
            if(p.x+2<width && mat[p.x+2][p.y])list.add(new Vector2i(p.x+2,p.y));
            if(p.y-2>=0 && mat[p.x][p.y-2])list.add(new Vector2i(p.x,p.y-2));
            if(p.y+2<height && mat[p.x][p.y+2])list.add(new Vector2i(p.x,p.y+2));
            if(list.isEmpty()){
                //vrati se nazad
                stack.pop();
            }else{
                //idi u random smeru
                Vector2i p2=list.get(random.nextInt(list.size()));
                mat[p2.x][p2.y]=false;
                mat[(p.x+p2.x)/2][(p.y+p2.y)/2]=false;
                stack.push(p2);
            }
        }
    }
}
