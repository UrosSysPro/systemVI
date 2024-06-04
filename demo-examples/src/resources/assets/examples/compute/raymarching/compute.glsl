#version 430 core
#define RAY_ITERATIONS 500
#define SAMPLES 1
#define DELTA_EPSILON 0.01
#define MAX_DISTANCE 1000.0
#define MAX_BOUNCES 100

//in uvec3 gl_NumWorkGroups;
//in uvec3 gl_WorkGroupID;
//in uvec3 gl_LocalInvocationID;
//in uvec3 gl_GlobalInvocationID;
//in uint  gl_LocalInvocationIndex;

layout (local_size_x = 8, local_size_y = 8, local_size_z = 1) in;
uniform layout(binding=0,rgba32f) image2D outputTexture;
uniform mat4 view;
uniform float time;

float sphere( vec3 p, float s )
{
    return length(p)-s;
}
float box( vec3 p, vec3 b )
{
    vec3 q = abs(p) - b;
    return length(max(q,0.0)) + min(max(q.x,max(q.y,q.z)),0.0);
}
float torus( vec3 p, vec2 t )
{
    vec2 q = vec2(length(p.xz)-t.x,p.y);
    return length(q)-t.y;
}
float plane( vec3 p, vec3 n, float h )
{
    return dot(p,n) + h;
}
float unionSDF( float d1, float d2 )
{
    return min(d1,d2);
}
float unionSDF( float d1, float d2, float d3 )
{
    return unionSDF(d1,unionSDF(d2,d3));
}
float unionSDF( float d1, float d2, float d3, float d4)
{
    return unionSDF(d1,unionSDF(d2,unionSDF(d3,d4)));
}
float subtraction( float d1, float d2 )
{
    return max(-d1,d2);
}
float intersection( float d1, float d2 )
{
    return max(d1,d2);
}
float xor(float d1, float d2 )
{
    return max(min(d1,d2),-max(d1,d2));
}
float smoothUnion( float d1, float d2, float k )
{
    float h = clamp( 0.5 + 0.5*(d2-d1)/k, 0.0, 1.0 );
    return mix( d2, d1, h ) - k*h*(1.0-h);
}

float smoothSubtraction( float d1, float d2, float k )
{
    float h = clamp( 0.5 - 0.5*(d2+d1)/k, 0.0, 1.0 );
    return mix( d2, -d1, h ) + k*h*(1.0-h);
}

float smoothIntersection( float d1, float d2, float k )
{
    float h = clamp( 0.5 - 0.5*(d2-d1)/k, 0.0, 1.0 );
    return mix( d2, d1, h ) + k*h*(1.0-h);
}
vec3 translate(in vec3 p,in vec3 t){
    return p-t;
}

vec3 transform(in mat4 mat,in vec3 p){
    vec4 helper=mat*vec4(p,1.0);
    return helper.xyz;
}

struct Material{
    float roughness;
    float metalic;
    vec4 color;
    vec4 emission;
};
Material materialOf(float roughness,float metalic,vec4 color,vec4 emissive){
    Material m;
    m.color=color;
    m.roughness=roughness;
    m.metalic=metalic;
    m.emission=emissive;
    return m;
}
struct Sphere{
    vec3 position;
    float radius;
    Material material;
};
Sphere sphereOf(vec3 position,Material material,float radius){
    Sphere b;
    b.position=position;
    b.material=material;
    b.radius=radius;
    return b;
}
#define SPHERES 4
Sphere[SPHERES] spheres={
    sphereOf(vec3( 0.0,0.0,-10.0),materialOf(0.0,0.0,vec4(0.0),vec4(0.4,0.7,1.0,1.0)),1.0),
    sphereOf(vec3( 2.5,0.0,-10.0),materialOf(0.7,1.0,vec4(1.0,0.2,0.3,1.0),vec4(0.0)),1.0),
    sphereOf(vec3( -2.5,0.0,-10.0),materialOf(1.0,1.0,vec4(0.2,0.4,0.9,1.0),vec4(0.0)),1.0),
    sphereOf(vec3( 10.0,5.0,-10.0),materialOf(0.0,0.0,vec4(0.0),vec4(4.0,7.0,10.0,1.0)),2.0)
};

