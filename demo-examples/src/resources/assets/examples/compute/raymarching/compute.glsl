#version 430 core
#define RAY_ITERATIONS 300
#define DELTA_EPSILON 0.001
#define MAX_DISTANCE 1000.0
#define FIXED_MIN_TRAVEL_DISTANCE 0.2
#define MAX_BOUNCES 20
//in uvec3 gl_NumWorkGroups;
//in uvec3 gl_WorkGroupID;
//in uvec3 gl_LocalInvocationID;
//in uvec3 gl_GlobalInvocationID;
//in uint  gl_LocalInvocationIndex;

layout (local_size_x = 8, local_size_y = 8, local_size_z = 1) in;
uniform layout(binding=0,rgba32f) writeonly image2D outputTexture;
uniform mat4 view;


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

struct RayOutput{
    vec3 start;
    vec3 end;
    float traveled;
};

struct RayInput{
    vec3 origin;
    vec3 direction;
};

float map(in vec3 p){
    return unionSDF(
        unionSDF(
            box(
                translate(p,vec3(0.0,0.0,-10.0)),
                vec3(1)
            ),
            sphere(
                translate(p,vec3(2.0,0.0,-10.0)),
                1.0
            )
        ),
        plane(p,vec3(0.0,1.0,0.0),1.0)
    );
}
struct Material{
    vec4 color;
    float roughness;
    float metalic;
};
Material materialOf(vec4 color,float roughness,float metalic){
    Material m;
    m.color=color;
    m.roughness=roughness;
    m.metalic=metalic;
    return m;
}
Material getMaterial(in vec3 p){
    if(box(translate(p,vec3(0.0,0.0,-10.0)), vec3(1.0))<DELTA_EPSILON*2.0)return materialOf(vec4(0.3,0.6,0.9,1.0),1.0,0.5);
    if(sphere(translate(p,vec3(2.0,0.0,-10.0)), 1.0)<DELTA_EPSILON*2.0)return materialOf(vec4(0.9,0.6,0.3,1.0),0.3,0.7);
    if(plane(p,vec3(0.0,1.0,0.0),1.0)<DELTA_EPSILON*2.0)return materialOf(vec4(0.6,0.8,0.3,1.0),1.0,0.0);
    return materialOf(vec4(0.9,0.9,1.0,1.0),1.0,0.0);
//    return materialOf(vec4(0.0),1.0,0.0,1.0);
}
vec4 getColor(in vec3 p){
    if(box(translate(p,vec3(0.0,0.0,-10.0)), vec3(1.0))<DELTA_EPSILON*2.0)return vec4(0.3,0.6,0.9,1.0);
    if(sphere(translate(p,vec3(2.0,0.0,-10.0)), 1.0)<DELTA_EPSILON*2.0)return vec4(0.9,0.6,0.3,1.4);
    if(plane(p,vec3(0.0,1.0,0.0),1.0)<DELTA_EPSILON*2.0)return vec4(0.6,0.8,0.3,1.0);
    return vec4(0.3,0.6,0.9,1.0);
}


vec3 getNormal(in vec3 p){
    return normalize(vec3(
    map(p+vec3(DELTA_EPSILON,0.0,0.0))-map(p-vec3(DELTA_EPSILON,0.0,0.0)),
    map(p+vec3(0.0,DELTA_EPSILON,0.0))-map(p-vec3(0.0,DELTA_EPSILON,0.0)),
    map(p+vec3(0.0,0.0,DELTA_EPSILON))-map(p-vec3(0.0,0.0,DELTA_EPSILON))
    ));
}

void rayMarch(out float distance,out vec3 endPoint,out int numOfIterations,in vec3 rayOrigin,in vec3 rayDirection){
    distance=0;
    endPoint=vec3(rayOrigin);
    for(int i=0;i<RAY_ITERATIONS;i++){
        numOfIterations=i;
        float d=map(rayOrigin+distance*rayDirection);
        distance+=d;
        if(distance>MAX_DISTANCE||d<DELTA_EPSILON){
            numOfIterations=i;
            endPoint=rayOrigin+distance*rayDirection;
            return;
        }
    }
    numOfIterations=RAY_ITERATIONS;
    endPoint=rayOrigin+distance*rayDirection;
}


