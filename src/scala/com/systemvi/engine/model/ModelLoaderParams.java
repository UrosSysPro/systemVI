package com.systemvi.engine.model;

public class ModelLoaderParams {
    public String fileName;

    public ModelLoaderParams(String fileName){
        this.fileName=fileName;
    }

    public static class Builder{
        private String fileName="";
        public Builder fileName(String fileName){
            this.fileName=fileName;
            return this;
        }
        public ModelLoaderParams build(){
            return new ModelLoaderParams(fileName);
        }
    }
    public static Builder builder(){
        return new Builder();
    }
}
