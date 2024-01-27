package com.systemvi.engine.shader;

import org.joml.*;

import java.io.File;
import java.util.Scanner;

import static org.lwjgl.opengl.GL46.*;

public class ComputeShader {
    public int id;
    private boolean compiled=true;
    private String compilationLog="";

    public ComputeShader(String fileName){
        String computeSource = readFile(fileName);

        int[] status=new int[1];

        int compute;
        compute = glCreateShader(GL_COMPUTE_SHADER);
        glShaderSource(compute, computeSource);
        glCompileShader(compute);
        glGetShaderiv(compute,GL_COMPILE_STATUS,status);
        if(status[0]==0){
            compiled=false;
            compilationLog+="Compile Log:\n"+glGetShaderInfoLog(compute);
            System.out.println("Compile Log:");
            System.out.println(glGetShaderInfoLog(compute));
        }

        id = glCreateProgram();
        glAttachShader(id, compute);
        glLinkProgram(id);
        glGetProgramiv(id,GL_LINK_STATUS,status);
        if(status[0]==0){
            compiled=false;
            compilationLog+="Program Link Log:\n"+glGetProgramInfoLog(id);
            System.out.println("Program Link Log:");
            System.out.println(glGetProgramInfoLog(id));
        }

        glDeleteShader(compute);
    }

    public void use(){
        glUseProgram(id);
    }

    public void dispatch(int x, int y, int z){
        glDispatchCompute(x, y, z);
//        glMemoryBarrier(GL_ALL_BARRIER_BITS);
    }

    public static String readFile(String name){
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
    public void setUniform(String name, Matrix3f mat){
        float[] data=new float[9];
        int uniformId=glGetUniformLocation(id,name);
        glUniformMatrix3fv(uniformId,false,mat.get(data));
    }
    public void setUniform(String name, Matrix2f mat){
        float[] data=new float[4];
        int uniformId=glGetUniformLocation(id,name);
        glUniformMatrix2fv(uniformId,false,mat.get(data));
    }
    public void setUniform(String name, Vector4i value){
        int uniformId=glGetUniformLocation(id,name);
        glUniform4i(uniformId,value.x,value.y,value.z,value.w);
    }
    public void setUniform(String name,Vector3i value){
        int uniformId=glGetUniformLocation(id,name);
        glUniform3i(uniformId,value.x,value.y,value.z);
    }
    public void setUniform(String name,Vector2i value){
        int uniformId=glGetUniformLocation(id,name);
        glUniform2i(uniformId,value.x,value.y);
    }
    public void setUniform(String name,int value){
        int uniformId=glGetUniformLocation(id,name);
        glUniform1i(uniformId,value);
    }
    public void setUniform(String name, Vector4f value){
        int uniformId=glGetUniformLocation(id,name);
        glUniform4f(uniformId,value.x,value.y,value.z,value.w);
    }
    public void setUniform(String name, Vector3f value){
        int uniformId=glGetUniformLocation(id,name);
        glUniform3f(uniformId,value.x,value.y,value.z);
    }
    public void setUniform(String name, Vector2f value){
        int uniformId=glGetUniformLocation(id,name);
        glUniform2f(uniformId,value.x,value.y);
    }
    public void setUniform(String name, float value){
        int uniformId=glGetUniformLocation(id,name);
        glUniform1f(uniformId,value);
    }
}
