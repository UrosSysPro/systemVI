package com.systemvi.engine.texture;

import java.util.ArrayList;
import  static org.lwjgl.opengl.GL33.*;

public class FrameBuffer {
    private final int id,renderBuffer;
    private final int width,height;
    private final boolean hasDepthAndStencil;
    public FrameBuffer(Texture[] colorAttachments,boolean createDepthAndStencilBuffer){
        id=glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER,id);

        width=colorAttachments[0].getWidth();
        height=colorAttachments[0].getHeight();
        hasDepthAndStencil=createDepthAndStencilBuffer;

        if(createDepthAndStencilBuffer){
            renderBuffer=glGenRenderbuffers();
            glBindRenderbuffer(GL_RENDERBUFFER,renderBuffer);
            glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, width, height);
            glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, renderBuffer);
        }else{
            renderBuffer=0;
        }

        for(int i=0;i<colorAttachments.length;i++){
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0+i, GL_TEXTURE_2D, colorAttachments[i].getId(), 0);
        }

        glBindFramebuffer(GL_FRAMEBUFFER,0);
    }

    public static class Builder{
        private ArrayList<Texture> colorAttachments;
        private Texture depthAttachment;
        private Texture stencilAttachment;
        private boolean hasDepthAndStencil;

        public Builder(){
            colorAttachments=new ArrayList<>();
            depthAttachment=null;
            stencilAttachment=null;
            hasDepthAndStencil=false;
        }
        public Builder color(Texture color){
            colorAttachments.add(color);
            return this;
        }
        public Builder depth(Texture depth){
            depthAttachment=depth;
            return this;
        }
        public Builder stencil(Texture stencil){
            stencilAttachment=stencil;
            return this;
        }
        public Builder depthAndStencil(boolean depthAndStencil){
            hasDepthAndStencil=depthAndStencil;
            return this;
        }
        public FrameBuffer build(){
            Texture[] c=new Texture[colorAttachments.size()];
            for(int i=0;i<colorAttachments.size();i++){
                c[i]=colorAttachments.get(i);
            }
            if(depthAttachment!=null){

            }
            if(stencilAttachment!=null){

            }
            return new FrameBuffer(c,hasDepthAndStencil);
        }
    }

    public static Builder builder(){
        return new Builder();
    }
    public int getWidth() {
        return width;
    }
    public int getHeight(){
        return height;
    }

    public int getId() {
        return id;
    }

    public boolean hasDepthAndStencil(){
        return hasDepthAndStencil;
    }

    public boolean isCoplete(){
        return glCheckFramebufferStatus(GL_FRAMEBUFFER) == GL_FRAMEBUFFER_COMPLETE;
    }
    public void begin(){
        glBindFramebuffer(GL_FRAMEBUFFER,id);
    }
    public void end(){
        glBindFramebuffer(GL_FRAMEBUFFER,0);
    }
    public void delete(){
        glDeleteFramebuffers(id);
        if(renderBuffer!=0){
            glDeleteRenderbuffers(renderBuffer);
        }
    }
}
