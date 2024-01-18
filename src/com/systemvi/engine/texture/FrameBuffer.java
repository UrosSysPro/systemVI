package com.systemvi.engine.texture;

import  static org.lwjgl.opengl.GL33.*;

public class FrameBuffer {
    private int id;
    public FrameBuffer(Texture[] colorAttachments,Texture depthAttachment,Texture stencilAtttachment){
        id=glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER,id);

        for(int i=0;i<colorAttachments.length;i++){
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0+i, GL_TEXTURE_2D, colorAttachments[i].getId(), 0);
        }
        if(depthAttachment!=null){
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthAttachment.getId(), 0);
        }
        if(stencilAtttachment!=null){
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_STENCIL_ATTACHMENT, GL_TEXTURE_2D, stencilAtttachment.getId(), 0);
        }

        glBindFramebuffer(GL_FRAMEBUFFER,0);
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
    }
}
