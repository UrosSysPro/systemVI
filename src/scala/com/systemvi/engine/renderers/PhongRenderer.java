package com.systemvi.engine.renderers;

import com.systemvi.engine.buffer.ArrayBuffer;
import com.systemvi.engine.buffer.ElementsBuffer;
import com.systemvi.engine.buffer.VertexArray;
import com.systemvi.engine.camera.Camera3;
import com.systemvi.engine.model.Model;
import com.systemvi.engine.model.VertexAttribute;
import com.systemvi.engine.shader.ElementsDataType;
import com.systemvi.engine.shader.Primitive;
import com.systemvi.engine.shader.Shader;
import com.systemvi.engine.texture.Format;
import com.systemvi.engine.texture.Texture;
import com.systemvi.engine.texture.TextureData;
import com.systemvi.engine.utils.Utils;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;

import static com.systemvi.engine.model.Model.*;

public class PhongRenderer {
    private static class MeshGpuData{
        private final ArrayBuffer vertexBuffer;
        private final VertexArray vertexArray;
        private final ElementsBuffer elementsBuffer;
        public MeshGpuData(Mesh mesh){
            vertexArray = new VertexArray();
            vertexBuffer = new ArrayBuffer();
            elementsBuffer = new ElementsBuffer();
            sendToGpu(mesh);
        }
        public void sendToGpu(Mesh mesh){
            vertexArray.bind();
            vertexBuffer.bind();
            elementsBuffer.bind();
            ArrayList<Vertex> vertices=mesh.vertices;
            ArrayList<Face> faces=mesh.faces;

            int vertexSize=18;
            float[] vertexData=new float[vertices.size()*vertexSize];
            for(int i=0;i<vertices.size();i++){
                Vertex vertex=vertices.get(i);
                vertexData[i*vertexSize]=vertex.position.x;
                vertexData[i*vertexSize+1]=vertex.position.y;
                vertexData[i*vertexSize+2]=vertex.position.z;

                vertexData[i*vertexSize+3]=vertex.tangent.x;
                vertexData[i*vertexSize+4]=vertex.tangent.y;
                vertexData[i*vertexSize+5]=vertex.tangent.z;

                vertexData[i*vertexSize+6]=vertex.bitangent.x;
                vertexData[i*vertexSize+7]=vertex.bitangent.y;
                vertexData[i*vertexSize+8]=vertex.bitangent.z;

                vertexData[i*vertexSize+9]=vertex.normal.x;
                vertexData[i*vertexSize+10]=vertex.normal.y;
                vertexData[i*vertexSize+11]=vertex.normal.z;

                Vector3f texCoords = !vertex.texCoords.isEmpty() ? vertex.texCoords.get(0):new Vector3f(0.5f);
                vertexData[i*vertexSize+12]=texCoords.x;
                vertexData[i*vertexSize+13]=texCoords.y;

                Vector4f color=!vertex.colors.isEmpty()? vertex.colors.get(0):new Vector4f(1);
                vertexData[i*vertexSize+14]=color.x;
                vertexData[i*vertexSize+15]=color.y;
                vertexData[i*vertexSize+16]=color.z;
                vertexData[i*vertexSize+17]=color.w;
            }
            vertexBuffer.setData(vertexData);
            vertexBuffer.setVertexAttributes(new VertexAttribute[]{
                new VertexAttribute("position",3),
                new VertexAttribute("tangent",3),
                new VertexAttribute("bitangent",3),
                new VertexAttribute("normal",3),
                new VertexAttribute("texCoords",2),
                new VertexAttribute("color",4)
            });

            int elementsPerFace=3;
            int[] elementData=new int[faces.size()*elementsPerFace];
            for(int i=0;i<faces.size();i++){
                Face face=faces.get(i);
                elementData[i*elementsPerFace+0]=face.indices[0];
                elementData[i*elementsPerFace+1]=face.indices[1];
                elementData[i*elementsPerFace+2]=face.indices[2];
            }
            elementsBuffer.setData(elementData);

            vertexArray.unbind();
        }
        public void bind(){
            vertexArray.bind();
        }
        public void unbind(){
            vertexArray.unbind();
        }
        public void delete(){
            vertexBuffer.delete();
            vertexArray.delete();
        }
    }