void phongData( in vec2 uv,in vec2 size,in vec3 lightPosition,
                out vec4 albedo,out vec3 cameraDirection,
                out vec3 lightDirection,out vec3 normal,out float shadow,out float cameraDistance,out float lightDistance
){
    //camera setup
    mat4 inverseView=inverse(view);
    vec4 helper;
    float f=2.0;

//    vec3 cameraPosition=-view[3].xyz;
    vec3 focusPoint=transform(inverseView,vec3(0.0,0.0,f));
    vec3 rayOrigin=transform(inverseView,vec3(uv,0.0));
    vec3 rayDirection=normalize(rayOrigin-focusPoint);
    cameraDirection=rayDirection;

    vec3 firstHit,secondHit;
    float distance;
    int numOfIterations;
    //ray march camra to scene
    rayMarch(distance,firstHit,numOfIterations,rayOrigin,rayDirection);
    cameraDistance=distance;
    albedo=getColor(firstHit);
    normal=getNormal(firstHit);
    lightDirection=normalize(firstHit-lightPosition);
    //ray march scene to light
    rayOrigin=firstHit-rayDirection*DELTA_EPSILON*2.0;
    rayDirection=normalize(lightPosition-rayOrigin);
    lightDistance=length(lightPosition-firstHit);
    rayMarch(distance,secondHit,numOfIterations,rayOrigin,rayDirection);
    shadow=mix(1.0,0.4,float(distance<length(firstHit-lightPosition)));
}
//
//void phongWithReflectionsData(
//    vec2 uv,vec2 size,out RayOutput cameraToScene,out RayOutput sceneToLight,out RayOutput sceneReflection,vec3 lightPosition
//){
//    //camera setup
//    mat4 inverseView=inverse(view);
//    float f=2.0;
//
//    vec3 focusPoint=transform(inverseView,vec3(0.0,0.0,f));
//    vec3 rayOrigin=transform(inverseView,vec3(uv,0.0));
//    vec3 rayDirection=normalize(rayOrigin-focusPoint);
//    vec3 cameraDirection=rayDirection;
//
//    vec3 hitPoint;
//    float traveled;
//    int numOfIterations;
//
//    rayMarch(traveled,hitPoint,numOfIterations,rayOrigin,rayDirection);
//    cameraToScene.start=rayOrigin;
//    cameraToScene.traveled=traveled;
//    cameraToScene.end=hitPoint;
//
//
//    rayOrigin=hitPoint-2.0*DELTA_EPSILON*rayDirection;
//    rayDirection=normalize(lightPosition-hitPoint);
//    rayMarch(traveled,hitPoint,numOfIterations,rayOrigin,rayDirection);
//    sceneToLight.start=rayOrigin;
//    sceneToLight.traveled=traveled;
//    sceneToLight.end=lightPosition;
//
//    vec3 normal=getNormal(cameraToScene.end);
//    rayOrigin=cameraToScene.end+2.0*DELTA_EPSILON*normal;
//    rayDirection=reflect(cameraDirection,normal);
//
//    rayMarch(traveled,hitPoint,numOfIterations,rayOrigin,rayDirection);
//    sceneReflection.start=rayOrigin;
//    sceneReflection.traveled=traveled;
//    sceneReflection.end=hitPoint;
//}
void phongReflectionData(in vec2 uv,in vec2 size,out vec3[MAX_BOUNCES] points,int n){
    mat4 inverseView=inverse(view);
    float f=2.0;
    vec3 focusPoint=transform(inverseView,vec3(0.0,0.0,f));
    vec3 rayOrigin=transform(inverseView,vec3(uv,0.0));
    vec3 rayDirection=normalize(rayOrigin-focusPoint);
    for(int i=0;i<n;i++)points[i]=vec3(0,0,0);
    points[0]=rayOrigin;
    for(int i=1;i<n;i++){
        float distance;
        int numberOfIterations;
        rayMarch(distance,points[i],numberOfIterations,rayOrigin,rayDirection);
        if(distance>MAX_DISTANCE-DELTA_EPSILON)break;
        vec3 normal=getNormal(points[i]);
        rayOrigin=points[i]+normal*DELTA_EPSILON*2.0;
        rayDirection=reflect(rayDirection,normal);
    }
}

void pbrData(in vec2 uv,in vec2 size){

}

vec4 phong(vec4 lightColor,vec3 atenuation,vec4 albedo,vec3 cameraDirection,vec3 lightDirection,vec3 normal,float shadow,float cameraDistance,float lightDistance){
    float ambient=0.2;
    float diffuse=max(dot(normal,-lightDirection),0.0);
    float specular=pow(max(dot(-lightDirection,reflect(cameraDirection,normal)),0.0),64.0);
    float fallOff=atenuation.x*lightDistance*lightDistance+atenuation.y*lightDistance+atenuation.z;
    return albedo*(ambient+max(shadow*(diffuse+specular)/fallOff,0.0));
}

