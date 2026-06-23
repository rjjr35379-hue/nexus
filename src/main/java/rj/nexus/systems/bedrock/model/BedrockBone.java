package rj.nexus.systems.bedrock.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Math;
import org.joml.Matrix3f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;
import rj.nexus.systems.bedrock.model.pojo.GeoLocator;
import rj.nexus.systems.bedrock.model.render.NexusRenderPassInfo;
import rj.nexus.systems.util.NexusRenderUtil;

import java.util.ArrayList;
import java.util.List;

public class BedrockBone {
    private static final int MAX_LIGHT = 0xF000F0;
    private static final Vector3f[] N  = new Vector3f[6];

    static {
        for (int i = 0; i < 6; i++) N[i] = new Vector3f();
    }

    public final List<BedrockCube> cubes    = new ArrayList<>();
    private final List<BedrockBone> children = new ArrayList<>();
    private final List<GeoLocator>  locators = new ArrayList<>();

    public BedrockBone parent;
    public int index = -1;

    public float x, y, z;
    public Quaternionf rotation = new Quaternionf();
    public float xScale = 1, yScale = 1, zScale = 1;
    public boolean visible = true, illuminated = false, mirror;

    @ApiStatus.Internal
    public NexusRenderPassInfo.BonePositionListener @Nullable [] positionListeners = null;

    public void render(PoseStack ps, VertexConsumer vc, int lm, int ov) {
        render(ps, vc, lm, ov, 1, 1, 1, 1);
    }

    public void render(PoseStack ps, VertexConsumer vc, int lm, int ov, float r, float g, float b, float a) {
        render(ps, vc, lm, ov, r, g, b, a, null);
    }

    public void render(PoseStack ps, VertexConsumer vc, int lm, int ov, float r, float g, float b, float a,
                       @Nullable NexusRenderPassInfo renderPassInfo) {
        if (!visible) return;
        if (Math.abs(xScale) < 1e-5f && Math.abs(zScale) < 1e-5f) return;
        if (Math.abs(xScale) < 1e-5f && Math.abs(yScale) < 1e-5f) return;
        if (Math.abs(yScale) < 1e-5f && Math.abs(zScale) < 1e-5f) return;
        if (cubes.isEmpty() && children.isEmpty()) return;

        ps.pushPose();
        applyTransform(ps);

        if (positionListeners != null && renderPassInfo != null)
            NexusRenderUtil.providePositionsToListeners(ps, renderPassInfo, positionListeners);

        for (GeoLocator locator : locators) {
            if (locator.positionListeners != null && renderPassInfo != null)
                locator.updatePositionListeners(ps, renderPassInfo);
        }

        int light = illuminated ? MAX_LIGHT : lm;
        buildNormals(ps.last().normal());
        for (BedrockCube c : cubes) c.compile(ps.last(), N, vc, light, ov, r, g, b, a);
        for (BedrockBone child : children) child.render(ps, vc, light, ov, r, g, b, a, renderPassInfo);
        ps.popPose();
    }

    public void applyTransform(PoseStack ps) {
        ps.translate(x / 16f, y / 16f, z / 16f);
        ps.last().pose().rotate(rotation);
        ps.last().normal().rotate(rotation);
        if (xScale != 1 || yScale != 1 || zScale != 1) {
            ps.last().pose().scale(xScale, yScale, zScale);
            ps.last().normal().scale(xScale, yScale, zScale);
        }
    }

    public void translateAwayFromPivotPoint(PoseStack poseStack) {
        poseStack.translate(-x / 16f, -y / 16f, -z / 16f);
    }

    private void buildNormals(Matrix3f n) {
        N[0].set(-n.m10(), -n.m11(), -n.m12());
        N[1].set( n.m10(),  n.m11(),  n.m12());
        N[2].set(-n.m20(), -n.m21(), -n.m22());
        N[3].set( n.m20(),  n.m21(),  n.m22());
        N[4].set(-n.m00(), -n.m01(), -n.m02());
        N[5].set( n.m00(),  n.m01(),  n.m02());
    }


    public void addChild(BedrockBone c)   { children.add(c); }
    public List<BedrockBone> getChildren() { return children; }

    public void addLocator(GeoLocator locator) { locators.add(locator); }
    public List<GeoLocator> getLocators()       { return locators; }
}