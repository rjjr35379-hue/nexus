package rj.nexus.systems.bedrock.model.render;

import com.mojang.blaze3d.vertex.PoseStack;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4f;
import org.jspecify.annotations.Nullable;
import net.minecraft.world.phys.Vec3;
import rj.nexus.systems.bedrock.model.BedrockBone;
import rj.nexus.systems.bedrock.model.BedrockModel;
import rj.nexus.systems.bedrock.model.pojo.GeoLocator;


import java.util.*;

public class NexusRenderPassInfo {
    private final BedrockModel model;
    private final PoseStack poseStack;
    private final double worldX, worldY, worldZ;

    /// PoseStack state captured before renderer-specific manipulations — mirrors GeckoLib's preRenderMatrixState
    private final Matrix4f objectRenderPose;

    private final Map<BedrockBone, List<BonePositionListener>>    bonePositionListeners    = new LinkedHashMap<>();
    private final Map<GeoLocator, List<BonePositionListener>>     locatorPositionListeners = new LinkedHashMap<>();

    public NexusRenderPassInfo(BedrockModel model, PoseStack poseStack,
                               double worldX, double worldY, double worldZ) {
        this.model        = model;
        this.poseStack    = poseStack;
        this.worldX       = worldX;
        this.worldY       = worldY;
        this.worldZ       = worldZ;
        this.objectRenderPose = new Matrix4f(poseStack.last().pose());
    }

    /// @return The BedrockModel for this render pass
    public BedrockModel model() { return model; }
    /// @return The PoseStack for this render pass
    public PoseStack poseStack() { return poseStack; }
    /// @return World X of the entity/object being rendered
    public double worldX() { return worldX; }
    /// @return World Y of the entity/object being rendered
    public double worldY() { return worldY; }
    /// @return World Z of the entity/object being rendered
    public double worldZ() { return worldZ; }
    /// @return World position of the entity/object being rendered
    public Vec3 worldPos() { return new Vec3(worldX, worldY, worldZ); }

    /// Get the [Matrix4f] for the current render pass representing
    /// the state of the [PoseStack] prior to any renderer-specific manipulations
    public Matrix4f getPreRenderMatrixState() { return objectRenderPose; }

    /// Add a [BonePositionListener] for a specific bone by name
    public void addBonePositionListener(String boneName, BonePositionListener listener) {
        BedrockBone bone = model.getBone(boneName);
        if (bone != null) addBonePositionListener(bone, listener);
    }

    /// Add a [BonePositionListener] for a specific bone
    public void addBonePositionListener(BedrockBone bone, BonePositionListener listener) {
        bonePositionListeners.computeIfAbsent(bone, k -> new ArrayList<>()).add(listener);
    }

    /// Add a [BonePositionListener] for a specific locator by name
    public void addLocatorPositionListener(String locatorName, BonePositionListener listener) {
        GeoLocator locator = model.getLocator(locatorName);
        if (locator != null) locatorPositionListeners.computeIfAbsent(locator, k -> new ArrayList<>()).add(listener);
    }

    /// Apply all position listeners to their respective bones/locators and run the render task.
    /// Cleans up listener arrays afterwards.
    @ApiStatus.Internal
    public void renderPosed(Runnable renderTask) {
        for (Map.Entry<BedrockBone, List<BonePositionListener>> e : bonePositionListeners.entrySet())
            e.getKey().positionListeners = e.getValue().toArray(new BonePositionListener[0]);
        for (Map.Entry<GeoLocator, List<BonePositionListener>> e : locatorPositionListeners.entrySet())
            e.getKey().positionListeners = e.getValue().toArray(new BonePositionListener[0]);

        try {
            renderTask.run();
        } finally {
            for (BedrockBone bone : bonePositionListeners.keySet()) bone.positionListeners = null;
            for (GeoLocator  loc  : locatorPositionListeners.keySet()) loc.positionListeners  = null;
        }
    }

    @FunctionalInterface
    public interface BonePositionListener {
        /// @param worldPos  Approximate world-space position of the bone
        /// @param modelPos  Model-space position of the bone (in Bedrock units)
        /// @param localPos  Local (camera-relative) position of the bone
        void accept(@Nullable Vec3 worldPos, @Nullable Vec3 modelPos, @Nullable Vec3 localPos);
    }
}