#version 330

out vec4 FragColor;
uniform sampler2D textureBuff;
uniform vec2 size;
in vec2 uv;

void main(){
    int n=10;
    vec4 average=vec4(0.0);
    for(int i=-n;i<=n;i++){
        for(int j=-n;j<=n;j++){
            vec2 samplePoint=uv+vec2(float(i)/size.x,float(j)/size.y);
            average+=texture(textureBuff,samplePoint);
        }
    }
    average/=float((n*2+1)*(n*2+1));
    FragColor=average;
}