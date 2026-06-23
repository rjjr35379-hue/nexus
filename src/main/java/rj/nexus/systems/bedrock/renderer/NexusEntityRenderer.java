package rj.nexus.systems.bedrock.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import org.jspecify.annotations.Nullable;
import rj.nexus.systems.bedrock.model.BedrockModel;
import rj.nexus.systems.bedrock.model.render.NexusRenderPassInfo;
import rj.nexus.systems.util.MiscUtil;

public abstract class NexusEntityRenderer<T extends Entity, S extends EntityRenderState>
        extends EntityRenderer<T, S> {

    protected final BedrockModel model;

    protected NexusEntityRenderer(EntityRendererProvider.Context context, BedrockModel model) {
        super(context);
        this.model = model;
    }

    public abstract Identifier getTextureLocation(S renderState);

    public @Nullable RenderType getRenderType(S renderState, Identifier texture) {
        return RenderTypes.entityCutout(texture);
    }

    protected void adjustModelForRender(S renderState) {}


    @Override
    public void submit(S renderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraState) {
        // Leashes + nametags
        super.submit(renderState, poseStack, submitNodeCollector, cameraState);

        Identifier texture = getTextureLocation(renderState);
        RenderType renderType = getRenderType(renderState, texture);
        if (renderType == null) return;

        // submitCustomGeometry: (PoseStack, RenderType, BiConsumer<PoseStack.Pose, VertexConsumer>)
        submitNodeCollector.submitCustomGeometry(poseStack, renderType, (pose, vc) -> {
            PoseStack ps = new PoseStack();
            ps.pushPose();
            ps.last().set(pose);

            applyRotations(renderState, ps);
            ps.translate(0, 0.01f, 0);
            adjustModelForRender(renderState);

            int packedLight   = renderState.lightCoords;
            int packedOverlay = getPackedOverlay(renderState);

            NexusRenderPassInfo passInfo = new NexusRenderPassInfo(
                    model, ps, renderState.x, renderState.y, renderState.z);

            model.renderToBuffer(ps, vc, packedLight, packedOverlay, 1, 1, 1, 1, passInfo);
            ps.popPose();
        });
    }

    protected int getPackedOverlay(S renderState) {
        if (renderState instanceof LivingEntityRenderState living)
            return OverlayTexture.pack(0, living.hasRedOverlay);
        return OverlayTexture.NO_OVERLAY;
    }

    protected void applyRotations(S renderState, PoseStack ps) {
        if (renderState instanceof LivingEntityRenderState living) {
            if (living.pose == Pose.SLEEPING) {
                var bedDir = living.bedOrientation;
                if (bedDir != null) {
                    float eyeOffset = living.eyeHeight - 0.1f;
                    ps.translate(-bedDir.getStepX() * eyeOffset, 0, -bedDir.getStepZ() * eyeOffset);
                }
            }

            if (living.deathTime > 0) {
                float deathRot = Math.min(Mth.sqrt((living.deathTime - 1f) / 20f * 1.6f), 1) * 90f;
                ps.mulPose(Axis.ZP.rotationDegrees(deathRot));
            } else if (living.isAutoSpinAttack) {
                ps.mulPose(Axis.XP.rotationDegrees(-90f - living.xRot));
                ps.mulPose(Axis.YP.rotationDegrees(renderState.ageInTicks * -75f));
            } else if (living.pose == Pose.SLEEPING) {
                var bedDir = living.bedOrientation;
                ps.mulPose(Axis.YP.rotationDegrees(
                        bedDir != null ? MiscUtil.getDirectionAngle(bedDir) : 0f));
                ps.mulPose(Axis.ZP.rotationDegrees(90f));
                ps.mulPose(Axis.YP.rotationDegrees(270f));
            }

            if (living.isUpsideDown) {
                ps.translate(0, living.boundingBoxHeight + 0.1f, 0);
                ps.mulPose(Axis.ZP.rotationDegrees(180f));
            }

            ps.mulPose(Axis.YP.rotationDegrees(180f - living.bodyRot));
        }
    }
}