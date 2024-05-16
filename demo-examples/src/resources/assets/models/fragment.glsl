#version 330 core

out vec4 FragColor;

in VERTEX_OUT{
    vec3 tangent,bitangent,normal;
}vertexIn;

void main(){
//    FragColor=vec4(0.9764,0.4509,0.0862,1);
    FragColor=vec4(vertexIn.normal*0.5+0.5,1);
}