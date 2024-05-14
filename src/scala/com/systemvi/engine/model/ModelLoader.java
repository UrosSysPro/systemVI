package com.systemvi.engine.model;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import java.nio.IntBuffer;
import java.util.ArrayList;

import static org.lwjgl.assimp.Assimp.*;

public class ModelLoader {
    public static Model load(ModelLoaderParams params){
        Model model = null;
        try{
            AIScene aiScene = aiImportFile(params.fileName,params.flags);
            if(aiScene == null){
                System.out.println("[ERROR] failed to load model");
                return model;
            }
            ArrayList<Model.Material> materials=loadMaterials(aiScene);

            ArrayList<Model.Mesh> meshes = loadMeshes(aiScene,materials);

            Model.Node root=loadNodes(aiScene,meshes);

            model = new Model(meshes,materials,root);
        }catch (Exception e){
            e.printStackTrace();
        }

        return model;
    }

    public static void printNodes(AINode root,String prefix){
        if(root==null){
            System.out.println(prefix+"node end null");
            return;
        }
        AIString aiString=root.mName();
        String name=aiString.dataString();
        System.out.println(prefix+"node: "+name);

        PointerBuffer nodeBuffer=root.mChildren();
        if(nodeBuffer==null){
            System.out.println(prefix+"node end no children");
            return;
        }
        while (nodeBuffer.remaining()>0){
            AINode node=AINode.create(nodeBuffer.get());
            printNodes(node,prefix+"\t");
        }
    }

    private static ArrayList<Model.Mesh> loadMeshes(AIScene aiScene,ArrayList<Model.Material> materials){
        ArrayList<Model.Mesh> meshes=new ArrayList<>();
        PointerBuffer meshesBuffer=aiScene.mMeshes();
        int meshCount=aiScene.mNumMeshes();
        for(int i=0; i < meshCount; i++){
            ArrayList<Model.Vertex> vertices=new ArrayList<>();
            AIMesh aiMesh=AIMesh.create(meshesBuffer.get(i));

            String name=aiMesh.mName().dataString();

            int numVertices=aiMesh.mNumVertices();

            for(int j=0; j<numVertices; j++){
                AIVector3D position=aiMesh.mVertices().get(j);
                AIVector3D normal=aiMesh.mNormals().get(j);
                AIVector3D tangent=aiMesh.mTangents().get(j);
                AIVector3D bitangent=aiMesh.mBitangents().get(j);
                ArrayList<Vector3f> texCoords=new ArrayList<>();
                ArrayList<Vector4f> colors=new ArrayList<>();

                AIVector3D.Buffer texCoordsBuffer=aiMesh.mTextureCoords(i);
                AIColor4D.Buffer colorsBuffer=aiMesh.mColors(i);

                if(texCoordsBuffer!=null) {
                    for (int k = 0; k < texCoordsBuffer.sizeof(); k++) {
                        AIVector3D texCoord = texCoordsBuffer.get(k);
                        System.out.printf("vertex: %d id: %d %f %f %f\n", i, j, texCoord.x(), texCoord.y(), texCoord.z());
                        texCoords.add(new Vector3f(texCoord.x(), texCoord.y(), texCoord.z()));
                    }
                }

                if(colorsBuffer!=null){
                    for(int k=0;k<colorsBuffer.sizeof();k++){
                        AIColor4D color=colorsBuffer.get(k);
                        System.out.printf("vertex: %d id:  %d %f %f %f\n",i,j,color.r(),color.g(),color.b());
                        colors.add(new Vector4f(color.r(),color.g(),color.b(),color.a()));
                    }
                }

                vertices.add(new Model.Vertex(
                    new Vector3f(position.x(),position.y(),position.z()),
                    new Vector3f(normal.x(),normal.y(),normal.z()),
                    new Vector3f(tangent.x(),tangent.y(),tangent.z()),
                    new Vector3f(bitangent.x(),bitangent.y(),bitangent.z()),
                    texCoords,
                    colors
                ));
            }

            int materialIndex=aiMesh.mMaterialIndex();
            Model.Material material=materials.get(materialIndex);

            meshes.add(new Model.Mesh(name,vertices,material,materialIndex));
        }
        return meshes;
    }

