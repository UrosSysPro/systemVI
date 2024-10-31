package com.systemvi.engine.texture;

import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.opengl.GL42.glBindImageTexture;

import com.systemvi.engine.ui.utils.data.Colors;
import com.systemvi.engine.utils.Utils;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;

import java.nio.ByteBuffer;

public class Texture {
    public enum Repeat {
        REPEAT(GL_REPEAT),
        REPEAT_MIRRORED(GL_MIRRORED_REPEAT),
        CLAMP_BORDER(GL_CLAMP_TO_BORDER),
        CLAMP_EDGE(GL_CLAMP_TO_EDGE),
        ;
        public final int id;

        Repeat(int id) {
            this.id = id;
        }
    }

    public enum FilterMin {
        LINEAR(GL_LINEAR),
        NEAREST(GL_NEAREST),
        LINEAR_MIPMAP_LINEAR(GL_LINEAR_MIPMAP_LINEAR),
        LINEAR_MIPMAP_NEAREST(GL_LINEAR_MIPMAP_NEAREST),
        NEAREST_MIPMAP_LINEAR(GL_NEAREST_MIPMAP_LINEAR),
        NEAREST_MIPMAP_NEAREST(GL_NEAREST_MIPMAP_NEAREST),
        ;
        public final int id;

        FilterMin(int id) {
            this.id = id;
        }
    }

    public enum FilterMag {
        LINEAR(GL_LINEAR),
        NEAREST(GL_NEAREST),
        ;
        public final int id;

        FilterMag(int id) {
            this.id = id;
        }
    }

    private final int id;
    private int width;
    private int height;
    private Format format;
    private FilterMin filterMin;
    private FilterMag filterMag;
    private Repeat horizontalRepeat, verticalRepeat;
    private boolean multisampled;
    private final Vector4f borderColor = new Vector4f();

    private Texture() {
        id = glGenTextures();
    }

    public Texture(int width, int height, Format format, FilterMin filterMin, FilterMag filterMag, Repeat horizontalRepeat, Repeat verticalRepeat, boolean multisampled, Vector4f borderColor) {
        this();
        this.width = width;
        this.height = height;
        this.format = format;
        setSamplerFilter(filterMin, filterMag);
        setRepeat(horizontalRepeat, verticalRepeat);
        this.multisampled = multisampled;
        setBorderColor(borderColor);
    }

    public Texture(String fileName) {
        id = glGenTextures();
        setRepeat(GL_REPEAT, GL_REPEAT);
        setSamplerFilter(GL_NEAREST, GL_NEAREST);
        loadFromFile(fileName);
    }

