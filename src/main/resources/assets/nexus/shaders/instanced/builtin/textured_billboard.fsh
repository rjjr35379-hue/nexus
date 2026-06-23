#version 150 core
in vec4 vTint;
in vec2 vUv;
uniform sampler2D TextureSampler;
out vec4 fragColor;
void main() {
    vec4 tex = texture(TextureSampler, vUv);
    fragColor = tex * vTint;
    if (fragColor.a < 0.01) discard;
}
