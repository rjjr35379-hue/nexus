package rj.nexus.systems.bedrock.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.core.Direction;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import rj.nexus.systems.bedrock.model.pojo.FaceItem;
import rj.nexus.systems.bedrock.model.pojo.FaceUVsItem;

public class BedrockCubePerFace implements BedrockCube {
    private static final Vector3f[] V = new Vector3f[8];

    static {
        for (int i = 0; i < 8; i++) V[i] = new Vector3f();
    }

    private final float x, y, z, w, h, d;
    private final float[][] uvs = new float[6][8];
    private int emptyMask = 0;

    public BedrockCubePerFace(float x, float y, float z, float w, float h, float d, float infl, float tw, float th, FaceUVsItem faces) {
        this.x = (x - infl) / 16f;
        this.y = (y - infl) / 16f;
        this.z = (z - infl) / 16f;
        this.w = (w + infl * 2) / 16f;
        this.h = (h + infl * 2) / 16f;
        this.d = (d + infl * 2) / 16f;
        for (Direction dir : Direction.values()) {
            FaceItem face = faces.getFace(dir);
            int idx = dir.ordinal();
            if (face == null || face.getUvSize() == null) {
                emptyMask |= (1 << idx);
                continue;
            }
            System.arraycopy(face.getRotatedUVs(tw, th), 0, uvs[idx], 0, 8);
        }
    }

    private void build(Matrix4f m) {
        Vector3f ex = new Vector3f(m.m00(), m.m01(), m.m02()).mul(w);
        Vector3f ey = new Vector3f(m.m10(), m.m11(), m.m12()).mul(h);
        Vector3f ez = new Vector3f(m.m20(), m.m21(), m.m22()).mul(d);
        V[0].set(x, y, z).mulPosition(m);
        V[0].add(ex, V[1]);
        V[1].add(ey, V[2]);
        V[0].add(ey, V[3]);
        V[0].add(ez, V[4]);
        V[1].add(ez, V[5]);
        V[2].add(ez, V[6]);
        V[3].add(ez, V[7]);
    }

    @Override
    public void compile(PoseStack.Pose pose, Vector3f[] n, VertexConsumer vc, int lm, int ov, float r, float g, float b, float a) {
        build(pose.pose());
        for (int i = 0; i < 6; i++) {
            if ((emptyMask & (1 << i)) != 0) continue;
            int[] fv = FACE_VERTS[i];
            float[] uv = uvs[i];
            vc.addVertex(V[fv[0]].x, V[fv[0]].y, V[fv[0]].z).setColor(r, g, b, a).setUv(uv[0], uv[1]).setOverlay(ov).setLight(lm).setNormal(n[i].x, n[i].y, n[i].z);
            vc.addVertex(V[fv[1]].x, V[fv[1]].y, V[fv[1]].z).setColor(r, g, b, a).setUv(uv[2], uv[3]).setOverlay(ov).setLight(lm).setNormal(n[i].x, n[i].y, n[i].z);
            vc.addVertex(V[fv[2]].x, V[fv[2]].y, V[fv[2]].z).setColor(r, g, b, a).setUv(uv[4], uv[5]).setOverlay(ov).setLight(lm).setNormal(n[i].x, n[i].y, n[i].z);
            vc.addVertex(V[fv[3]].x, V[fv[3]].y, V[fv[3]].z).setColor(r, g, b, a).setUv(uv[6], uv[7]).setOverlay(ov).setLight(lm).setNormal(n[i].x, n[i].y, n[i].z);
        }
    }
}