Material getMaterial(in vec3 p){
    Material gray=materialOf(0.9,0.0,vec4(0.5),vec4(0.0));
    Material sky=materialOf(0,0,vec4(0.0),vec4(0.0));
    for(int i=0;i<SPHERES;i++){
        if(sphere(translate(p,spheres[i].position),spheres[i].radius)<DELTA_EPSILON)return spheres[i].material;
    }
    if(plane(p,vec3(0.0,1.0,0.0),1.0)<DELTA_EPSILON)return gray;
    return sky;
}
float map(in vec3 p){
    float d=MAX_DISTANCE;
    for(int i=0;i<SPHERES;i++){
        d=unionSDF(
            d, sphere(translate(p,spheres[i].position),spheres[i].radius)
        );
    }
    return unionSDF(d,plane(p,vec3(0.0,1.0,0.0),1.0));
}

vec3 getNormal(in vec3 p){
    return normalize(vec3(
        map(p+vec3(DELTA_EPSILON,0.0,0.0))-map(p-vec3(DELTA_EPSILON,0.0,0.0)),
        map(p+vec3(0.0,DELTA_EPSILON,0.0))-map(p-vec3(0.0,DELTA_EPSILON,0.0)),
        map(p+vec3(0.0,0.0,DELTA_EPSILON))-map(p-vec3(0.0,0.0,DELTA_EPSILON))
    ));
}

void rayMarch(out float distance,out vec3 endPoint,out int n,in vec3 rayOrigin,in vec3 rayDirection){
    distance=0;
    for(int i=0;i<RAY_ITERATIONS;i++){
        float d=map(rayOrigin+distance*rayDirection);
        distance+=d;
        if(distance>MAX_DISTANCE||d<DELTA_EPSILON){
            n=i;
            endPoint=rayOrigin+distance*rayDirection;
            return;
        }
    }
    n=RAY_ITERATIONS;
    endPoint=rayOrigin+distance*rayDirection;
}

float rand(float co) { return abs(fract(sin((co+time)*(91.3458)) * 47453.5453)); }
float rand(vec2 co){ return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453); }
float rand(vec3 co){ return rand(co.xy+rand(co.z)); }

vec4 simulatePhoton(vec2 uv,vec2 size,int sampleID){

    mat4 inverted=inverse(view);
    vec4 focus=inverted*vec4(0.0,0.0,2.2,1.0);
    vec4 point=inverted*vec4(uv,0.0,1.0);
    vec3 rayOrigin=vec3(focus.xyz);
    vec3 rayDirection=normalize(vec3(point.xyz)-rayOrigin);

    Material[MAX_BOUNCES] materials;
    int i;
    for(i=0;i<MAX_BOUNCES;i++){
        float d;
        vec3 p;
        int n;
        rayMarch(d,p,n,rayOrigin,rayDirection);
        vec3 normal=getNormal(p);
        Material material=getMaterial(p);
        rayOrigin=p+3.0*DELTA_EPSILON*normal;
        vec3 reflectedDirection=normalize(
        reflect(rayDirection,normal)+
            normalize(vec3(
                rand(vec3(uv,float(sampleID+3*i+1)))*2.0-1.0,
                rand(vec3(uv,float(sampleID+3*i+2)))*2.0-1.0,
                rand(vec3(uv,float(sampleID+3*i+3)))*2.0-1.0
            ))*material.roughness
        );
        vec3 diffuseDirection=normalize(
            normal+normalize(vec3(
                rand(vec3(uv,float(sampleID+3*i+1)))*2.0-1.0,
                rand(vec3(uv,float(sampleID+3*i+2)))*2.0-1.0,
                rand(vec3(uv,float(sampleID+3*i+3)))*2.0-1.0
            ))
        );
        rayDirection=mix(diffuseDirection,reflectedDirection,material.metalic);
//        color*=material.color;

        materials[i]=material;

        if(d>MAX_DISTANCE)break;
    }
    vec4 color=vec4(1.0);
    for(int j=0;j<=i;j++){
        color*=materials[i-j].color;
        color+=materials[i-j].emission;
    }
    return color;
}

vec4 calculateColor(vec2 uv,vec2 size){
    vec4 color;
    for(int i=0;i<SAMPLES;i++){
        color+=simulatePhoton(uv,size,i);
    }
    color/=float(SAMPLES);
    float gamma=1.0/2.2;
    return vec4(
        pow(color.r,gamma),
        pow(color.g,gamma),
        pow(color.b,gamma),
        1.0
    );
}

void main() {
    ivec2 texelCoord=ivec2(gl_GlobalInvocationID.xy);
    ivec2 textureSize=imageSize(outputTexture);

    vec2 size=vec2(textureSize);
    vec2 uv=(vec2(texelCoord)/size-vec2(0.5))*vec2(size.x/size.y,1.0);
    vec4 value=imageLoad(outputTexture,texelCoord);
    imageStore(outputTexture,texelCoord,mix(calculateColor(uv,size),value,0.95));
}