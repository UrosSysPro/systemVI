package com.systemvi.engine.renderers;

import com.systemvi.engine.camera.Camera;
import com.systemvi.engine.model.Mesh;
import com.systemvi.engine.model.VertexAttribute;
import com.systemvi.engine.shader.Shader;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class ShapeRenderer {
    private Mesh mesh;
    private Shader shader;
    private int pointsToDraw=0;
    private int maxPoints=1000,vertexSize=6;
    private float[] verexData;
    private Camera camera;

    public ShapeRenderer(){
        mesh=new Mesh(
            new VertexAttribute("position",2),
            new VertexAttribute("color",4)
        );
        shader=new Shader(
            "assets/renderer/shapeRenderer/vertex.glsl",
            "assets/renderer/shapeRenderer/fragment.glsl"
        );
        if(!shader.isCompiled()){
            System.out.println(shader.getLog());
        }
        verexData=new float[vertexSize*maxPoints];
    }
    public void rect(float x, float y, float width, float height, Vector4f color){
        if(pointsToDraw+6>=maxPoints)flush();
        //prva tacka
        int index=pointsToDraw*vertexSize;
        verexData[index+0]=x;
        verexData[index+1]=y;
        verexData[index+2]=color.x;
        verexData[index+3]=color.y;
        verexData[index+4]=color.z;
        verexData[index+5]=color.w;
        //druga tacka
        pointsToDraw++;
        index+=vertexSize;
        verexData[index+0]=x+width;
        verexData[index+1]=y;
        verexData[index+2]=color.x;
        verexData[index+3]=color.y;
        verexData[index+4]=color.z;
        verexData[index+5]=color.w;
        //treca tacka
        pointsToDraw++;
        index+=vertexSize;
        verexData[index+0]=x;
        verexData[index+1]=y+height;
        verexData[index+2]=color.x;
        verexData[index+3]=color.y;
        verexData[index+4]=color.z;
        verexData[index+5]=color.w;
        //cetvrta tacka
        pointsToDraw++;
        index+=vertexSize;
        verexData[index+0]=x+width;
        verexData[index+1]=y;
        verexData[index+2]=color.x;
        verexData[index+3]=color.y;
        verexData[index+4]=color.z;
        verexData[index+5]=color.w;
        //peta tacka
        pointsToDraw++;
        index+=vertexSize;
        verexData[index+0]=x;
        verexData[index+1]=y+height;
        verexData[index+2]=color.x;
        verexData[index+3]=color.y;
        verexData[index+4]=color.z;
        verexData[index+5]=color.w;
        //sesta tacka
        pointsToDraw++;
        index+=vertexSize;
        verexData[index+0]=x+width;
        verexData[index+1]=y+height;
        verexData[index+2]=color.x;
        verexData[index+3]=color.y;
        verexData[index+4]=color.z;
        verexData[index+5]=color.w;
        pointsToDraw++;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public void polygon(Vector2f[] points, Vector4f color){
        for(int i=0;i<points.length-2;i++){
            int index=pointsToDraw*vertexSize;
            verexData[index+0]=points[0].x;
            verexData[index+1]=points[0].y;
            verexData[index+2]=color.x;
            verexData[index+3]=color.y;
            verexData[index+4]=color.z;
            verexData[index+5]=color.w;
            pointsToDraw++;

            index+=vertexSize;
            verexData[index+0]=points[i+1].x;
            verexData[index+1]=points[i+1].y;
            verexData[index+2]=color.x;
            verexData[index+3]=color.y;
            verexData[index+4]=color.z;
            verexData[index+5]=color.w;
            pointsToDraw++;

            index+=vertexSize;
            verexData[index+0]=points[i+2].x;
            verexData[index+1]=points[i+2].y;
            verexData[index+2]=color.x;
            verexData[index+3]=color.y;
            verexData[index+4]=color.z;
            verexData[index+5]=color.w;
            pointsToDraw++;
        }
    }
    public void flush(){
        shader.use();
        shader.setUniform("view",camera.getView());
        shader.setUniform("projection",camera.getProjection());
        mesh.setVertexData(verexData);
        mesh.draw(pointsToDraw*vertexSize);
        pointsToDraw=0;
    }
}
