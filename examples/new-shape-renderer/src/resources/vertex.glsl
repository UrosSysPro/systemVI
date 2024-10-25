#version 330

layout(location=0) in vec2 position;
layout(location=1) in vec4 color;
layout(location=2) in vec4 col0;
layout(location=3) in vec4 col1;
layout(location=4) in vec4 col2;
layout(location=5) in vec4 col3;


uniform mat4 view;
uniform mat4 projection;

out vec4 vColor;

void main(){
    vColor=color;
    mat4 model=mat4(col0,col1,col2,col3);
    gl_Position=projection*view*model*vec4(position,0.0,1.0);
}
