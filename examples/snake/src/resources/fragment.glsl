#version 330
in vec4 vColor;
in vec2 textureCoords;
out vec4 FragColor;
uniform sampler2D tiles;

void main(){
    FragColor=texture(tiles,textureCoords)*vColor;
//    FragColor=vec4(textureCoords,0.0,0.0);
}