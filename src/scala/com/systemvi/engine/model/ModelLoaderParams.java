package com.systemvi.engine.model;
import static org.lwjgl.assimp.Assimp.*;
public class ModelLoaderParams {
    public String fileName;
    public int flags;

    public ModelLoaderParams(String fileName,int flags){
        this.fileName=fileName;
        this.flags=flags;
    }

    public static class Builder{
        private String fileName="";
        private int flags=0;
        public Builder fileName(String fileName){
            this.fileName=fileName;
            return this;
        }
        public Builder calcTangentSpace(){
            flags|=aiProcess_CalcTangentSpace;
            return this;
        }
        public Builder debone(){
            flags|=aiProcess_Debone;
            return this;
        }
        public Builder dropNormals(){
            flags|=aiProcess_DropNormals;
            return this;
        }
        public Builder fixInfacingNormals(){
            flags|=aiProcess_FixInfacingNormals;
            return this;
        }
        public Builder embedTextures(){
            flags|=aiProcess_EmbedTextures;
            return this;
        }
        public Builder convertToLeftHanded(){
            flags|=aiProcess_ConvertToLeftHanded;
            return this;
        }
        public Builder findDegenerates(){
            flags|=aiProcess_FindDegenerates;
            return this;
        }
        public Builder findInstances(){
            flags|=aiProcess_FindInstances;
            return this;
        }
        public Builder findInvalidData(){
            flags|=aiProcess_FindInvalidData;
            return this;
        }
        public Builder flipUVs(){
            flags|=aiProcess_FlipUVs;
            return this;
        }
        public Builder flipWindingOrder(){
            flags|=aiProcess_FlipWindingOrder;
            return this;
        }
        public Builder forceGenNormals(){
            flags|=aiProcess_ForceGenNormals;
            return this;
        }
        public Builder genBoundingBoxes(){
            flags|=aiProcess_GenBoundingBoxes;
            return this;
        }
        public Builder genNormals(){
            flags|=aiProcess_GenNormals;
            return this;
        }
        public Builder genSmoothNormals(){
            flags|=aiProcess_GenSmoothNormals;
            return this;
        }
        public Builder genUVCoords(){
            flags|=aiProcess_GenUVCoords;
            return this;
        }
        public Builder joinIdenticalVertices(){
            flags|=aiProcess_JoinIdenticalVertices;
            return this;
        }
        public Builder triangulate(){
            flags|=aiProcess_Triangulate;
            return this;
        }
        public ModelLoaderParams build(){
            return new ModelLoaderParams(fileName,flags);
        }
    }

    public static Builder builder(){
        return new Builder();
    }
}
