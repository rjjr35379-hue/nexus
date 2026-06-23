#version 150 core
in vec3 Position;
in vec3 aCenter;    // location 2
in float aSize;     // location 3
in vec4 aTint;      // location 4
in vec4 aUv;        // location 5 - (u0,v0,u1,v1)

uniform mat4 ProjViewMatrix;
uniform mat4 ViewMatrix;

out vec4 vTint;
out vec2 vUv;

void main() {
    vec3 right = vec3(ViewMatrix[0][0], ViewMatrix[1][0], ViewMatrix[2][0]);
    vec3 up    = vec3(ViewMatrix[0][1], ViewMatrix[1][1], ViewMatrix[2][1]);
    vec3 world = aCenter + (right * Position.x + up * Position.y) * aSize;
    gl_Position = ProjViewMatrix * vec4(world, 1.0);
    vTint = aTint;
    vUv = mix(aUv.xy, aUv.zw, (Position.xy * 0.5 + 0.5));
}
