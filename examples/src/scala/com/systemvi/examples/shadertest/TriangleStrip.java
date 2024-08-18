package com.systemvi.examples.shadertest;

import com.systemvi.engine.application.Game;
import com.systemvi.engine.model.Mesh;
import com.systemvi.engine.model.VertexAttribute;
import com.systemvi.engine.shader.Primitive;
import com.systemvi.engine.shader.Shader;
import com.systemvi.engine.utils.Utils;
import com.systemvi.engine.window.Window;
//import jdk.jshell.execution.Util;

public class TriangleStrip extends Game {
    public TriangleStrip() {
        super(3,3,60,800,600,"Triangle strip test");
    }
    private Shader shader;
    private Mesh mesh;
    @Override
    public void setup(Window window) {
        shader= Shader.builder()
            .fragment("assets/examples/shadertest/triangleStrip/fragment.glsl")
            .vertex("assets/examples/shadertest/triangleStrip/vertex.glsl")
            .build();
        System.out.println(shader.getLog());
        mesh=new Mesh(new VertexAttribute("random",1));
        mesh.setVertexData(new float[]{1,1,1,1});
    }

    @Override
    public void loop(float delta) {
        Utils.clear(0,0,0,0, Utils.Buffer.COLOR_BUFFER);
        shader.use();
//        mesh.draw();
        mesh.bind();
        shader.drawArrays(Primitive.TRIANGLE_STRIP,4);
    }
}
