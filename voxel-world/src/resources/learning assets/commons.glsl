// ----------------------------------------------------------------
// Defines
// ----------------------------------------------------------------
// - Scene can go from 0 to 2
// - The furnace_test show the energy loss, the image should be
//   all white in a perfect pathtracer
//
// ----------------------------------------------------------------
#define SCENE 0
#define FURNACE_TEST 0
#define CAMERA_SENSITIVTY .01
#define FOCAL_LENGTH 2.5


// ---------------------------------------------
// Hash & Random
// From iq
// ---------------------------------------------
int   seed = 1;
int   rand(void) { seed = seed*0x343fd+0x269ec3; return (seed>>16)&32767; }
float frand() { return float(rand())/32767.0; }
vec2 frand2() { return vec2(frand(), frand()); }
vec3 frand3() { return vec3(frand(), frand(), frand()); }
void  srand( ivec2 p, int frame )
{
    int n = frame;
    n = (n<<13)^n; n=n*(n*n*15731+789221)+1376312589; // by Hugo Elias
    n += p.y;
    n = (n<<13)^n; n=n*(n*n*15731+789221)+1376312589;
    n += p.x;
    n = (n<<13)^n; n=n*(n*n*15731+789221)+1376312589;
    seed = n;
}
vec3 hash3(vec3 p) {
    uvec3 x = uvec3(floatBitsToUint(p));
    const uint k = 1103515245U;
    x = ((x>>8U)^x.yzx)*k;
    x = ((x>>8U)^x.yzx)*k;
    x = ((x>>8U)^x.yzx)*k;

    return vec3(x)*(1.0/float(0xffffffffU));
}








// ---------------------------------------------
// Maths
// ---------------------------------------------
#define saturate(x) clamp(x,0.,1.)
#define PI 3.141592653589

mat3 lookat(vec3 ro, vec3 ta)
{
    const vec3 up = vec3(0.,1.,0.);
    vec3 fw = normalize(ta-ro);
    vec3 rt = normalize( cross(fw, normalize(up)) );
    return mat3( rt, cross(rt, fw), fw );
}

mat2 rot(float v) {
    float a = cos(v);
    float b = sin(v);
    return mat2(a,b,-b,a);
}

// From fizzer - https://web.archive.org/web/20170610002747/http://amietia.com/lambertnotangent.html
vec3 cosineSampleHemisphere(vec3 n)
{
    vec2 rnd = frand2();

    float a = PI*2.*rnd.x;
    float b = 2.0*rnd.y-1.0;

    vec3 dir = vec3(sqrt(1.0-b*b)*vec2(cos(a),sin(a)),b);
    return normalize(n + dir);
}

// From pixar - https://graphics.pixar.com/library/OrthonormalB/paper.pdf
void basis(in vec3 n, out vec3 b1, out vec3 b2)
{
    if(n.z<0.){
        float a = 1.0 / (1.0 - n.z);
        float b = n.x * n.y * a;
        b1 = vec3(1.0 - n.x * n.x * a, -b, n.x);
        b2 = vec3(b, n.y * n.y*a - 1.0, -n.y);
    }
    else{
        float a = 1.0 / (1.0 + n.z);
        float b = -n.x * n.y * a;
        b1 = vec3(1.0 - n.x * n.x * a, b, -n.x);
        b2 = vec3(b, 1.0 - n.y * n.y * a, -n.y);
    }
}

vec3 toWorld(vec3 x, vec3 y, vec3 z, vec3 v)
{
    return v.x*x + v.y*y + v.z*z;
}

vec3 toLocal(vec3 x, vec3 y, vec3 z, vec3 v)
{
    return vec3(dot(v, x), dot(v, y), dot(v, z));
}









// ---------------------------------------------
// Color
// ---------------------------------------------
vec3 RGBToYCoCg(vec3 rgb)
{
    float y  = dot(rgb, vec3(  1, 2,  1 )) * 0.25;
    float co = dot(rgb, vec3(  2, 0, -2 )) * 0.25 + ( 0.5 * 256.0/255.0 );
    float cg = dot(rgb, vec3( -1, 2, -1 )) * 0.25 + ( 0.5 * 256.0/255.0 );
    return vec3(y, co, cg);
}

vec3 YCoCgToRGB(vec3 ycocg)
{
    float y = ycocg.x;
    float co = ycocg.y - ( 0.5 * 256.0 / 255.0 );
    float cg = ycocg.z - ( 0.5 * 256.0 / 255.0 );
    return vec3(y + co-cg, y + cg, y - co-cg);
}

float luma(vec3 color) {
    return dot(color, vec3(0.299, 0.587, 0.114));
}









// ---------------------------------------------
// Microfacet
// ---------------------------------------------
vec3 F_Schlick(vec3 f0, float theta) {
    return f0 + (1.-f0) * pow(1.0-theta, 5.);
}

float F_Schlick(float f0, float f90, float theta) {
    return f0 + (f90 - f0) * pow(1.0-theta, 5.0);
}

float D_GTR(float roughness, float NoH, float k) {
    float a2 = pow(roughness, 2.);
    return a2 / (PI * pow((NoH*NoH)*(a2*a2-1.)+1., k));
}

