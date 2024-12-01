package com.systemvi.engine.model;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import java.nio.IntBuffer;
import java.util.ArrayList;

import static org.lwjgl.assimp.Assimp.*;

public class ModelUtils {
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
            aiReleaseImport(aiScene);
        }catch (Exception e){
            e.printStackTrace();
        }

        return model;
    }

    private static ArrayList<Model.Mesh> loadMeshes(AIScene aiScene,ArrayList<Model.Material> materials){
        ArrayList<Model.Mesh> meshes=new ArrayList<>();
        PointerBuffer meshesBuffer=aiScene.mMeshes();
        int meshCount=aiScene.mNumMeshes();
        for(int i=0; i < meshCount; i++){
            ArrayList<Model.Vertex> vertices=new ArrayList<>();
            ArrayList<Model.Face> faces=new ArrayList<>();
            AIMesh aiMesh=AIMesh.create(meshesBuffer.get(i));

            String name=aiMesh.mName().dataString();

            int numVertices=aiMesh.mNumVertices();
            int numFaces=aiMesh.mNumFaces();

            AIFace.Buffer faceBuffer=aiMesh.mFaces();

            for(int j=0;j<numFaces;j++){
                AIFace face=faceBuffer.get(j);
                IntBuffer indices=face.mIndices();
                faces.add(new Model.Face(new int[]{
                    indices.get(0),
                    indices.get(1),
                    indices.get(2)
                }));
            }

            for(int j=0; j<numVertices; j++){
                AIVector3D position=aiMesh.mVertices().get(j);
                AIVector3D normal=aiMesh.mNormals().get(j);
                AIVector3D tangent=aiMesh.mTangents().get(j);
                AIVector3D bitangent=aiMesh.mBitangents().get(j);
                ArrayList<Vector3f> texCoords=new ArrayList<>();
                ArrayList<Vector4f> colors=new ArrayList<>();

                for(int k=0;k<8;k++){
                    AIVector3D.Buffer texCoordsBuffer=aiMesh.mTextureCoords(k);
                    AIColor4D.Buffer colorsBuffer=aiMesh.mColors(k);

                    if(texCoordsBuffer!=null) {
                        AIVector3D texCoord=texCoordsBuffer.get(i);
                        texCoords.add(new Vector3f(texCoord.x(),texCoord.y(),texCoord.z()));
                    }else{
                        texCoords.add(new Vector3f(-1f));
                    }

                    if(colorsBuffer!=null){
                        AIColor4D color=colorsBuffer.get(i);
                        colors.add(new Vector4f(color.r(),color.g(),color.b(),color.a()));
                    }else{
                        colors.add(new Vector4f(1,0,1,1));
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

            meshes.add(new Model.Mesh(name,vertices,material,materialIndex,faces));
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

            aiGetMaterialTexture(aiMaterial,aiTextureType_DIFFUSE,0,aiString,(IntBuffer) null,null,null,null,null,null);
            String diffuseMapFile=aiString.dataString();

            aiGetMaterialTexture(aiMaterial,aiTextureType_METALNESS,0,aiString,(IntBuffer) null,null,null,null,null,null);
            String metalnessMapFile=aiString.dataString();

            aiGetMaterialTexture(aiMaterial,aiTextureType_DISPLACEMENT,0,aiString,(IntBuffer) null,null,null,null,null,null);
            String displacementMapFile=aiString.dataString();

            aiGetMaterialTexture(aiMaterial,aiTextureType_DIFFUSE_ROUGHNESS,0,aiString,(IntBuffer) null,null,null,null,null,null);
            String roughnessMapFile=aiString.dataString();

            aiGetMaterialTexture(aiMaterial,aiTextureType_NORMALS,0,aiString,(IntBuffer) null,null,null,null,null,null);
            String normalMapFile=aiString.dataString();

            aiGetMaterialTexture(aiMaterial,aiTextureType_AMBIENT_OCCLUSION,0,aiString,(IntBuffer) null,null,null,null,null,null);
            String ambientOclusionMapFile=aiString.dataString();

            result=aiGetMaterialColor(aiMaterial,AI_MATKEY_COLOR_AMBIENT,aiTextureType_NONE,0,aiColor);
            Vector4f ambient=new Vector4f(0.1f,0.1f,0.1f,1.0f);
            if(result==0)ambient.set(aiColor.r(),aiColor.g(),aiColor.b(),aiColor.a());

            result=aiGetMaterialColor(aiMaterial,AI_MATKEY_COLOR_DIFFUSE,aiTextureType_NONE,0,aiColor);
            Vector4f diffuse=new Vector4f(0.1f,0.1f,0.1f,1.0f);
            if(result==0)diffuse.set(aiColor.r(),aiColor.g(),aiColor.b(),aiColor.a());

            result=aiGetMaterialColor(aiMaterial,AI_MATKEY_COLOR_SPECULAR,aiTextureType_NONE,0,aiColor);
            Vector4f specular=new Vector4f(0.1f,0.1f,0.1f,1.0f);
            if(result==0)specular.set(aiColor.r(),aiColor.g(),aiColor.b(),aiColor.a());

            result=aiGetMaterialColor(aiMaterial,AI_MATKEY_COLOR_EMISSIVE,aiTextureType_NONE,0,aiColor);
            Vector4f emissive=new Vector4f(0.1f,0.1f,0.1f,1.0f);
            if(result==0)emissive.set(aiColor.r(),aiColor.g(),aiColor.b(),aiColor.a());

            result=aiGetMaterialColor(aiMaterial,AI_MATKEY_COLOR_REFLECTIVE,aiTextureType_NONE,0,aiColor);
            Vector4f reflective=new Vector4f(0.1f,0.1f,0.1f,1.0f);
            if(result==0)reflective.set(aiColor.r(),aiColor.g(),aiColor.b(),aiColor.a());

            result=aiGetMaterialColor(aiMaterial,AI_MATKEY_COLOR_TRANSPARENT,aiTextureType_NONE,0,aiColor);
            Vector4f transparent=new Vector4f(0.1f,0.1f,0.1f,1.0f);
            if(result==0)transparent.set(aiColor.r(),aiColor.g(),aiColor.b(),aiColor.a());


            materials.add(
                new Model.Material(
                    ambient,
                    diffuse,
                    specular,
                    emissive,
                    reflective,
                    transparent,
                    diffuseMapFile,
                    specularMapFile,
                    ambientOclusionMapFile,
                    metalnessMapFile,
                    displacementMapFile,
                    roughnessMapFile,
                    normalMapFile
                )
            );
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
            t.a1(),t.b1(),t.c1(),t.d1(),
            t.a2(),t.b2(),t.c2(),t.d2(),
            t.a3(),t.b3(),t.c3(),t.d3(),
            t.a4(),t.b4(),t.c4(),t.d4()
        );
        //meshes
        int meshCount=aiNode.mNumMeshes();
        IntBuffer meshIndicesBuffer=aiNode.mMeshes();
        ArrayList<Integer> meshIndices=new ArrayList<>();
        for(int i=0;i<meshCount;i++){
            meshIndices.add(meshIndicesBuffer.get(i));
        }
        ArrayList<Model.Mesh> meshes=new ArrayList<>();
        for (Integer meshIndex : meshIndices) {
            meshes.add(modelMeshes.get(meshIndex));
        }
        //children
        int childCount=aiNode.mNumChildren();
        ArrayList<Model.Node> children=new ArrayList<>();
        PointerBuffer childrenBuffer=aiNode.mChildren();
        for(int i=0;i<childCount;i++){
            AINode aiChildNode=AINode.create(childrenBuffer.get(i));
            children.add(loadNode(aiChildNode,modelMeshes));
        }
        return new Model.Node(name,children,meshIndices,meshes,transform);
    }

    public static void export(Model model){

    }
}
