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
            AIScene aiScene = aiImportFile(params.fileName,params.flags);
            if(aiScene == null){
                System.out.println("[ERROR] failed to load model");
                return model;
            }
            System.out.println("aiScene");
            //meshes
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

            //materials
            int materialCount=aiScene.mNumMaterials();
            PointerBuffer materialsBuffer=aiScene.mMaterials();
            for(int i=0; i<materialCount; i++){
                AIMaterial aiMaterial=AIMaterial.create(materialsBuffer.get(i));
                System.out.println("\taiMaterial");
            }

            //textures
            System.out.println("\tTextures");
            PointerBuffer textureBuffer=aiScene.mTextures();
            if(textureBuffer!=null){
                while(textureBuffer.remaining()>0){
                    AITexture texture=AITexture.create(textureBuffer.get());
                    AIString aiString=texture.mFilename();
                    System.out.println(aiString.dataString());
                }
            }else{
                System.out.println("\tno textures");
            }

            //node
            System.out.println("\tnodes");
            AINode aiNode=aiScene.mRootNode();
            printNodes(aiNode,"\t\t");

            //cameras
            System.out.println("\tcameras");
            PointerBuffer cameraBuffer=aiScene.mCameras();
            if(cameraBuffer!=null){
                while (cameraBuffer.remaining()>0){
                    AICamera aiCamera=AICamera.create(cameraBuffer.get());
                    System.out.println("\t\tcamera");
                }
            }else{
                System.out.println("\tno cameras");
            }

            //lights
            System.out.println("\tlights");
            PointerBuffer lightBuffer=aiScene.mLights();
            if(cameraBuffer!=null){
                while (cameraBuffer.remaining()>0){
                    AILight aiLight=AILight.create(lightBuffer.get());
                    System.out.println("\t\tlight");
                }
            }else{
                System.out.println("\tno lights");
            }


            model=new Model();

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
}
