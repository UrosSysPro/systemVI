package com.systemvi.engine.renderers;

import com.systemvi.engine.camera.Camera;
import com.systemvi.engine.model.Mesh;
import com.systemvi.engine.model.VertexAttribute;
import com.systemvi.engine.shader.Shader;
import org.joml.*;

import java.lang.Math;

public class ShapeRenderer {
    private final Mesh mesh;
    private final Shader shader;
    private Shader customShader=null;
    private final Vector4f helperVector=new Vector4f();
    private final Matrix4f helperMatrix=new Matrix4f();
    private int pointsToDraw=0;
    private int trianglesToDraw=0;
    private final int vertexSize=6;
    private final float[] vertexData;
    private final int[] indices;
    int maxPoints;
    int maxTriangles;
    private Camera camera;

    public ShapeRenderer(int maxPoints,int maxTriangles){
        this.maxPoints=maxPoints;
        this.maxTriangles=maxTriangles;
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
    public void setShader(Shader shader){
        customShader=shader;
    }
    public ShapeRenderer(){
        this(1000,1000);
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
    public void rect(float x, float y, float width, float height, Vector4f color,float angle){
        if(pointsToDraw+4>maxPoints||trianglesToDraw+2>maxTriangles)flush();

        helperMatrix.identity().translate((x+width/2),(y+height/2),0).rotateZ(angle).translate(-(x+width/2),-(y+height/2),0);

        int pointsOffset=pointsToDraw;
        for(int j=0;j<2;j++){
            for(int i=0;i<2;i++){
                int index=pointsToDraw*vertexSize;
                helperVector.set(x+i*width,y+j*height,1);
                helperMatrix.transform(helperVector);
                vertexData[index+0]=helperVector.x;
                vertexData[index+1]=helperVector.y;
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
        if(pointsToDraw+points.length>maxPoints)flush();
        if(trianglesToDraw+pointsToDraw-2>maxTriangles)flush();
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

    public void polygon(Vector2f[] points, Vector4f color,Vector3f position,float angle){
        if(pointsToDraw+points.length>maxPoints)flush();
        if(trianglesToDraw+pointsToDraw-2>maxTriangles)flush();
        int pointsOffset=pointsToDraw;
        helperMatrix.identity().rotateZ(angle);
        for(int i=0;i<points.length;i++){
            int index=pointsToDraw*vertexSize;
            helperVector.set(points[i].x,points[i].y,0,1);
            vertexData[index+0]=helperVector.x+position.x;
            vertexData[index+1]=helperVector.y+position.y;
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

    public void regularPolygon(int n,float r,Vector2f position,Vector4f color,float angle){
        if(pointsToDraw+n>maxPoints)flush();
        if(trianglesToDraw+pointsToDraw-2>maxTriangles)flush();
        int pointsOffset=pointsToDraw;
        helperMatrix.identity().rotateZ(angle);
        for(int i=0;i<n;i++){
            int index=pointsToDraw*vertexSize;
            float a=(float)Math.toRadians(360f/n*i)+angle;
            float x=(float)(Math.cos(a))*r;
            float y=(float)(Math.sin(a))*r;
            helperVector.set(x,y,0,1);
            vertexData[index+0]=helperVector.x+position.x;
            vertexData[index+1]=helperVector.y+position.y;
            vertexData[index+2]=color.x;
            vertexData[index+3]=color.y;
            vertexData[index+4]=color.z;
            vertexData[index+5]=color.w;
            pointsToDraw++;
        }
        for(int i=0;i<n-2;i++){
            int index=trianglesToDraw*3;
            indices[index+0]=pointsOffset+0;
            indices[index+1]=pointsOffset+i+1;
            indices[index+2]=pointsOffset+i+2;
            trianglesToDraw++;
        }
    }
    public void line(Vector2f start, Vector2f end, float width, Vector4f color){
        float x=(start.x+end.x)/2;
        float y=(start.y+end.y)/2;
        float d=start.distance(end);
        x-=width/2;
        y-=d/2;
        float angle=(float)-Math.atan2(start.x-end.x,start.y-end.y);
        rect(x,y,width,d,color,angle);
    }
    public void flush(){
        Shader shader=customShader!=null?customShader:this.shader;
        shader.use();
        shader.setUniform("view",camera.getView());
        shader.setUniform("projection",camera.getProjection());
        mesh.setVertexData(vertexData);
        mesh.setIndices(indices);
        mesh.drawElements(trianglesToDraw);
        pointsToDraw=0;
        trianglesToDraw=0;
    }
}