    public Texture(int width, int height, Format format) {
        this.width = width;
        this.height = height;
        this.format = format;
        id = glGenTextures();

        setRepeat(GL_REPEAT, GL_REPEAT);
        setSamplerFilter(GL_NEAREST, GL_NEAREST);

        glBindTexture(GL_TEXTURE_2D, id);
        switch (format.id) {
            case GL_DEPTH_COMPONENT:
            case GL_DEPTH_COMPONENT16:
            case GL_DEPTH_COMPONENT24:
            case GL_DEPTH_COMPONENT32: {
                glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT24, width, height, 0, GL_DEPTH_COMPONENT, GL_FLOAT, (ByteBuffer) null);
            }
            break;
            default: {
                glTexImage2D(GL_TEXTURE_2D, 0, this.format.id, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer) null);
            }
            break;
        }
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public Texture setData(TextureData data) {
        width = data.getWidth();
        height = data.getHeight();

        glBindTexture(GL_TEXTURE_2D, id);
        glTexImage2D(GL_TEXTURE_2D, 0, this.format.id, width, height, 0, Format.RGBA.id, GL_UNSIGNED_BYTE, data.getBuffer());
        glGenerateMipmap(GL_TEXTURE_2D);

        glBindTexture(GL_TEXTURE_2D, 0);
        return this;
    }

    public Texture loadFromFile(String fileName) {
        glBindTexture(GL_TEXTURE_2D, id);

        int[] width = new int[1], height = new int[1], chanels = new int[1];
        ByteBuffer buffer = STBImage.stbi_load(Utils.assetsFolder + fileName, width, height, chanels, 0);
        if (buffer == null) {
            System.out.println("[ERROR] Loading Image");
            return this;
        }
        this.width = width[0];
        this.height = height[0];
        int channels = chanels[0];
        this.format = Format.R;
        if (channels == 2) this.format = Format.RG;
        if (channels == 3) this.format = Format.RGB;
        if (channels == 4) this.format = Format.RGBA;

        glTexImage2D(GL_TEXTURE_2D, 0, this.format.id, this.width, this.height, 0, this.format.id, GL_UNSIGNED_BYTE, buffer);
        glGenerateMipmap(GL_TEXTURE_2D);
        STBImage.stbi_image_free(buffer);
        glBindTexture(GL_TEXTURE_2D, 0);
        return this;
    }

    public Texture fromFormat(int width, int height, Format format,boolean multisampled) {
        this.width = width;
        this.height = height;
        this.format = format;
        this.multisampled = multisampled;
        glTexImage2D(GL_TEXTURE_2D, 0, this.format.id, this.width, this.height, 0, Format.RGBA.id, GL_UNSIGNED_BYTE, (ByteBuffer) null);
        glGenerateMipmap(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, 0);
        return this;
    }

    public void bind() {
        bind(0);
    }

    public void bind(int i) {
        glActiveTexture(GL_TEXTURE0 + i);
        glBindTexture(GL_TEXTURE_2D, id);
    }

    public void bindAsImage(int i) {
        glActiveTexture(GL_TEXTURE0 + i);
        glBindTexture(GL_TEXTURE_2D, id);
        glBindImageTexture(i, id, 0, false, 0, GL_READ_WRITE, format.id);
    }

    //getters
    public int getId() {
        return id;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getChannels() {
        return format.channels;
    }

    public Format getFormat() {
        return format;
    }

    public Repeat getVerticalRepeat() {
        return verticalRepeat;
    }

    public Repeat getHorizontalRepeat() {
        return horizontalRepeat;
    }

    public FilterMin getFilterMin() {
        return filterMin;
    }

    public FilterMag getFilterMag() {
        return filterMag;
    }

    public boolean isMultisampled() {
        return multisampled;
    }

    public Vector4f getBorderColor() {
        return borderColor;
    }

    //setters
    public void setBorderColor(float r, float g, float b, float a) {
        this.borderColor.set(r, g, b, a);
        float[] borderColor = new float[]{r, g, b, a};
        glBindTexture(GL_TEXTURE_2D, id);
        glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, borderColor);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void setBorderColor(Vector4f color) {
        this.borderColor.set(color);
        float[] borderColor = new float[]{this.borderColor.x, this.borderColor.y, this.borderColor.z, this.borderColor.w};
        glBindTexture(GL_TEXTURE_2D, id);
        glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, borderColor);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void setRepeat(int horizontal, int vertical) {
        glBindTexture(GL_TEXTURE_2D, id);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, horizontal);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, vertical);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void setRepeat(Repeat horizontal, Repeat vertical) {
        this.horizontalRepeat = horizontal;
        this.verticalRepeat = vertical;
        glBindTexture(GL_TEXTURE_2D, id);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, horizontalRepeat.id);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, verticalRepeat.id);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void setSamplerFilter(int min, int mag) {
        glBindTexture(GL_TEXTURE_2D, id);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, min);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, mag);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void setSamplerFilter(FilterMin min, FilterMag mag) {
        this.filterMin = min;
        this.filterMag = mag;
        glBindTexture(GL_TEXTURE_2D, id);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, min.id);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, mag.id);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    //utils
    public void generateMipMaps() {
        glBindTexture(GL_TEXTURE_2D, id);
        glGenerateMipmap(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void delete() {
        glBindTexture(GL_TEXTURE_2D, 0);
        glDeleteTextures(id);
    }

    //builder
    public static class Builder {
        private int width = 1;
        private int height = 1;
        private Format format = Format.RGBA;
        private FilterMin filterMin = FilterMin.NEAREST;
        private FilterMag filterMag = FilterMag.NEAREST;
        private Repeat horizontalRepeat = Repeat.REPEAT, verticalRepeat = Repeat.REPEAT;
        private boolean multisampled = false;
        private final Vector4f borderColor = new Vector4f(Colors.black());
        private String file = null;

        public Builder width(int width) {
            this.width = width;
            return this;
        }

        public Builder height(int height) {
            this.height = height;
            return this;
        }

        public Builder format(Format format) {
            this.format = format;
            return this;
        }

        public Builder filterMin(FilterMin filterMin) {
            this.filterMin = filterMin;
            return this;
        }

        public Builder filterMag(FilterMag filterMag) {
            this.filterMag = filterMag;
            return this;
        }

        public Builder horizontalRepeat(Repeat horizontalRepeat) {
            this.horizontalRepeat = horizontalRepeat;
            return this;
        }

        public Builder verticalRepeat(Repeat verticalRepeat) {
            this.verticalRepeat = verticalRepeat;
            return this;
        }

        public Builder multisampled(boolean multisampled) {
            this.multisampled = multisampled;
            return this;
        }

        public Builder borderColor(Vector4f borderColor) {
            this.borderColor.set(borderColor);
            return this;
        }

        public Builder file(String file) {
            this.file = file;
            return this;
        }

        public Texture build() {
            Texture texture = new Texture();
            if(file != null) {
                texture.loadFromFile(file);
            }else{
                texture.fromFormat(width,height,format,multisampled);
            }
            texture.setRepeat(horizontalRepeat, verticalRepeat);
            texture.setSamplerFilter(filterMin, filterMag);
            texture.setBorderColor(borderColor);
            return texture;
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
