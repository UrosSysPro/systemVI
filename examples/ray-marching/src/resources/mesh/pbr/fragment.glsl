#version 330

in vec3 worldPosition;
in vec3 vNormal;

out vec4 FragColor;

uniform vec3 cameraPosition;
uniform vec3 lightPosition;
uniform vec3 lightColor;

// Disney BRDF parameters
//uniform vec3  baseColor;
//uniform float metallic;
//uniform float roughness;
//uniform float subsurface;
//uniform float specular;
//uniform float specularTint;
//uniform float anisotropic;  // not fully implemented here, requires tangent
//uniform float sheen;
//uniform float sheenTint;
//uniform float clearcoat;
//uniform float clearcoatGloss;
const vec3  baseColor      = vec3(0.8, 0.2, 0.1);
const float metallic       = 0.0;
const float roughness      = 0.4;
const float subsurface     = 0.0;
const float specular       = 0.5;
const float specularTint   = 0.0;
const float sheen          = 0.0;
const float sheenTint      = 0.5;
const float clearcoat      = 0.0;
const float clearcoatGloss = 1.0;

const float PI = 3.14159265358979323846;

// ---- helpers ----

float sqr(float x) { return x * x; }

float luminance(vec3 c) {
    return dot(c, vec3(0.2126, 0.7152, 0.0722));
}

float schlickFresnel(float u) {
    float m = clamp(1.0 - u, 0.0, 1.0);
    float m2 = m * m;
    return m2 * m2 * m; // m^5
}

float GTR1(float NdotH, float a) {
    if (a >= 1.0) return 1.0 / PI;
    float a2 = a * a;
    float t = 1.0 + (a2 - 1.0) * NdotH * NdotH;
    return (a2 - 1.0) / (PI * log(a2) * t);
}

float GTR2(float NdotH, float a) {
    float a2 = a * a;
    float t = 1.0 + (a2 - 1.0) * NdotH * NdotH;
    return a2 / (PI * t * t);
}

float smithG_GGX(float NdotV, float alphaG) {
    float a = alphaG * alphaG;
    float b = NdotV * NdotV;
    return 1.0 / (NdotV + sqrt(a + b - a * b));
}

// ---- BRDF ----

vec3 disneyBRDF(vec3 L, vec3 V, vec3 N) {
    float NdotL = max(dot(N, L), 0.0);
    float NdotV = max(dot(N, V), 0.0);
    if (NdotL <= 0.0 || NdotV <= 0.0) return vec3(0.0);

    vec3 H = normalize(L + V);
    float NdotH = max(dot(N, H), 0.0);
    float LdotH = max(dot(L, H), 0.0);

    // tint from base color
    float lum = luminance(baseColor);
    vec3 ctint = lum > 0.0 ? baseColor / lum : vec3(1.0);
    vec3 cspec0 = mix(specular * 0.08 * mix(vec3(1.0), ctint, specularTint), baseColor, metallic);
    vec3 csheen = mix(vec3(1.0), ctint, sheenTint);

    // --- Diffuse ---
    float FL = schlickFresnel(NdotL);
    float FV = schlickFresnel(NdotV);

    float Fd90 = 0.5 + 2.0 * roughness * LdotH * LdotH;
    float Fd = mix(1.0, Fd90, FL) * mix(1.0, Fd90, FV);

    // subsurface (Hanrahan-Krueger approximation)
    float Fss90 = roughness * LdotH * LdotH;
    float Fss = mix(1.0, Fss90, FL) * mix(1.0, Fss90, FV);
    float ss = 1.25 * (Fss * (1.0 / (NdotL + NdotV) - 0.5) + 0.5);

    vec3 diffuse = (1.0 / PI) * mix(Fd, ss, subsurface) * baseColor * (1.0 - metallic);

    // --- Sheen ---
    float FH = schlickFresnel(LdotH);
    vec3 sheenTerm = FH * sheen * csheen * (1.0 - metallic);

    // --- Specular (GGX) ---
    float alpha = max(sqr(roughness), 0.001);
    float Ds = GTR2(NdotH, alpha);
    float FH_spec = schlickFresnel(LdotH);
    vec3 Fs = mix(cspec0, vec3(1.0), FH_spec);
    float Gs = smithG_GGX(NdotL, alpha) * smithG_GGX(NdotV, alpha);

    vec3 specularTerm = Gs * Fs * Ds;

    // --- Clearcoat (GTR1, fixed IOR=1.5 -> F0=0.04) ---
    float Dr = GTR1(NdotH, mix(0.1, 0.001, clearcoatGloss));
    float Fr = mix(0.04, 1.0, FH);
    float Gr = smithG_GGX(NdotL, 0.25) * smithG_GGX(NdotV, 0.25);
    vec3 clearcoatTerm = vec3(0.25 * clearcoat * Gr * Fr * Dr);

    return (diffuse + sheenTerm + specularTerm + clearcoatTerm) * NdotL;
}

void main(){
    vec3 N = normalize(vNormal);
    vec3 V = normalize(cameraPosition - worldPosition);
    vec3 L = normalize(lightPosition - worldPosition);

    vec3 color = disneyBRDF(L, V, N) * lightColor;

    // basic gamma correction
    color = pow(clamp(color, 0.0, 1.0), vec3(1.0 / 2.2));

    FragColor = vec4(color, 1.0);
}