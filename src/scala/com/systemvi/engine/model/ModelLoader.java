package com.systemvi.engine.model;

import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import java.io.File;

import static org.lwjgl.assimp.Assimp.*;

public class ModelLoader {
    public static Model load(ModelLoaderParams params){
        Model model = null;
        File file=new File(params.fileName);
        System.out.println(file.exists());
        try{
            AIScene aiScene = aiImportFile(params.fileName,aiProcess_JoinIdenticalVertices | aiProcess_Triangulate | aiProcess_FixInfacingNormals);
            if(aiScene == null){
                System.out.println("[ERROR] failed to load model");
                return model;
            }
            System.out.println("aiScene");
            PointerBuffer meshesBuffer=aiScene.mMeshes();
            int meshCount=aiScene.mNumMeshes();
            for(int i=0; i<meshCount; i++){
                AIMesh aiMesh=AIMesh.create(meshesBuffer.get(i));
                System.out.println("\taiMesh");
                AIVector3D.Buffer positions=aiMesh.mVertices();
                while(positions.remaining()>0){
                    AIVector3D position=positions.get();
                    System.out.printf("\t\t%f, %f, %f\n",position.x(),position.y(),position.z());
                }
                AIVector3D.Buffer normals=aiMesh.mNormals();
                while(normals.remaining()>0){
                    AIVector3D normal=normals.get();
                    System.out.printf("\t\t%f, %f, %f\n",normal.x(),normal.y(),normal.z());
                }
            }

            int materialCount=aiScene.mNumMaterials();
            PointerBuffer materialsBuffer=aiScene.mMaterials();
            for(int i=0; i<materialCount; i++){
                AIMaterial aiMaterial=AIMaterial.create(materialsBuffer.get(i));
                System.out.println("\taiMaterial");
            }

            model=new Model();

        }catch (Exception e){
            e.printStackTrace();
        }

        return model;
    }
}
