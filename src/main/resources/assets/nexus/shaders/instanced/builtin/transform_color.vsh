#version 150 core

in vec3 Position;        // location 0 - geometry vertex
in mat4 aWorldMat0;      // location 1..4 - per-instance model matrix (4 columns)
in vec4 aTint;           // location 5 - per-instance colour

uniform mat4 ProjViewMatrix;
uniform float Time;
uniform vec3 SunDir;

out vec4 vColor;
out vec3 vNormal;

void main() {
    vec4 worldPos = aWorldMat0 * vec4(Position, 1.0);
    gl_Position = ProjViewMatrix * worldPos;
    vColor = aTint;
    vNormal = normalize(mat3(aWorldMat0) * vec3(0,1,0));
}
