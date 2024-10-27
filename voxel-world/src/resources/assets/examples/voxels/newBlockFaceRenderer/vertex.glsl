#version 330

layout(location=0) in vec3 position;
layout(location=1) in float sideIndex;

uniform mat4 view;
uniform mat4 projection;

//enum BlockSide(val index:Int):
//  case Left extends BlockSide(0)
//  case Right extends BlockSide(1)
//  case Top extends BlockSide(2)
//  case Bottom extends BlockSide(3)
//  case Front extends BlockSide(4)
//  case Back extends BlockSide(5)

void main(){
    int index=int(sideIndex);
    
    gl_Position=projection*view*vec4(position,1);
}