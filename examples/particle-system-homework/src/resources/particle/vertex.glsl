#version 330


uniform mat4 view;
uniform mat4 projection;

layout(location=0)in vec2 position;
layout(location=1)in vec4 color;
layout(location=2)in float alpha;

out vec4 vertexColor;

mat3 rotation(float angle){
    return mat3(
            vec3(cos(angle),sin(angle),0),
            vec3(-sin(angle),cos(angle),0),
            vec3(0,0,1)
    );
}
mat3 translation(vec2 p){
    return mat3(
            vec3(1,0,0),
            vec3(0,1,0),
            vec3(p.x,p.y,1)
    );
}

void main(){
    vertexColor=vec4(color.xyz,alpha);
//    vertexColor=color;
    vec3 point=vec3(position,1.0);

    gl_Position=projection*view*vec4(position,0.0,1.0);
}