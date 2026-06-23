package rj.nexus.systems.bedrock.sodium;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.caffeinemc.mods.sodium.api.vertex.buffer.VertexBufferWriter;
import net.caffeinemc.mods.sodium.client.render.vertex.VertexConsumerUtils;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public final class SodiumCubeWriter implements ISodiumVertexWriter {

    private static final SodiumCubeWriter INSTANCE = new SodiumCubeWriter();
    private static final Vector4f SCRATCH_VEC = new Vector4f();

    private SodiumCubeWriter() {
    }

    public static boolean renderCube(VertexConsumer consumer, Matrix4f pose, int light, int overlay, int color,
                                     float ox, float oy, float oz, float sx, float sy, float sz,
                                     float[][] faces) {
        if (!NexusSodiumCompat.isSodiumInstalled()) return false;
        VertexBufferWriter writer = VertexConsumerUtils.convertOrLog(consumer);
        if (writer == null) return false;
        INSTANCE.write(writer, pose, light, overlay, color, ox, oy, oz, sx, sy, sz, faces);
        return true;
    }

    private void write(VertexBufferWriter writer, Matrix4f pose, int light, int overlay, int color,
                       float ox, float oy, float oz, float sx, float sy, float sz, float[][] faces) {
        NORMALS[0] = packNormal(0f, 0f, -1f);
        NORMALS[1] = packNormal(0f, 0f, 1f);
        NORMALS[2] = packNormal(-1f, 0f, 0f);
        NORMALS[3] = packNormal(1f, 0f, 0f);
        NORMALS[4] = packNormal(0f, 1f, 0f);
        NORMALS[5] = packNormal(0f, -1f, 0f);

        long ptr = SCRATCH_BUFFER;
        ptr = emitQuad(ptr, pose, light, overlay, color, NORMALS[0], faces != null ? faces[0] : null,
                ox, oy + sy, oz, ox + sx, oy + sy, oz, ox + sx, oy, oz, ox, oy, oz);
        ptr = emitQuad(ptr, pose, light, overlay, color, NORMALS[1], faces != null ? faces[1] : null,
                ox + sx, oy + sy, oz + sz, ox, oy + sy, oz + sz, ox, oy, oz + sz, ox + sx, oy, oz + sz);
        ptr = emitQuad(ptr, pose, light, overlay, color, NORMALS[2], faces != null ? faces[2] : null,
                ox, oy + sy, oz + sz, ox, oy + sy, oz, ox, oy, oz, ox, oy, oz + sz);
        ptr = emitQuad(ptr, pose, light, overlay, color, NORMALS[3], faces != null ? faces[3] : null,
                ox + sx, oy + sy, oz, ox + sx, oy + sy, oz + sz, ox + sx, oy, oz + sz, ox + sx, oy, oz);
        ptr = emitQuad(ptr, pose, light, overlay, color, NORMALS[4], faces != null ? faces[4] : null,
                ox, oy + sy, oz + sz, ox + sx, oy + sy, oz + sz, ox + sx, oy + sy, oz, ox, oy + sy, oz);
        ptr = emitQuad(ptr, pose, light, overlay, color, NORMALS[5], faces != null ? faces[5] : null,
                ox, oy, oz, ox + sx, oy, oz, ox + sx, oy, oz + sz, ox, oy, oz + sz);

        flush(writer, SIZE);
    }

    private long emitQuad(long ptr, Matrix4f pose, int light, int overlay, int color, int normal, float[] uv,
                          float x0, float y0, float z0, float x1, float y1, float z1,
                          float x2, float y2, float z2, float x3, float y3, float z3) {
        float u0 = uv != null ? uv[0] : 0, v0 = uv != null ? uv[1] : 0;
        float u1 = uv != null ? uv[2] : 1, v1 = uv != null ? uv[3] : 1;
        pose.transform(SCRATCH_VEC.set(x0, y0, z0, 1f));
        emitVertex(ptr, SCRATCH_VEC.x, SCRATCH_VEC.y, SCRATCH_VEC.z, color, u0, v0, overlay, light, normal);
        ptr += STRIDE;
        pose.transform(SCRATCH_VEC.set(x1, y1, z1, 1f));
        emitVertex(ptr, SCRATCH_VEC.x, SCRATCH_VEC.y, SCRATCH_VEC.z, color, u1, v0, overlay, light, normal);
        ptr += STRIDE;
        pose.transform(SCRATCH_VEC.set(x2, y2, z2, 1f));
        emitVertex(ptr, SCRATCH_VEC.x, SCRATCH_VEC.y, SCRATCH_VEC.z, color, u1, v1, overlay, light, normal);
        ptr += STRIDE;
        pose.transform(SCRATCH_VEC.set(x3, y3, z3, 1f));
        emitVertex(ptr, SCRATCH_VEC.x, SCRATCH_VEC.y, SCRATCH_VEC.z, color, u0, v1, overlay, light, normal);
        ptr += STRIDE;
        return ptr;
    }
}
