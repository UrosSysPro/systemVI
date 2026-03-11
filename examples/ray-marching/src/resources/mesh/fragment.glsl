#version 330

in vec3 worldPosition;

out vec4 FragColor;

void main(){
    vec4 color = vec4(worldPosition/1000.0,1.0);
    color = mix(color,vec4(0.3,0.5,0.7,1.0),vec4(color.x<0.0,color.y<0.0,color.z<0.0,0.0));
    color = mix(color,vec4(0.3,0.5,0.7,1.0),vec4(color.x>1.0,color.y<1.0,color.z<1.0,0.0));
    FragColor = color;
}