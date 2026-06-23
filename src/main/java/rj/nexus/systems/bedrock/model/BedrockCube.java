package rj.nexus.systems.bedrock.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Vector3f;

public interface BedrockCube {

    int[][] FACE_VERTS = {
            {5, 4, 0, 1},
            {2, 3, 7, 6},
            {3, 2, 1, 0},
            {7, 6, 5, 4},
            {7, 3, 0, 4},
            {2, 6, 5, 1},
    };

    void compile(PoseStack.Pose pose, Vector3f[] normals, VertexConsumer vc,
                 int lightmap, int overlay, float r, float g, float b, float a);
}
