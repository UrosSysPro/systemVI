package com.systemvi.engine.texture;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

import static org.lwjgl.opengl.GL33.*;

public class FrameBuffer {

    public static class Attachment {
        public Texture texture;
        public boolean enabled;

        public Attachment(Texture texture, boolean enabled) {
            this.texture = texture;
            this.enabled = enabled;
        }
    }

    public static Stack<FrameBuffer> stack = new Stack<>();
    private final int id, renderBuffer;
    private final int width, height;
    private final boolean hasRenderBuffer;
    private final Attachment[] attachments;
    private final Attachment depthAttachment;

    public FrameBuffer(Attachment[] attachments,Attachment depthAttachment, boolean createRenderBuffer) {
        this.hasRenderBuffer = createRenderBuffer;
        this.attachments = attachments;
        this.depthAttachment = depthAttachment;
        int[] drawBuffers = new int[attachments.length];
        for (int i = 0; i < drawBuffers.length; i++) {
            drawBuffers[i] = GL_COLOR_ATTACHMENT0 + i;
        }

        id = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, id);

        width = attachments[0].texture.getWidth();
        height = attachments[0].texture.getHeight();

        if (createRenderBuffer) {
            renderBuffer = glGenRenderbuffers();
            glBindRenderbuffer(GL_RENDERBUFFER, renderBuffer);
            glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, width, height);
            glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, renderBuffer);
        } else {
            renderBuffer = 0;
        }

        for (int i = 0; i < attachments.length; i++) {
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0 + i, GL_TEXTURE_2D, attachments[i].texture.getId(), 0);
        }
        if(depthAttachment!=null)glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthAttachment.texture.getId(), 0);

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    //builder
    public static class Builder {
        private ArrayList<Attachment> colorAttachments;
        private Attachment depthAttachment;
        private Texture stencilAttachment;
        private boolean hasDepthAndStencil;

        public Builder() {
            colorAttachments = new ArrayList<>();
            depthAttachment = null;
            stencilAttachment = null;
            hasDepthAndStencil = false;
        }

        public Builder color(Texture color) {
            colorAttachments.add(new Attachment(color, true));
            return this;
        }

        public Builder depth(Texture depth) {
            depthAttachment = new Attachment(depth, true);
            return this;
        }

        public Builder stencil(Texture stencil) {
            stencilAttachment = stencil;
            return this;
        }

        public Builder depthAndStencil(boolean depthAndStencil) {
            hasDepthAndStencil = depthAndStencil;
            return this;
        }

        public FrameBuffer build() {
            Attachment[] attachments = new Attachment[colorAttachments.size()];
            for(int i = 0; i < colorAttachments.size(); i++) attachments[i] = colorAttachments.get(i);
            return new FrameBuffer(attachments,depthAttachment, hasDepthAndStencil);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    //getters
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getId() {
        return id;
    }

    public boolean hasRenderBuffer() {
        return hasRenderBuffer;
    }

    public boolean isCoplete() {
        return glCheckFramebufferStatus(GL_FRAMEBUFFER) == GL_FRAMEBUFFER_COMPLETE;
    }

    public Attachment getDepthAttachment() {
        return depthAttachment;
    }

    public Attachment[] getAttachments() {
        return attachments;
    }

    //utils
    public void begin() {
        stack.push(this);
        int[] drawBuffers = new int[attachments.length];
        for (int i = 0; i < drawBuffers.length; i++) {
            drawBuffers[i] = GL_COLOR_ATTACHMENT0 + i;
        }
        glBindFramebuffer(GL_FRAMEBUFFER, id);
        glDrawBuffers(drawBuffers);
    }

    public void end() {
        if (stack.peek() != this) System.out.println("[ERROR] Framebuffers closed out of order");
        if (stack.empty()) {
            System.out.println("[ERROR] Framebuffers calling end on empty framebuffer stack");
            return;
        }
        stack.pop();
        if (stack.isEmpty()) {
            glBindFramebuffer(GL_FRAMEBUFFER, 0);
        } else {
            glBindFramebuffer(GL_FRAMEBUFFER, stack.peek().getId());
            int[] drawBuffers = new int[stack.peek().attachments.length];
            for (int i = 0; i < drawBuffers.length; i++) {
                drawBuffers[i] = GL_COLOR_ATTACHMENT0 + i;
            }
            glDrawBuffers(drawBuffers);
        }
    }

    public void delete() {
        glDeleteFramebuffers(id);
        if (renderBuffer != 0) {
            glDeleteRenderbuffers(renderBuffer);
        }
    }
}
