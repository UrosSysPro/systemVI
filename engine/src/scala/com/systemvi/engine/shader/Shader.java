package com.systemvi.engine.shader;
import com.systemvi.engine.buffer.UniformBuffer;
import com.systemvi.engine.utils.Utils;
import org.joml.*;

import java.io.File;
import java.util.Scanner;

import static org.lwjgl.opengl.GL46.*;

public class Shader {
    private final int id;
    private final boolean compiled;
    private final String compilationLog;

    private Shader(int programId,boolean compiled,String log){
        this.id=programId;
        this.compiled=compiled;
        this.compilationLog=log;
    }

    public static class Builder{
        private String fragment,vertex,geometry,compute,tesselationEvaluation,tesselationControl;

        public Builder(){
            fragment=null;
            vertex=null;
            geometry=null;
        }
        public Builder fragment(String file){
            fragment= Utils.readInternal(file);
            return this;
        }
        public Builder vertex(String file){
            vertex=Utils.readInternal(file);
            return this;
        }
        public Builder geometry(String file){
            geometry=Utils.readInternal(file);
            return this;
        }
        public Builder tesselationEvaluation(String file){
            tesselationEvaluation=Utils.readInternal(file);
            return this;
        }
        public Builder tesselationControl(String file){
            tesselationControl=Utils.readInternal(file);
            return this;
        }
        public Builder compute(String file){
            compute=Utils.readInternal(file);
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
        public Builder tesselationEvaluationSource(String source){
            tesselationEvaluation=source;
            return this;
        }
        public Builder tesselationControlSource(String source){
            tesselationControl=source;
            return this;
        }
        public Builder computeSource(String source){
            compute=source;
            return this;
        }
        public Shader build(){
            boolean compiled=true;
            String log="";

            int[] status=new int[1];

            //cmopiling vertext shader
            int vertexShader=0;
            if(vertex!=null){
                vertexShader=glCreateShader(GL_VERTEX_SHADER);
                glShaderSource(vertexShader,vertex);
                glCompileShader(vertexShader);
                glGetShaderiv(vertexShader,GL_COMPILE_STATUS,status);
                if(status[0]==0){
                    compiled=false;
                    log+="Vertex Log:\n";
                    log+=glGetShaderInfoLog(vertexShader)+"\n";
                }
            }

            //compiling fragment
            int fragmentShader=0;
            if(fragment!=null){
                fragmentShader=glCreateShader(GL_FRAGMENT_SHADER);
                glShaderSource(fragmentShader,fragment);
                glCompileShader(fragmentShader);
                glGetShaderiv(fragmentShader,GL_COMPILE_STATUS,status);
                if(status[0]==0){
                    compiled=false;
                    log+="Fragment Log:\n";
                    log+=glGetShaderInfoLog(fragmentShader)+"\n";
                }
            }

            //compiling geometry
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

            int tesselationEvaluationShader=0;
            if(tesselationEvaluation!=null){
                tesselationEvaluationShader=glCreateShader(GL_TESS_EVALUATION_SHADER);
                glShaderSource(tesselationEvaluationShader,tesselationEvaluation);
                glCompileShader(tesselationEvaluationShader);
                glGetShaderiv(tesselationEvaluationShader,GL_COMPILE_STATUS,status);
                if(status[0]==0){
                    compiled=false;
                    log+="Tesselation Evaluation Log:\n";
                    log+=glGetShaderInfoLog(tesselationEvaluationShader)+"\n";
                }
            }

            int tesselationControlShader=0;
            if(tesselationControl!=null){
                tesselationControlShader=glCreateShader(GL_TESS_CONTROL_SHADER);
                glShaderSource(tesselationControlShader,tesselationControl);
                glCompileShader(tesselationControlShader);
                glGetShaderiv(tesselationControlShader,GL_COMPILE_STATUS,status);
                if(status[0]==0){
                    compiled=false;
                    log+="Tesselation Control Log:\n";
                    log+=glGetShaderInfoLog(tesselationControlShader)+"\n";
                }
            }

            //compiling compute shader
            int computeShader=0;
            if(compute!=null){
                computeShader=glCreateShader(GL_COMPUTE_SHADER);
                glShaderSource(computeShader,compute);
                glCompileShader(computeShader);
                glGetShaderiv(computeShader,GL_COMPILE_STATUS,status);
                if(status[0]==0){
                    compiled=false;
                    log+="Compute Log:\n";
                    log+=glGetShaderInfoLog(computeShader)+"\n";
                }
            }

            int programId=glCreateProgram();
            if(vertex   !=null)    glAttachShader(programId,vertexShader);
            if(fragment !=null)  glAttachShader(programId,fragmentShader);
            if(geometry !=null)  glAttachShader(programId,geometryShader);
            if(tesselationControl !=null)  glAttachShader(programId,tesselationControlShader);
            if(tesselationEvaluation !=null)  glAttachShader(programId,tesselationEvaluationShader);
            if(compute  !=null)  glAttachShader(programId,computeShader);
            glLinkProgram(programId);

            glGetProgramiv(programId,GL_LINK_STATUS,status);
            if(status[0]==0){
                compiled=false;
                log+="Program Link Log:\n";
                log+=glGetProgramInfoLog(programId)+"\n";
            }
            if(vertex!=null)glDeleteShader(vertexShader);
            if(fragment!=null)glDeleteShader(fragmentShader);
            if(geometry!=null)glDeleteShader(geometryShader);
            if(compute!=null)glDeleteShader(computeShader);

            glUseProgram(programId);

            if(!compiled){
                System.out.println(log);
            }

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

    public void bindUniformBuffer(String name, UniformBuffer buffer){
        int index=glGetUniformBlockIndex(id,name);
    }

    /**
     * @param primitive type of primitive to draw
     * @param count number of vertices, so 3 for one triangle or 2 for a line
     * @param first vertex offset from the beginning of array buffer
     * */
    public void drawArrays(Primitive primitive,int first,int count){
        glDrawArrays(primitive.id,first,count);
    }
    /**
     * @param primitive type of primitive to draw
     * @param count number of vertices, so 3 for one triangle or 2 for a line
     * */
    public void drawArrays(Primitive primitive,int count){
        drawArrays(primitive,0,count);
    }
    /**
     * @param elements number of faces,lines, whatever is being rendered
     * @param indicesPerElement number of indices per face,line... 3 for triangles
     * */
    public void drawElements(Primitive primitive,int elements, ElementsDataType type,int indicesPerElement){
        glDrawElements(primitive.id,elements*indicesPerElement,type.id,0);
    }

    public void drawArraysInstanced(Primitive primitive,int first,int count,int instancesToDraw){
        glDrawArraysInstanced(primitive.id,first,count,instancesToDraw);
    }
    public void drawArraysInstanced(Primitive primitive,int count,int instancesToDraw){
        drawArraysInstanced(primitive,0,count,instancesToDraw);
    }
    public void drawElementsInstanced(Primitive primitive,int count,ElementsDataType type,int elementsToDraw,int instancesToDraw){
        glDrawElementsInstanced(primitive.id,count,type.id,elementsToDraw,instancesToDraw);
    }

    public void dispatch(int x,int y,int z){
        glDispatchCompute(x,y,z);
    }
}