    private static class MaterialGpuData{
        public final Texture diffuseTexture,specularTexture,normalTexture;
        public final String texturesFolder;
        public MaterialGpuData(Material material,String texturesFolder){
            this.texturesFolder=texturesFolder;
            TextureData data=new TextureData(1,1, Format.RGBA);
            if(material.diffuseFile!=null&&!material.diffuseFile.isEmpty()){
                diffuseTexture=new Texture(texturesFolder+"/"+material.diffuseFile);
            }else{
                data.setPixel4f(0,0,new Vector4f(1));
                diffuseTexture=new Texture(1,1,Format.RGBA);
                diffuseTexture.setData(data);
            }
            if(material.specularFile!=null&&!material.specularFile.isEmpty()){
                specularTexture=new Texture(texturesFolder+"/"+material.specularFile);
            }else{
                data.setPixel4f(0,0,new Vector4f(1));
                specularTexture=new Texture(1,1,Format.RGBA);
                specularTexture.setData(data);
            }
            if(material.normalFile!=null&&!material.normalFile.isEmpty()){
                normalTexture=new Texture(texturesFolder+"/"+material.normalFile);
            }else{
                data.setPixel4f(0,0,new Vector4f(0.5f,0.5f,1.0f,1.0f));
                normalTexture=new Texture(1,1,Format.RGBA);
                normalTexture.setData(data);
            }
        }
        public void delete(){
            diffuseTexture.delete();
            specularTexture.delete();
            normalTexture.delete();
        }
    }

    public static class Builder{
        private Camera3 camera=null;
        private Model model=null;
        private String texturesFolder="";
        public Builder camera(Camera3 camera){
            this.camera=camera;
            return this;
        }
        public Builder model(Model model){
            this.model=model;
            return this;
        }
        public Builder texturesFolder(String texturesFolder){
            this.texturesFolder=texturesFolder;
            return this;
        }
        public PhongRenderer build(){
            return new PhongRenderer(model,camera,texturesFolder);
        }
    }
    public static Builder builder(){
        return new Builder();
    }

    private ArrayList<MeshGpuData> meshGpuData;
    private ArrayList<MaterialGpuData> materialGpuData;
    private Shader shader;
    private Model model;
    private Camera3 camera;
    private String texturesFolder;

    public PhongRenderer(Model model,Camera3 camera,String texturesFolder){
        this.model = model;
        this.camera = camera;
        this.texturesFolder=texturesFolder;

        meshGpuData=new ArrayList<>();
        materialGpuData=new ArrayList<>();

        if(model!=null) setModel(model);

        shader = Shader.builder()
            .vertex("assets/renderer/phongRenderer/vertex.glsl")
            .fragment("assets/renderer/phongRenderer/fragment.glsl")
            .build();
    }

    public void setModel(Model model){
        deleteMeshGpuData();
        meshGpuData=new ArrayList<>();
        for(Model.Mesh mesh:model.meshes){
            meshGpuData.add(new MeshGpuData(mesh));
        }
        for(Material material:model.materials){
            materialGpuData.add(new MaterialGpuData(material,texturesFolder));
        }
    }

    private void deleteMeshGpuData(){
        for(MeshGpuData meshGpuData:meshGpuData){
            meshGpuData.delete();
        }
        meshGpuData.clear();
    }

    public void render(Matrix4f transform){
        if(camera==null){
            System.out.println("[ERROR] PhongRenderer: Camera can't be null!");
            return;
        }
        if(model==null){
            System.out.println("[ERROR] PhongRenderer: Model can't be null!");
            return;
        }
        Utils.enableDepthTest();
        Utils.enableFaceCulling(Utils.Face.BACK);
        render(transform,new Matrix4f(),model.root);
        Utils.disableDepthTest();
        Utils.disableFaceCulling();
    }

    private void render(Matrix4f transform,Matrix4f model,Model.Node node){
        Matrix4f nodeTransform=new Matrix4f().set(node.transform);
//        System.out.println(node.name);
        model.mul(nodeTransform);
        for(int i=0;i<node.meshes.size();i++){
            int meshIndex=node.meshIndices.get(i);
            Model.Mesh mesh=node.meshes.get(i);
            Material material=this.model.materials.get(mesh.materialIndex);
            MaterialGpuData materialGpuData=this.materialGpuData.get(mesh.materialIndex);
            MeshGpuData meshGpuData=this.meshGpuData.get(meshIndex);
            meshGpuData.bind();
            shader.use();
            materialGpuData.diffuseTexture.bind(0);
            shader.setUniform("diffuseTexture",0);
            shader.setUniform("materialDiffuse",material.diffuse);
            shader.setUniform("model", model);
            shader.setUniform("transform",transform);
            shader.setUniform("view",camera.view());
            shader.setUniform("projection",camera.projection());
            shader.drawElements(Primitive.TRIANGLES,mesh.faces.size(), ElementsDataType.UNSIGNED_INT,3);
        }

        for(Model.Node child:node.children){
            render(transform,model,child);
        }
        model.mul(nodeTransform.invertAffine());

    }
}