float SmithG(float NDotV, float alphaG)
{
    float a = alphaG * alphaG;
    float b = NDotV * NDotV;
    return (2.0 * NDotV) / (NDotV + sqrt(a + b - a * b));
}

float GeometryTerm(float NoL, float NoV, float roughness)
{
    float a2 = roughness*roughness;
    float G1 = SmithG(NoV, a2);
    float G2 = SmithG(NoL, a2);
    return G1*G2;
}

vec3 SampleGGXVNDF(vec3 V, float ax, float ay, float r1, float r2)
{
    vec3 Vh = normalize(vec3(ax * V.x, ay * V.y, V.z));

    float lensq = Vh.x * Vh.x + Vh.y * Vh.y;
    vec3 T1 = lensq > 0. ? vec3(-Vh.y, Vh.x, 0) * inversesqrt(lensq) : vec3(1, 0, 0);
    vec3 T2 = cross(Vh, T1);

    float r = sqrt(r1);
    float phi = 2.0 * PI * r2;
    float t1 = r * cos(phi);
    float t2 = r * sin(phi);
    float s = 0.5 * (1.0 + Vh.z);
    t2 = (1.0 - s) * sqrt(1.0 - t1 * t1) + s * t2;

    vec3 Nh = t1 * T1 + t2 * T2 + sqrt(max(0.0, 1.0 - t1 * t1 - t2 * t2)) * Vh;

    return normalize(vec3(ax * Nh.x, ay * Nh.y, max(0.0, Nh.z)));
}

float GGXVNDFPdf(float NoH, float NoV, float roughness)
{
    float D = D_GTR(roughness, NoH, 2.);
    float G1 = SmithG(NoV, roughness*roughness);
    return (D * G1) / max(0.00001, 4.0f * NoV);
}









// ---------------------------------------------
// Sky simulation
// ---------------------------------------------
float iSphere(vec3 ro, vec3 rd, float radius) {
    float b = 2.0 * dot(rd, ro);
    float c = dot( ro, ro ) - radius * radius;
    float disc = b * b - 4.0 * c;
    if (disc < 0.0)
    return (-1.0);
    float q = (-b + ((b < 0.0) ? -sqrt(disc) : sqrt(disc))) / 2.0;
    float t0 = q;
    float t1 = c / q;
    return max(t0,t1);//vec2(t0,t1);
}

vec3 skyColor(vec3 rd, vec3 sundir)
{
    #if FURNACE_TEST
    return vec3(1.);
    #endif
    rd.y = max(rd.y, .03);
    const int nbSamples = 16;
    const int nbSamplesLight = 16;

    vec3 absR = vec3(3.8e-6f, 13.5e-6f, 33.1e-6f);
    vec3 absM = vec3(21e-6f);


    vec3 accR = vec3(0.);
    vec3 accM = vec3(0.);

    float mu = dot(rd, sundir); // mu in the paper which is the cosine of the angle between the sun direction and the ray direction
    float g = 0.76f;
    vec2 phase = vec2(3.f / (16.f * PI) * (1. + mu * mu), 3.f / (8.f * PI) * ((1.f - g * g) * (1.f + mu * mu)) / ((2.f + g * g) * pow(1.f + g * g - 2.f * g * mu, 1.5f)));

    float radA = 6420e3;
    float radE = 6360e3;
    vec3 ro = vec3(0., radE+1., 0.);
    float t = iSphere(ro, rd, radA);
    float stepSize = t / float(nbSamples);

    vec2 opticalDepth = vec2(0.);

    for(int i=0; i<nbSamples; i++) {
        vec3 p = ro + rd * (float(i)+.5) * stepSize;

        float h = length(p) - radE;
        vec2 thickness = vec2(exp(-h/7994.), exp(-h/1200.)) * stepSize;
        opticalDepth += thickness;

        float tl = iSphere(p, sundir, radA);
        float stepSizeLight = tl / float(nbSamplesLight);
        vec2 opticalDepthLight = vec2(0.);
        int j;
        for(j=0; j<nbSamplesLight; j++) {
            vec3 pl = p + sundir * (float(j)+.5) * stepSizeLight;
            float hl = length(pl) - radE;
            if (hl < 0.) break;
            opticalDepthLight += vec2(exp(-hl/7994.), exp(-hl/1200.)) * stepSizeLight;
        }
        if (j == nbSamplesLight) {
            vec3 tau = absR * (opticalDepth.x + opticalDepthLight.x) + absM * 1.1 * (opticalDepth.y + opticalDepthLight.y);
            vec3 att = exp(-tau);
            accR += att * thickness.x ;
            accM += att * thickness.y;
        }
    }

    vec3 col = min((accR * absR * phase.x + accM * absM * phase.y)*10., vec3(1.));
    return col;
}









// ---------------------------------------------
// Data IO
// ---------------------------------------------
struct Data {
    float theta;
    float phi;
    float r;

    vec3 ro;
    vec3 ta;

    vec3 oldRo;
    vec3 oldTa;

