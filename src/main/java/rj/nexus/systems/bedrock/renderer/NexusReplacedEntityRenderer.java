package rj.nexus.systems.bedrock.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import org.jspecify.annotations.Nullable;
import rj.nexus.systems.bedrock.model.BedrockModel;
import rj.nexus.systems.bedrock.model.render.NexusRenderPassInfo;
import rj.nexus.systems.util.ClientUtil;
import rj.nexus.systems.util.MiscUtil;

public abstract class NexusReplacedEntityRenderer<E extends Entity, S extends EntityRenderState>
        extends EntityRenderer<E, S> {

    protected final BedrockModel model;
    protected float scaleWidth  = 1f;
    protected float scaleHeight = 1f;

    protected NexusReplacedEntityRenderer(EntityRendererProvider.Context context, BedrockModel model) {
        super(context);
        this.model = model;
    }

    public abstract Identifier getTextureLocation(S renderState);

    public @Nullable RenderType getRenderType(S renderState, Identifier texture) {
        if (renderState.isInvisible) {
            var player = ClientUtil.getClientPlayer();
            if (player != null && renderState.appearsGlowing())
                return RenderTypes.outline(texture);
            return null;
        }
        return RenderTypes.entityCutout(texture);
    }

    protected void adjustModelForRender(S renderState) {}


    public NexusReplacedEntityRenderer<E, S> withScale(float scale) { return withScale(scale, scale); }
    public NexusReplacedEntityRenderer<E, S> withScale(float w, float h) { scaleWidth = w; scaleHeight = h; return this; }


    @Override
    public void submit(S renderState, PoseStack poseStack, SubmitNodeCollector renderTasks, CameraRenderState cameraState) {
        super.submit(renderState, poseStack, renderTasks, cameraState);

        Identifier texture = getTextureLocation(renderState);
        RenderType renderType = getRenderType(renderState, texture);
        if (renderType == null) return;

        renderTasks.submitCustomGeometry(poseStack, renderType, (pose, vc) -> {
            PoseStack ps = new PoseStack();
            ps.pushPose();
            ps.last().set(pose);

            applyRotations(renderState, ps);
            ps.translate(0, 0.01f, 0);
            if (scaleWidth != 1 || scaleHeight != 1) ps.scale(scaleWidth, scaleHeight, scaleWidth);
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
                    ps.translate(-bedDir.getStepX() * (living.eyeHeight - 0.1f), 0,
                            -bedDir.getStepZ() * (living.eyeHeight - 0.1f));
                }
            }

            if (living.deathTime > 0) {
                ps.mulPose(Axis.ZP.rotationDegrees(
                        Math.min(Mth.sqrt((living.deathTime - 1f) / 20f * 1.6f), 1) * 90f));
            } else if (living.isAutoSpinAttack) {
                ps.mulPose(Axis.XP.rotationDegrees(-90f - living.xRot));
                ps.mulPose(Axis.YP.rotationDegrees(renderState.ageInTicks * -75f));
            } else if (living.pose == Pose.SLEEPING) {
                var bedDir = living.bedOrientation;
                ps.mulPose(Axis.YP.rotationDegrees(bedDir != null ? MiscUtil.getDirectionAngle(bedDir) : 0f));
                ps.mulPose(Axis.ZP.rotationDegrees(90f));
                ps.mulPose(Axis.YP.rotationDegrees(270f));
            } else if (living.isUpsideDown) {
                ps.translate(0, (living.boundingBoxHeight + 0.1f) / living.scale, 0);
                ps.mulPose(Axis.ZP.rotationDegrees(180f));
            }

            ps.mulPose(Axis.YP.rotationDegrees(180f - living.bodyRot));
        }
    }

    @Override
    public void extractRenderState(E entity, S state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);

        if (state instanceof LivingEntityRenderState living && entity instanceof LivingEntity le) {
            float lerpHeadYRot = Mth.rotLerp(partialTick, le.yHeadRotO, le.yHeadRot);
            living.bodyRot = LivingEntityRenderer.solveBodyRot(le, lerpHeadYRot, partialTick);
            living.yRot    = Mth.wrapDegrees(lerpHeadYRot - living.bodyRot);
            living.xRot    = le.getXRot(partialTick);
            living.scale   = le.getScale();
            living.ageScale = le.getAgeScale();
            living.pose    = le.getPose();
            living.bedOrientation  = le.getBedOrientation();
            living.isFullyFrozen   = le.isFullyFrozen();
            living.isBaby          = le.isBaby();
            living.isInWater       = le.isInWater();
            living.isAutoSpinAttack = le.isAutoSpinAttack();
            living.hasRedOverlay   = le.hurtTime > 0 || le.deathTime > 0;
            living.deathTime       = le.deathTime > 0 ? (float) le.deathTime + partialTick : 0;
            living.isUpsideDown    = false;

            if (!entity.isPassenger() && entity.isAlive()) {
                living.walkAnimationPos   = le.walkAnimation.position(partialTick);
                living.walkAnimationSpeed = le.walkAnimation.speed(partialTick);
            }
            if (living.bedOrientation != null)
                living.eyeHeight = le.getEyeHeight(Pose.STANDING);
        }
    }

    public BedrockModel getModel() { return model; }
}