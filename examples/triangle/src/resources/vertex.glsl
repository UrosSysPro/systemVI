#version 330

layout(location=0)in vec2 position;

void main(){
//    int float vec2 vec3 mat2 mat3

//    int id=gl_VertexID;
//    if(id==0){
//        gl_Position=vec4(0,0.5,0.0,1.0);
//    }
//if(id==1){
//    gl_Position=vec4(0.5,-0.5,0.0,1.0);
//}
//if(id==2){
//    gl_Position=vec4(-0.5,-0.5,0.0,1.0);
//}
    gl_Position=vec4(position,0.0,1.0);
}