    private static ArrayList<Model.Material> loadMaterials(AIScene aiScene){
        ArrayList<Model.Material> materials=new ArrayList<>();
        int materialCount=aiScene.mNumMaterials();
        for (int i=0;i<materialCount;i++){
            int result;
            AIColor4D aiColor = AIColor4D.create();
            AIString aiString=AIString.calloc();
            AIMaterial aiMaterial=AIMaterial.create(aiScene.mMaterials().get(i));

            aiGetMaterialTexture(aiMaterial,aiTextureType_SPECULAR,0,aiString,(IntBuffer) null,null,null,null,null,null);
            String specularMapFile=aiString.dataString();
//            System.out.println(specularMapFile.isEmpty() ?"specular ne postoji":specularMapFile);

            aiGetMaterialTexture(aiMaterial,aiTextureType_DIFFUSE,0,aiString,(IntBuffer) null,null,null,null,null,null);
            String diffuseMapFile=aiString.dataString();
//            System.out.println(diffuseMapFile.isEmpty() ?"diffuse ne postoji":diffuseMapFile);

            aiGetMaterialTexture(aiMaterial,aiTextureType_METALNESS,0,aiString,(IntBuffer) null,null,null,null,null,null);
            String metalnessMapFile=aiString.dataString();
//            System.out.println(metalnessMapFile.isEmpty() ?"metalness ne postoji":metalnessMapFile);

            aiGetMaterialTexture(aiMaterial,aiTextureType_DISPLACEMENT,0,aiString,(IntBuffer) null,null,null,null,null,null);
            String displacementMapFile=aiString.dataString();
//            System.out.println(displacementMapFile.isEmpty() ?"displacement ne postoji":displacementMapFile);

            aiGetMaterialTexture(aiMaterial,aiTextureType_DIFFUSE_ROUGHNESS,0,aiString,(IntBuffer) null,null,null,null,null,null);
            String roughnessMapFile=aiString.dataString();
//            System.out.println(roughnessMapFile.isEmpty() ?"roughness ne postoji":roughnessMapFile);

            aiGetMaterialTexture(aiMaterial,aiTextureType_NORMALS,0,aiString,(IntBuffer) null,null,null,null,null,null);
            String normalMapFile=aiString.dataString();
//            System.out.println(normalMapFile.isEmpty() ?"normals ne postoji":normalMapFile);

            result=aiGetMaterialColor(aiMaterial,AI_MATKEY_COLOR_AMBIENT,aiTextureType_NONE,0,aiColor);
            Vector4f ambient=new Vector4f(0.1f,0.1f,0.1f,1.0f);
            if(result==0)ambient.set(aiColor.r(),aiColor.g(),aiColor.b(),aiColor.a());

            result=aiGetMaterialColor(aiMaterial,AI_MATKEY_COLOR_DIFFUSE,aiTextureType_NONE,0,aiColor);
            Vector4f diffuse=new Vector4f(0.1f,0.1f,0.1f,1.0f);
            if(result==0)diffuse.set(aiColor.r(),aiColor.g(),aiColor.b(),aiColor.a());

            result=aiGetMaterialColor(aiMaterial,AI_MATKEY_COLOR_SPECULAR,aiTextureType_NONE,0,aiColor);
            Vector4f specular=new Vector4f(0.1f,0.1f,0.1f,1.0f);
            if(result==0)specular.set(aiColor.r(),aiColor.g(),aiColor.b(),aiColor.a());

            materials.add(new Model.Material(
                ambient,diffuse,specular
            ));
        }
        return materials;
    }

    private static Model.Node loadNodes(AIScene aiScene,ArrayList<Model.Mesh> meshes){
        return loadNode(aiScene.mRootNode(),meshes);
    }

    private static Model.Node loadNode(AINode aiNode,ArrayList<Model.Mesh> modelMeshes){
        if(aiNode==null)return null;
        //name
        AIString aiString=aiNode.mName();
        String name = aiString.dataString();
        //transform
        AIMatrix4x4 t=aiNode.mTransformation();
        Matrix4f transform=new Matrix4f(
            t.a1(),t.a2(),t.a3(),t.a4(),
            t.b1(),t.b2(),t.b3(),t.b4(),
            t.c1(),t.c2(),t.c3(),t.c4(),
            t.d1(),t.d2(),t.d3(),t.d4()
        );
//        Matrix4f transform=new Matrix4f(
//            t.a1(),t.b1(),t.c1(),t.d1(),
//            t.a2(),t.b2(),t.c2(),t.d2(),
//            t.a3(),t.b3(),t.c3(),t.d3(),
//            t.a4(),t.b4(),t.c4(),t.d4()
//        );
        //meshes
        IntBuffer meshIndicesBuffer=aiNode.mMeshes();
        ArrayList<Integer> meshIndices=new ArrayList<>();
        while(meshIndicesBuffer.remaining()>0){
            meshIndices.add(meshIndicesBuffer.get());
        }
        ArrayList<Model.Mesh> meshes=new ArrayList<>();
        for (Integer meshIndex : meshIndices) {
            meshes.add(modelMeshes.get(meshIndex));
        }
        //children
        ArrayList<Model.Node> children=new ArrayList<>();
        PointerBuffer childrenBuffer=aiNode.mChildren();
        if(childrenBuffer!=null){
            AINode aiChildNode=AINode.create(childrenBuffer.get());
            children.add(loadNode(aiChildNode,modelMeshes));
        }
        return new Model.Node(name,children,meshIndices,meshes,transform);
    }
}
