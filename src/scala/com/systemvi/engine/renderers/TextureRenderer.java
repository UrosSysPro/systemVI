package com.systemvi.engine.renderers;

import com.systemvi.engine.camera.Camera;
import com.systemvi.engine.model.Mesh;
import com.systemvi.engine.model.VertexAttribute;
import com.systemvi.engine.shader.Shader;
import com.systemvi.engine.texture.Texture;
import com.systemvi.engine.texture.TextureRegion;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class TextureRenderer {
    private final Mesh mesh;
    private final Shader shader;
    private Texture texture;
    private Shader customShader=null;
    private final Vector4f helperVector=new Vector4f();
    private final Matrix4f helperMatrix=new Matrix4f();
    private int pointsToDraw=0;
    private int trianglesToDraw=0;
    private final int vertexSize=4;
    private final float[] vertexData;
    private final int[] indices;
    int maxPoints;
    int maxTriangles;
//    private Camera camera;
    private Matrix4f view;
    private Matrix4f projection;

    public TextureRenderer(int maxPoints,int maxTriangles){
        this.maxPoints=maxPoints;
        this.maxTriangles=maxTriangles;
        mesh=new Mesh(
            new VertexAttribute("position",2),
            new VertexAttribute("texCoords",2)
        );
        shader= Shader.builder()
            .fragment("assets/renderer/textureRenderer/fragment.glsl")
            .vertex("assets/renderer/textureRenderer/vertex.glsl")
            .build();
        if(!shader.isCompiled()){
            System.out.println(shader.getLog());
        }
        vertexData =new float[vertexSize* maxPoints];
        indices=new int[3* maxTriangles];
    }
    public void setShader(Shader shader){
        customShader=shader;
    }
    public TextureRenderer(){
        this(1000,1000);
    }
    public void draw(Texture texutre,float x, float y, float width, float height){
        this.texture=texutre;
        if(pointsToDraw+4>maxPoints||trianglesToDraw+2>maxTriangles)flush();

        int pointsOffset=pointsToDraw;
        for(int j=0;j<2;j++){
            for(int i=0;i<2;i++){
                int index=pointsToDraw*vertexSize;
                vertexData[index+0]=x+i*width;
                vertexData[index+1]=y+j*height;
                vertexData[index+2]=i;
                vertexData[index+3]=j;
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
    public void draw(Texture texture,float x, float y, float width, float height, float angle){
        this.texture=texture;
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
                vertexData[index+2]=i;
                vertexData[index+3]=j;
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
    public void draw(TextureRegion region,int x,int y,int width,int height){
        this.texture=region.getTexture();
        if(pointsToDraw+4>maxPoints||trianglesToDraw+2>maxTriangles)flush();

        int pointsOffset=pointsToDraw;
        for(int j=0;j<2;j++){
            for(int i=0;i<2;i++){
                int index=pointsToDraw*vertexSize;
                vertexData[index+0]=x+i*width;
                vertexData[index+1]=y+j*height;
                vertexData[index+2]=i==0?region.getLeft():region.getRight();
                vertexData[index+3]=j==0?region.getTop():region.getBottom();
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
    public void draw(TextureRegion region,int x,int y,int width,int height,float angle){
        this.texture=region.getTexture();
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
                vertexData[index+2]=i==0?region.getLeft():region.getRight();
                vertexData[index+3]=j==0?region.getTop():region.getBottom();
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
        view=camera.getView();
        projection=camera.getProjection();
    }
    public Matrix4f view(){return view;}
    public Matrix4f projection(){return projection;}
    public TextureRenderer projection(Matrix4f projection){this.projection=projection;return this;}
    public TextureRenderer view(Matrix4f view){this.view=view;return this;}

    public void flush(){
        Shader shader=customShader!=null?customShader:this.shader;
        texture.bind(0);
        shader.use();
        shader.setUniform("t0",0);
        shader.setUniform("view",view);
        shader.setUniform("projection",projection);
        mesh.setVertexData(vertexData);
        mesh.setIndices(indices);
        mesh.drawElements(trianglesToDraw);
        pointsToDraw=0;
        trianglesToDraw=0;
    }
}
