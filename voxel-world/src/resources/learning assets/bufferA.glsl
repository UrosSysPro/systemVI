// ---------------------------------------------
// Raw Frame
// ---------------------------------------------
#define sundir normalize( vec3(5.,1.,0.))


// ---------------------------------------------
// Material
// ---------------------------------------------
struct Material {
    vec3 albedo;
    float metallic;
    float roughness;
    vec3 emissive;
};
Material newMaterial() {
    Material mat;
    mat.albedo = vec3(1.);
    mat.metallic = 0.;
    mat.roughness = .1;
    mat.emissive = vec3(0.);

    return mat;
}
Material getMaterial(vec3 p, inout vec3 n) {

    Material mat = newMaterial();

    #if 1 // Random materials
    if (abs(map(p)-p.y) > 0.001) {
        vec3 rnd = hash3(floor(p+1000.));
        mat.roughness = rnd.y*rnd.y;
        mat.metallic = rnd.x;
        mat.albedo = hash3(floor(p))*.75;
        mat.emissive = mat.albedo*step(rnd.y,.02);
    } else {
        mat.metallic = 1.;
        mat.roughness = .5;
    }
    #else // Roughness/Metallic axis
    if (abs(map(p)-p.y) > 0.001) {
        mat.roughness = pow(floor(p.z+5.)/9.,1.)*.99+0.01;
        mat.metallic = saturate(floor(p.x+3.)/5.);
        mat.albedo = vec3(1.);
    } else {
        mat.metallic = 1.;
        mat.roughness = mod(floor(p.x)+floor(p.z),2.)*.25+.25;
    }
    #endif


    #if FURNACE_TEST // Set it in the common tab
    mat.albedo = vec3(1.);
    mat.roughness = pow(floor(p.z+5.)/9.,1.)*.99+0.01;
    mat.metallic = saturate(floor(p.x+3.)/5.);
    mat.emissive = vec3(0.);
    #endif

    return mat;
}



// ---------------------------------------------
// BRDF
// ---------------------------------------------
vec3 evalDisneyDiffuse(Material mat, float NoL, float NoV, float LoH, float roughness) {
    float FD90 = 0.5 + 2. * roughness * pow(LoH,2.);
    float a = F_Schlick(1.,FD90, NoL);
    float b = F_Schlick(1.,FD90, NoV);

    return mat.albedo * (a * b / PI);
}

vec3 evalDisneySpecular(Material mat, vec3 F, float NoH, float NoV, float NoL) {
    float roughness = pow(mat.roughness, 2.);
    float D = D_GTR(roughness, NoH,2.);
    float G = GeometryTerm(NoL, NoV, pow(0.5+mat.roughness*.5,2.));

    vec3 spec = D*F*G / (4. * NoL * NoV);

    return spec;
}

vec4 sampleDisneyBRDF(vec3 v, vec3 n, Material mat, inout vec3 l) {

    float roughness = pow(mat.roughness, 2.);

    // sample microfacet normal
    vec3 t,b;
    basis(n,t,b);
    vec3 V = toLocal(t,b,n,v);
    vec3 h = SampleGGXVNDF(V, roughness,roughness, frand(), frand());
    if (h.z < 0.0)
    h = -h;
    h = toWorld(t,b,n,h);

    // fresnel
    vec3 f0 = mix(vec3(0.04), mat.albedo, mat.metallic);
    vec3 F = F_Schlick(f0, dot(v,h));

    // lobe weight probability
    float diffW = (1.-mat.metallic);
    float specW = luma(F);
    float invW = 1./(diffW + specW);
    diffW *= invW;
    specW *= invW;


    vec4 brdf = vec4(0.);
    float rnd = frand();
    if (rnd < diffW) // diffuse
    {
        l = cosineSampleHemisphere(n);
        h = normalize(l+v);

        float NoL = dot(n,l);
        float NoV = dot(n,v);
        if ( NoL <= 0. || NoV <= 0. ) { return vec4(0.); }
        float LoH = dot(l,h);
        float pdf = NoL/PI;

        vec3 diff = evalDisneyDiffuse(mat, NoL, NoV, LoH, roughness) * (1.-F);
        brdf.rgb = diff * NoL;
        brdf.a = diffW * pdf;
    }
    else // specular
    {
        l = reflect(-v,h);

        float NoL = dot(n,l);
        float NoV = dot(n,v);
        if ( NoL <= 0. || NoV <= 0. ) { return vec4(0.); }
        float NoH = min(dot(n,h),.99);
        float pdf = GGXVNDFPdf(NoH, NoV, roughness);

        vec3 spec = evalDisneySpecular(mat, F, NoH, NoV, NoL);
        brdf.rgb = spec * NoL;
        brdf.a = specW * pdf;
    }

    return brdf;
}


// ---------------------------------------------
// Pathtrace
// ---------------------------------------------
vec4 pathtrace(vec3 ro, vec3 rd) {

    float firstDepth = 0.;
    vec3 acc = vec3(0.);
    vec3 abso = vec3(1.);

    const int BOUNCE_COUNT = 6;
    for(int i=0; i<BOUNCE_COUNT; i++) {

        // raytrace
        float t = trace(ro,rd, vec2(0.01, 1000.));
        vec3 p = ro + rd * t;
        if (i == 0) firstDepth = t;

        // sky intersection ?
        if (t > 1000.) {
            acc += skyColor(rd, sundir) * abso;
            break;
        }

        // info at intersection point
        vec3 n = normal(p, t);
        vec3 v = -rd;
        Material mat = getMaterial(p,n);

        // sample BRDF
        vec3 outDir;
        vec4 brdf = sampleDisneyBRDF(v,n, mat, outDir);

        // add emissive part of the current material
        acc += mat.emissive * abso;

        // absorption (pdf are in brdf.a)
        if (brdf.a > 0.)
        abso *= brdf.rgb / brdf.a;

        // next direction
        ro = p+n*0.01;
        rd = outDir;
    }

    return vec4(acc, firstDepth);
}


// ---------------------------------------------
// Entrypoint
// ---------------------------------------------
void mainImage( out vec4 fragColor, in vec2 fragCoord ) {
    vec2 invRes = vec2(1.) / iResolution.xy;
    srand(ivec2(fragCoord), iFrame);

    // read data
    Data data = readData(iChannel1, invRes);
    if (iFrame == 0) data = initData();

    // setup ray
    vec2 uv = (fragCoord + vec2(frand(), frand())-.5) * invRes;
    vec3 ro = data.ro;
    vec2 v = uv*2.-1.;
    v.x *= iResolution.x * invRes.y;
    vec3 rd = lookat(data.ro, data.ta) * normalize(vec3(v,FOCAL_LENGTH));

    // pathtrace
    vec4 col = pathtrace(ro, rd);

    // fog
    col.rgb = mix(col.rgb, skyColor(rd,sundir), 1.-exp(-col.a*0.001));


    fragColor = vec4(min(col.rgb, 10.), col.a > 1000. ? -1. : col.a);
}