    vec4 oldMouse;

    float refreshTime;
};

float readData1(sampler2D tex, int id) {
    return texelFetch(tex, ivec2(id,0), 0).r;
}
vec3 readData3(sampler2D tex, int id) {
    return texelFetch(tex, ivec2(id,0), 0).rgb;
}
vec4 readData4(sampler2D tex, int id) {
    return texelFetch(tex, ivec2(id,0), 0);
}
vec4 writeData(vec4 col, vec2 fragCoord, int id, float value) {
    if (floor(fragCoord.x) == float(id))
    col.r = value;

    return col;
}
vec4 writeData(vec4 col, vec2 fragCoord, int id, vec3 value) {
    if (floor(fragCoord.x) == float(id))
    col.rgb = value.rgb;

    return col;
}
vec4 writeData(vec4 col, vec2 fragCoord, int id, vec4 value) {
    if (floor(fragCoord.x) == float(id))
    col = value;

    return col;
}
Data initData() {
    Data data;

    data.theta = PI;
    data.phi = 1.;
    data.r = 9.;

    data.ro = normalize(vec3(cos(data.theta), data.phi, sin(data.theta)))*data.r;
    data.ta = vec3(0.,0.5,0.);

    data.oldRo = data.ro;
    data.oldTa = data.ta;

    data.oldMouse = vec4(0.);

    data.refreshTime = 0.;

    return data;
}
Data readData(sampler2D tex, vec2 invRes) {
    Data data;

    data.theta = readData1(tex, 0);
    data.phi = readData1(tex, 1);
    data.r = readData1(tex, 2);

    data.ro = readData3(tex, 3);
    data.ta = readData3(tex, 4);

    data.oldRo = readData3(tex, 5);
    data.oldTa = readData3(tex, 6);

    data.oldMouse = readData4(tex, 7);
    data.refreshTime = readData1(tex, 8);
    data.ta = vec3(0.,0.5,0.);

    return data;
}
vec4 writeData(vec4 col, vec2 fragCoord, Data data) {
    col = writeData(col, fragCoord.xy, 0, data.theta);
    col = writeData(col, fragCoord.xy, 1, data.phi);
    col = writeData(col, fragCoord.xy, 2, data.r);
    col = writeData(col, fragCoord.xy, 3, data.ro);
    col = writeData(col, fragCoord.xy, 4, data.ta);
    col = writeData(col, fragCoord.xy, 5, data.oldRo);
    col = writeData(col, fragCoord.xy, 6, data.oldTa);
    col = writeData(col, fragCoord.xy, 7, data.oldMouse);
    col = writeData(col, fragCoord.xy, 8, data.refreshTime);
    return col;
}









// ---------------------------------------------
// Distance field
// ---------------------------------------------
float box( vec3 p, vec3 b )
{
    vec3 q = abs(p) - b;
    return length(max(q,0.0)) + min(max(q.x,max(q.y,q.z)),0.0);
}

float map(vec3 p) {

    float d = 99999.;

    #if SCENE == 0
    vec3 pp = p;
    p.xz = mod(p.zx,1.)-.5;
    d = min(d, length(p-vec3(0.,.4,0.))-.4);
    //d = min(d, sheep(p*8.)/8.);
    d = max(d, abs(pp.z)-5.);
    d = max(d, abs(pp.x)-3.);
    #endif

    #if SCENE == 1
    vec3 pp = p;
    p.xz = mod(p.zx,1.)-.5;
    //p.xz = rot(p.y*.5)*p.xz;
    d = min(d, box(p-vec3(0.,.4,0.),vec3(.4)));
    //d = min(d, sheep(p*8.)/8.);
    d = max(d, abs(pp.z)-5.);
    d = max(d, abs(pp.x)-3.);
    #endif

    #if SCENE == 2
    {
        vec3 ip = floor(p);
        vec3 fp = fract(p)-.5;

        vec3 id = hash3(ip+1000.);
        fp.y = p.y-.2;
        fp.xy = rot(id.x*PI*3.) * fp.xy;
        fp.xz = rot(id.y*PI*3.) * fp.xz;
        fp.yz = rot(id.z*PI*3.) * fp.yz;
        d = min(d, box(fp,vec3(.3)));
        d = max(d, abs(p.z)-5.);
        d = max(d, abs(p.x)-3.);

    }
    #endif

    #if !FURNACE_TEST
    d = min(d, p.y);
    #endif
    return d;
}









// ---------------------------------------------
// Ray tracing
// ---------------------------------------------
float trace(vec3 ro, vec3 rd, vec2 nf) {
    float t = nf.x;
    for(int i=0; i<256; i++) {
        float d = map(ro+rd*t);
        if (t > nf.y || abs(d)<0.001) break;
        t += d;
    }

    return t;
}

vec3 normal(vec3 p, float t) {
    vec2 eps = vec2(0.0001,0.0);
    float d = map(p);
    vec3 n;
    n.x = d - map(p - eps.xyy);
    n.y = d - map(p - eps.yxy);
    n.z = d - map(p - eps.yyx);
    n = normalize(n);
    return n;
}
