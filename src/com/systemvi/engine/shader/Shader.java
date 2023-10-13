package com.systemvi.engine.shader;
import org.joml.Matrix4f;

import java.io.File;
import java.util.Scanner;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;

public class Shader {
    private int id;
    private boolean compiled=true;
    private String compilationLog="";

    public Shader(String vertex,String fragment){
        String vertexSource=readFile(vertex);
        String fragmentSource=readFile(fragment);

        int[] status=new int[1];

        int vertexShader=glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader,vertexSource);
        glCompileShader(vertexShader);
        glGetShaderiv(vertexShader,GL_COMPILE_STATUS,status);
        if(status[0]==0){
            compiled=false;
            compilationLog+="Vertex Log:\n";
            compilationLog+=glGetShaderInfoLog(vertexShader)+"\n";
        }

        int fragmentShader=glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader,fragmentSource);
        glCompileShader(fragmentShader);
        glGetShaderiv(fragmentShader,GL_COMPILE_STATUS,status);
        if(status[0]==0){
            compiled=false;
            compilationLog+="Fragment Log:\n";
            compilationLog+=glGetShaderInfoLog(fragmentShader)+"\n";
        }

        id=glCreateProgram();
        glAttachShader(id,vertexShader);
        glAttachShader(id,fragmentShader);
        glLinkProgram(id);

        glGetProgramiv(id,GL_LINK_STATUS,status);
        if(status[0]==0){
            compiled=false;
            compilationLog+="Program Link Log:\n";
            compilationLog+=glGetProgramInfoLog(id)+"\n";
        }
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
        glUseProgram(id);
    }

    public boolean isCompiled() {
        return compiled;
    }

    public String getLog() {
        return compilationLog;
    }

    public int getId() {
        return id;
    }

    public void use(){
        glUseProgram(id);
    }
    public void delete(){
        glDeleteProgram(id);
    }

    public String readFile(String name){
        try{
            File file=new File(name);
            Scanner scanner=new Scanner(file);
            StringBuilder content= new StringBuilder();
            while(scanner.hasNext()){
                content.append(scanner.nextLine()).append("\n");
            }
            scanner.close();
            return content.toString();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public void setUniform(String name, Matrix4f mat){
        float[] data=new float[16];
        int uniformId=glGetUniformLocation(id,name);
        glUniformMatrix4fv(uniformId,false,mat.get(data));
    }
    public void setUniform(String name,int value){
        int unifromId=glGetUniformLocation(id,name);
        glUniform1i(unifromId,value);
    }
}
