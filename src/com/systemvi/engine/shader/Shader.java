package com.systemvi.engine.shader;
import org.joml.*;

import java.io.File;
import java.util.Scanner;

import static org.lwjgl.opengl.GL46.*;

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

    public Shader(int programId,boolean compiled,String log){
        this.id=programId;
        this.compiled=compiled;
        this.compilationLog=log;
    }

    public static class Builder{
        public String fragment,vertex,geometry;

        public Builder(){
            fragment=null;
            vertex=null;
            geometry=null;
        }
        public Builder fragment(String file){
            fragment=readFile(file);
            return this;
        }
        public Builder vertex(String file){
            vertex=readFile(file);
            return this;
        }
        public Builder geometry(String file){
            geometry=readFile(file);
            return this;
        }
        public Builder fragmentSource(String source){
            fragment=source;
            return this;
        }
        public Builder vertexSource(String source){
            vertex=source;
            return this;
        }
        public Builder geometrySource(String source){
            geometry=source;
            return this;
        }
        public Shader build(){
            boolean compiled=true;
            String log="";

            int[] status=new int[1];

            int vertexShader=glCreateShader(GL_VERTEX_SHADER);
            glShaderSource(vertexShader,vertex);
            glCompileShader(vertexShader);
            glGetShaderiv(vertexShader,GL_COMPILE_STATUS,status);
            if(status[0]==0){
                compiled=false;
                log+="Vertex Log:\n";
                log+=glGetShaderInfoLog(vertexShader)+"\n";
            }

            int fragmentShader=glCreateShader(GL_FRAGMENT_SHADER);
            glShaderSource(fragmentShader,fragment);
            glCompileShader(fragmentShader);
            glGetShaderiv(fragmentShader,GL_COMPILE_STATUS,status);
            if(status[0]==0){
                compiled=false;
                log+="Fragment Log:\n";
                log+=glGetShaderInfoLog(fragmentShader)+"\n";
            }

            int geometryShader=0;
            if(geometry!=null){
                geometryShader=glCreateShader(GL_GEOMETRY_SHADER);
                glShaderSource(geometryShader,geometry);
                glCompileShader(geometryShader);
                glGetShaderiv(geometryShader,GL_COMPILE_STATUS,status);
                if(status[0]==0){
                    compiled=false;
                    log+="Geometry Log:\n";
                    log+=glGetShaderInfoLog(geometryShader)+"\n";
                }
            }

            int programId=glCreateProgram();
            glAttachShader(programId,vertexShader);
            glAttachShader(programId,fragmentShader);
            if(geometry!=null)glAttachShader(programId,geometryShader);
            glLinkProgram(programId);

            glGetProgramiv(programId,GL_LINK_STATUS,status);
            if(status[0]==0){
                compiled=false;
                log+="Program Link Log:\n";
                log+=glGetProgramInfoLog(programId)+"\n";
            }
            glDeleteShader(vertexShader);
            glDeleteShader(fragmentShader);
            if(geometry!=null)glDeleteShader(geometryShader);
            glUseProgram(programId);

            return new Shader(programId,compiled,log);
        }
    }

    public static Builder builder(){
        return new Builder();
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
