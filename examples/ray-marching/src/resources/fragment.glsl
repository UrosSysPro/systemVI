#version 330

struct Camera{
    float fi,aspect,yaw,pitch;
    vec3 position;
    mat4 view;
};

uniform Camera camera;
in vec3 uv;
out vec4 FragColor;

float sphereSdf(float r, vec3 p){
    return length(p)-r;
}

float unionSdf(float d1,float d2){
    return min(d1,d2);
}

float differenceSdf(float d1,float d2){
    return max(d1,-d2);
}

vec3 translateSdf(vec3 t,vec3 p){
    return p-t;
}

float sdf(vec3 p){
    return differenceSdf(
        differenceSdf(
            sphereSdf(100.0,
                translateSdf(
                    vec3(0,0,-300),
                    p
                )
            ),
            sphereSdf(50.0,
                translateSdf(
                    vec3(70,0,-200),
                    p
                )
            )
        ),
        sphereSdf(50.0,
            translateSdf(
                vec3(-70,0,-200),
                p
            )
        )
    );
}

float rayMarch(vec3 rayOrigin, vec3 rayDirection){
    float d=0;
    for(int i=0;i<100;i++){
        vec3 p=rayOrigin+d*rayDirection;
        float step=sdf(p);
        d+=step;
        if(d<0.001 || d>1000.0)break;
    }
    return min(d,1000.0);
}

void main(){
    vec4 focalPoint = camera.view * vec4(0.0,0.0,camera.fi,1.0);
    vec4 screenPoint = camera.view * vec4(uv.x*camera.aspect,uv.y,0.0,1.0);

    vec3 rayOrigin = focalPoint.xyz;
    vec3 rayDirection = normalize(screenPoint.xyz - rayOrigin);

    float d = rayMarch(rayOrigin, rayDirection);

    FragColor = vec4(clamp(1.0 - d/1000.0 ,0.0,1.0));
}
