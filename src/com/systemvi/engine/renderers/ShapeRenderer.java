package com.systemvi.engine.renderers;

import com.systemvi.engine.camera.Camera;
import com.systemvi.engine.model.Mesh;
import com.systemvi.engine.model.VertexAttribute;
import com.systemvi.engine.shader.Shader;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class ShapeRenderer {
    private final Mesh mesh;
    private final Shader shader;
    private int pointsToDraw=0;
    private int trianglesToDraw=0;
    private final int vertexSize=6;
    private final float[] vertexData;
    private final int[] indices;
    int maxPoints = 1000;
    int maxTriangles = 1000;
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
        vertexData =new float[vertexSize* maxPoints];
        indices=new int[3* maxTriangles];
    }
    public void rect(float x, float y, float width, float height, Vector4f color){
        if(pointsToDraw+4>maxPoints||trianglesToDraw+2>maxTriangles)flush();

        int pointsOffset=pointsToDraw;
        for(int j=0;j<2;j++){
            for(int i=0;i<2;i++){
                int index=pointsToDraw*vertexSize;
                vertexData[index+0]=x+i*width;
                vertexData[index+1]=y+j*height;
                vertexData[index+2]=color.x;
                vertexData[index+3]=color.y;
                vertexData[index+4]=color.z;
                vertexData[index+5]=color.w;
                pointsToDraw++;
            }
        }
        int index=trianglesToDraw*3;
        indices[index+0]= (pointsOffset+0);
        indices[index+1]= (pointsOffset+1);
        indices[index+2]= (pointsOffset+2);
        indices[index+3]= (pointsOffset+1);
        indices[index+4]= (pointsOffset+2);
        indices[index+5]= (pointsOffset+3);
        trianglesToDraw+=2;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public void polygon(Vector2f[] points, Vector4f color){
        int pointsOffset=pointsToDraw;
        for(int i=0;i<points.length;i++){
            int index=pointsToDraw*vertexSize;
            vertexData[index+0]=points[i].x;
            vertexData[index+1]=points[i].y;
            vertexData[index+2]=color.x;
            vertexData[index+3]=color.y;
            vertexData[index+4]=color.z;
            vertexData[index+5]=color.w;
            pointsToDraw++;
        }
        for(int i=0;i<points.length-2;i++){
            int index=trianglesToDraw*3;
            indices[index+0]=pointsOffset+0;
            indices[index+1]=pointsOffset+i+1;
            indices[index+2]=pointsOffset+i+2;
            trianglesToDraw++;
        }
    }
    public void flush(){
        shader.use();
        shader.setUniform("view",camera.getView());
        shader.setUniform("projection",camera.getProjection());
        mesh.setVertexData(vertexData);
        mesh.setIndices(indices);
        mesh.drawElements(pointsToDraw,trianglesToDraw);
        pointsToDraw=0;
        trianglesToDraw=0;
    }
}
