package com.systemvi.engine.model;

import org.lwjgl.assimp.*;

import static org.lwjgl.assimp.Assimp.*;

public class ModelLoader {
    public static Model load(ModelLoaderParams params){
        Model model = null;
        try(AIScene aiScene = aiImportFile(params.fileName,aiProcess_JoinIdenticalVertices | aiProcess_Triangulate | aiProcess_FixInfacingNormals)){
//            if(aiScene == null){
//                System.out.println("error");
//            }
//
//            model=new Model();
        }
        return model;
    }
}
