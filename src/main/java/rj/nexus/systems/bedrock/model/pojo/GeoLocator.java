package rj.nexus.systems.bedrock.model.pojo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;
import rj.nexus.systems.bedrock.model.BedrockBone;
import rj.nexus.systems.bedrock.model.render.NexusRenderPassInfo;
import rj.nexus.systems.util.NexusRenderUtil;

public class GeoLocator {
    protected final BedrockBone parent;
    protected final String name;

    protected final float offsetX;
    protected final float offsetY;
    protected final float offsetZ;

    protected final float rotX;
    protected final float rotY;
    protected final float rotZ;

    @ApiStatus.Internal
    public NexusRenderPassInfo.BonePositionListener @Nullable [] positionListeners = null;

    public GeoLocator(BedrockBone parent, String name,
                      float offsetX, float offsetY, float offsetZ,
                      float rotX,   float rotY,   float rotZ) {
        this.parent  = parent;
        this.name    = name;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.rotX    = rotX;
        this.rotY    = rotY;
        this.rotZ    = rotZ;
    }

    /// The parent bone of this locator
    public BedrockBone parent()  { return parent; }
    /// The name of this locator, as defined in the model JSON
    public String name()         { return name; }
    /// The offset x coordinate, relative to its parent bone
    public float offsetX()       { return offsetX; }
    /// The offset y coordinate, relative to its parent bone
    public float offsetY()       { return offsetY; }
    /// The offset z coordinate, relative to its parent bone
    public float offsetZ()       { return offsetZ; }
    /// The x rotation of this locator, in radians, relative to its parent bone
    public float rotX()          { return rotX; }
    /// The y rotation of this locator, in radians, relative to its parent bone
    public float rotY()          { return rotY; }
    /// The z rotation of this locator, in radians, relative to its parent bone
    public float rotZ()          { return rotZ; }

    /// Pass the current render position to any applied [NexusRenderPassInfo.BonePositionListener]s
    @ApiStatus.Internal
    public void updatePositionListeners(PoseStack poseStack, NexusRenderPassInfo renderPassInfo) {
        if (this.positionListeners == null) return;

        poseStack.pushPose();

        // Step back to parent bone pivot, then apply this locator's own offset
        poseStack.translate(-parent.x / 16f, -parent.y / 16f, -parent.z / 16f);
        poseStack.translate(offsetX / 16f, offsetY / 16f, offsetZ / 16f);

        if (rotZ != 0) poseStack.mulPose(Axis.ZP.rotation(rotZ));
        if (rotY != 0) poseStack.mulPose(Axis.YP.rotation(rotY));
        if (rotX != 0) poseStack.mulPose(Axis.XP.rotation(rotX));

        NexusRenderUtil.providePositionsToListeners(poseStack, renderPassInfo, positionListeners);

        poseStack.popPose();
    }
}