vec4 phongReflection(vec2 uv,vec2 size,vec3[MAX_BOUNCES] points,int n,vec3 lightPosition,vec4 lightColor,vec3 atenuation){
    vec4 color=vec4(1.0);

    mat4 inverseView=inverse(view);
    float f=2.0;
    vec3 focusPoint=transform(inverseView,vec3(0.0,0.0,f));
    vec3 cameraPosition=transform(inverseView,vec3(uv,0.0));
    vec3 cameraDirection=normalize(cameraPosition-focusPoint);

    for(int i=1;i<n;i++){
        Material m=getMaterial(points[i]);
        color*=m.color;
    }
    return color;
}
//
//vec4 phongReflections(vec2 uv,vec2 size,
//    RayOutput cameraToScene,RayOutput sceneToLight,RayOutput sceneReflection,vec3 lightPosition,vec4 lightColor,vec3 atenuation
//){
//
////    mat4 inverseView=inverse(view);
////    float f=2.0;
////
////    vec3 focusPoint=transform(inverseView,vec3(0.0,0.0,f));
////    vec3 rayOrigin=transform(inverseView,vec3(uv,0.0));
////    vec3 rayDirection=normalize(rayOrigin-focusPoint);
////    vec3 cameraDirection=rayDirection;
//
//    vec3 normal=getNormal(cameraToScene.end);
//    vec3 lightDirection=normalize(sceneToLight.start-sceneToLight.end);
//    float lightDistance=length(sceneToLight.end-sceneToLight.start);
//    vec3 cameraDirection=normalize(cameraToScene.end-cameraToScene.start);
//    vec4 albedo=getColor(cameraToScene.end);
//    float shadow=mix(1.0,0.0,float(sceneToLight.traveled<lightDistance));
//    float ambient=0.2;
//    float diffuse=max(dot(normal,-lightDirection),0.0);
//    float specular=pow(max(dot(-lightDirection,reflect(cameraDirection,normal)),0.0),64.0);
//    float fallOff=atenuation.x*lightDistance*lightDistance+atenuation.y*lightDistance+atenuation.z;
//    vec4 normalColor=albedo*(ambient+(diffuse+specular)*shadow/fallOff);
////    vec4 bounceAlbedo=getColor(sceneReflection.end);
////    vec4 bounceColor=normalColor*bounceAlbedo;
////    float rspec = 1.45*(dot(cameraDirection,normal)+1);
////    return albedo*(ambient+(diffuse+specular)*shadow/fallOff)+(bounceAlbedo*rspec+0.0);
//    lightDirection=normalize(sceneReflection.end-lightPosition);
//    cameraDirection=normalize(sceneReflection.end-cameraToScene.start);
//    albedo=getColor(sceneReflection.end);
//    normal=getNormal(sceneReflection.end);
//    diffuse=max(dot(normal,-lightDirection),0.0);
//    specular=pow(max(dot(-lightDirection,reflect(cameraDirection,normal)),0.0),64.0);
//    vec4 bounceColor=albedo*(ambient+(diffuse+specular));
//
//    //    vec4 bounceAlbedo=getColor(sceneReflection.end);
//    //    vec4 bounceColor=normalColor*bounceAlbedo;
//    cameraDirection=normalize(cameraToScene.end-cameraToScene.start);
//    normal=getNormal(cameraToScene.end);
//        float rspec = 1.45 * (dot(cameraDirection,normal)+1);//1.45*(dot(cameraDirection,normal)+1);
//        return normalColor*(bounceColor*rspec+1.0);
////    return bounceColor;
//}

vec4 pbr(){
    return vec4(0.0);
}

vec4 calculateColor(vec2 uv,vec2 size){
//    vec3 lightPosition=vec3(5.0,5.0,-5.0);
//    vec4 lightColor=vec4(2.0);
//    vec4 albedo;
//    vec3 cameraDirection, lightDirection, normal;
//    float shadow,cameraDistance,lightDistance;
//    phongData(uv,size,lightPosition,albedo,cameraDirection,lightDirection,normal,shadow,cameraDistance,lightDistance);
//    return phong(lightColor,vec3(0.01,0.01,1.0),albedo,cameraDirection,lightDirection,normal,shadow,cameraDistance,lightDistance);

    //RayOutput cameraToScene
    //RayOutput sceneToLight
    //RayOutput sceneReflection
//    vec3 lightPosition=vec3(5.0,5.0,-5.0);
//    vec4 lightColor=vec4(2.0);
//    vec3 atenuation=vec3(0.01,0.01,1.0);
//    RayOutput cameraToScene,sceneToLight,sceneReflection;
//    phongWithReflectionsData(uv,size,cameraToScene,sceneToLight,sceneReflection,lightPosition);
////    return vec4(0);
//    return phongReflections(uv,size,cameraToScene,sceneToLight,sceneReflection,lightPosition,lightColor,atenuation);

    vec3[MAX_BOUNCES] points;
    int n=5;
    vec3 lightPosition=vec3(5.0,5.0,-5.0);
    vec4 lightColor=vec4(2.0);
    vec3 atenuation=vec3(0.01,0.01,1.0);
    phongReflectionData(uv,size,points,n);
    return phongReflection(uv,size,points,n,lightPosition,lightColor,atenuation);
}


void main() {
    ivec2 texelCoord=ivec2(gl_GlobalInvocationID.xy);
    ivec2 textureSize=imageSize(outputTexture);

    vec2 size=vec2(textureSize);
    vec2 uv=vec2(texelCoord)/size;
    uv-=vec2(0.5);
    float aspect=size.x/size.y;
    uv.x*=aspect;
    //uv size

    imageStore(outputTexture,texelCoord,calculateColor(uv,size));
}