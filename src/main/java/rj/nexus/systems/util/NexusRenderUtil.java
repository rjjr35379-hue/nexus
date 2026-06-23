package rj.nexus.systems.util;

/*MIT License
Copyright (c) 2026 GeckoLib — adapted for Nexus by rj.nexus */

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import rj.nexus.systems.bedrock.model.render.NexusRenderPassInfo;


public final class NexusRenderUtil {

    /// Convert a [Matrix4f] pose to a three-dimensional vector position, multiplying it by input values
    /// to allow for inline transformations
    public static Vec3 renderPoseToPosition(Matrix4f pose, float xScale, float yScale, float zScale) {
        final Vector4f position = pose.transform(new Vector4f(0, 0, 0, 1));
        return new Vec3(position.x() * xScale, position.y() * yScale, position.z() * zScale);
    }

    /// Extract the relative pose of an input matrix from a base matrix
    public static Matrix4f extractPoseFromRoot(Matrix4f baseMatrix, Matrix4f inputMatrix) {
        inputMatrix = new Matrix4f(inputMatrix);
        inputMatrix.invert();
        inputMatrix.mul(baseMatrix);
        return inputMatrix;
    }

    /// Directly translate a Matrix pose by a given position
    public static Matrix4f addPosToMatrix(Matrix4f baseMatrix, Vec3 pos) {
        baseMatrix.m30(baseMatrix.m30() + (float) pos.x)
                .m31(baseMatrix.m31() + (float) pos.y)
                .m32(baseMatrix.m32() + (float) pos.z);
        return baseMatrix;
    }

    public static void providePositionsToListeners(PoseStack poseStack,
                                                   NexusRenderPassInfo renderPassInfo,
                                                   NexusRenderPassInfo.BonePositionListener[] listeners) {
        final Matrix4f bonePose  = new Matrix4f(poseStack.last().pose());
        final Matrix4f localPose = extractPoseFromRoot(bonePose, renderPassInfo.getPreRenderMatrixState());
        final Matrix4f worldPose = addPosToMatrix(new Matrix4f(localPose), renderPassInfo.worldPos());

        final Vec3 localPos = renderPoseToPosition(localPose, 1,   1,  1);
        final Vec3 modelPos = renderPoseToPosition(localPose, -16, 16, 16);
        final Vec3 worldPos = renderPoseToPosition(worldPose, 1,   1,  1);

        for (NexusRenderPassInfo.BonePositionListener listener : listeners) {
            listener.accept(worldPos, modelPos, localPos);
        }
    }

    public static void faceRotation(PoseStack poseStack, Entity entity, float partialTick) {
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(
                net.minecraft.util.Mth.lerp(partialTick, entity.yRotO, entity.getYRot()) - 90));
        poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(
                net.minecraft.util.Mth.lerp(partialTick, entity.xRotO, entity.getXRot())));
    }

    private NexusRenderUtil() {}
}