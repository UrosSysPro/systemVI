#version 410 core
#define PI 3.14159265358979323846
uniform ivec2 grid;
uniform mat4 view;
uniform mat4 projection;
uniform mat4 model;

out struct VERTEX_DATA{
    vec2 uv;
    vec3 worldPosition;
    vec3 modelPosition;
}vertexOut;


float rand(vec2 c){
    return fract(sin(dot(c.xy ,vec2(12.9898,78.233))) * 43758.5453);
}
float screenWidth=2;
float noise(vec2 p, float freq ){
    float unit = screenWidth/freq;
    vec2 ij = floor(p/unit);
    vec2 xy = mod(p,unit)/unit;
    //xy = 3.*xy*xy-2.*xy*xy*xy;
    xy = .5*(1.-cos(PI*xy));
    float a = rand((ij+vec2(0.,0.)));
    float b = rand((ij+vec2(1.,0.)));
    float c = rand((ij+vec2(0.,1.)));
    float d = rand((ij+vec2(1.,1.)));
    float x1 = mix(a, b, xy.x);
    float x2 = mix(c, d, xy.x);
    return mix(x1, x2, xy.y);
}

float pNoise(vec2 p, int res){
    float persistance = .5;
    float n = 0.;
    float normK = 0.;
    float f = 4.;
    float amp = 1.;
    int iCount = 0;
    for (int i = 0; i<50; i++){
        n+=amp*noise(p, f);
        f*=2.;
        normK+=amp;
        amp*=persistance;
        if (iCount == res) break;
        iCount++;
    }
    float nf = n/normK;
    return nf*nf*nf*nf;
}

void main(){
    ivec2 quadrant;
    quadrant.x=gl_VertexID%(2*grid.x+2)/2;
    quadrant.y=gl_VertexID/(2*grid.x+2);
    int index=gl_VertexID%(2*grid.x+2);
    if(index==grid.x*2){
        quadrant.x=grid.x-1;
        quadrant.y++;
    }
    if(index==grid.x*2+1){
        quadrant.x=0;
    }

    vec2 gridPos=vec2(0,index%2)+vec2(quadrant);

    vec4 point=vec4(gridPos,0,1);
    point/=vec4(grid-1,1,1);
    vec2 uv=point.xy;
    point-=vec4(0.5,0.5,0.0,0.0);
    point.z+=noise(uv,10)*0.3;
    vertexOut.modelPosition=point.xyz;

    vec4 worldPosition=model*point;

    vertexOut.worldPosition=worldPosition.xyz;

    vertexOut.uv=uv;

    gl_Position=projection*view*worldPosition;
}