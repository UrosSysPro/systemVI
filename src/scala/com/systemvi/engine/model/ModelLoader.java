package com.systemvi.engine.model;

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
            ArrayList<Model.Mesh> meshes = loadMeshes(aiScene);

            ArrayList<Model.Material> materials=loadMaterials(aiScene);

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


            model=new Model(meshes,materials);

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

    private static ArrayList<Model.Mesh> loadMeshes(AIScene aiScene){
        ArrayList<Model.Mesh> meshes=new ArrayList<>();
        PointerBuffer meshesBuffer=aiScene.mMeshes();
        int meshCount=aiScene.mNumMeshes();
        for(int i=0; i < meshCount; i++){
            ArrayList<Model.Vertex> vertices=new ArrayList<>();
            AIMesh aiMesh=AIMesh.create(meshesBuffer.get(i));

            int numVertices=aiMesh.mNumVertices();
            for(int j=0; j<numVertices; j++){
                AIVector3D position=aiMesh.mVertices().get(j);
                AIVector3D normal=aiMesh.mNormals().get(j);
                AIVector3D tangent=aiMesh.mTangents().get(j);
                AIVector3D bitangent=aiMesh.mBitangents().get(j);
                vertices.add(new Model.Vertex(
                    new Vector3f(position.x(),position.y(),position.z()),
                    new Vector3f(normal.x(),normal.y(),normal.z()),
                    new Vector3f(tangent.x(),tangent.y(),tangent.z()),
                    new Vector3f(bitangent.x(),bitangent.y(),bitangent.z())
                ));
            }

            meshes.add(new Model.Mesh(vertices));
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

            aiGetMaterialTexture(aiMaterial,aiTextureType_DIFFUSE,0,aiString,(IntBuffer) null,null,null,null,null,null);
            if(aiString != null && aiString.dataString() != null) System.out.println(aiString.dataString());

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
}
