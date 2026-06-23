#version 150 core
in vec3 Position;
in mat4 aWorldMat0;
uniform mat4 ProjViewMatrix;
void main() { gl_Position = ProjViewMatrix * aWorldMat0 * vec4(Position, 1.0); }
