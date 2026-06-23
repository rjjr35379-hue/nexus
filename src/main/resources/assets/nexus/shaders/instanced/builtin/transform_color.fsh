#version 150 core

in vec4 vColor;
in vec3 vNormal;

uniform vec3 SunDir;

out vec4 fragColor;

void main() {
    float diff = max(dot(normalize(vNormal), normalize(SunDir)), 0.0);
    float light = 0.3 + 0.7 * diff;
    fragColor = vec4(vColor.rgb * light, vColor.a);
}
