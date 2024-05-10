package com.systemvi.engine.model;

import org.lwjgl.assimp.*;

import java.util.Arrays;
import java.util.Objects;

import static org.lwjgl.assimp.Assimp.*;


public class ModelLoader {
    public static Model load(ModelLoaderParams params){

        try(AIScene aiScene = aiImportFile(params.fileName,0)){
            if(aiScene == null){
                throw new RuntimeException("Failed to load model");
            }
            int meshCount = aiScene.mNumMeshes();
            for(int i = 0; i < meshCount; i++){
                AIMesh mesh=AIMesh.create(Objects.requireNonNull(aiScene.mMeshes()).get(i));
                int vertexCount=mesh.mNumVertices();
                AIVector3D.Buffer vertices=mesh.mVertices();
                AIVector3D.Buffer normals=mesh.mNormals();
                AIVector3D.Buffer tangents=mesh.mTangents();
                AIVector3D.Buffer bitangents=mesh.mBitangents();

                for(int j = 0; j < vertexCount; j++){
                    AIVector3D position=vertices.get(j);
                    System.out.printf("%f %f %f\n", position.x(),position.y(),position.z());
                }
            }


            return new Model();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
