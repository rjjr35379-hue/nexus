package rj.nexus.systems.bedrock.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import rj.nexus.systems.bedrock.model.BedrockModel;
import rj.nexus.systems.bedrock.model.render.NexusRenderPassInfo;

public abstract class NexusBlockEntityRenderer<T extends BlockEntity, S extends BlockEntityRenderState>
        implements BlockEntityRenderer<T, S> {

    protected final BedrockModel model;
    protected float scaleWidth  = 1f;
    protected float scaleHeight = 1f;

    @SuppressWarnings("unused")
    protected NexusBlockEntityRenderer(BlockEntityRendererProvider.Context context, BedrockModel model) {
        this.model = model;
    }

    public abstract Identifier getTextureLocation(S renderState);

    public @Nullable RenderType getRenderType(S renderState, Identifier texture) {
        return RenderTypes.entityCutout(texture);
    }

    protected void adjustModelForRender(S renderState) {}


    public NexusBlockEntityRenderer<T, S> withScale(float scale) { return withScale(scale, scale); }
    public NexusBlockEntityRenderer<T, S> withScale(float w, float h) { scaleWidth = w; scaleHeight = h; return this; }


    @SuppressWarnings("unchecked")
    protected Direction getBlockStateDirection(T blockEntity) {
        BlockState state = blockEntity.getBlockState();
        for (EnumProperty<Direction> prop : new EnumProperty[]{
                BlockStateProperties.FACING, BlockStateProperties.HORIZONTAL_FACING,
                BlockStateProperties.VERTICAL_DIRECTION, BlockStateProperties.FACING_HOPPER}) {
            if (state.hasProperty(prop)) return state.getValue(prop);
        }
        return Direction.NORTH;
    }

    protected void tryRotateByDirection(PoseStack poseStack, Direction facing) {
        switch (facing) {
            case SOUTH -> poseStack.mulPose(Axis.YP.rotationDegrees(180));
            case WEST  -> poseStack.mulPose(Axis.YP.rotationDegrees(90));
            case EAST  -> poseStack.mulPose(Axis.YN.rotationDegrees(90));
            case UP    -> poseStack.mulPose(Axis.XP.rotationDegrees(90));
            case DOWN  -> poseStack.mulPose(Axis.XN.rotationDegrees(90));
            default    -> {}
        }
    }


    @Override
    public void submit(S renderState, PoseStack poseStack, SubmitNodeCollector renderTasks,
                       CameraRenderState cameraState) {
        Identifier texture = getTextureLocation(renderState);
        RenderType renderType = getRenderType(renderState, texture);
        if (renderType == null) return;

        renderTasks.submitCustomGeometry(poseStack, renderType, (pose, vc) -> {
            PoseStack ps = new PoseStack();
            ps.pushPose();
            ps.last().set(pose);

            ps.translate(0.5, 0, 0.5);
            if (scaleWidth != 1 || scaleHeight != 1) ps.scale(scaleWidth, scaleHeight, scaleWidth);

            adjustModelForRender(renderState);

            double wx = renderState.blockPos.getX() + 0.5;
            double wy = renderState.blockPos.getY();
            double wz = renderState.blockPos.getZ() + 0.5;

            NexusRenderPassInfo passInfo = new NexusRenderPassInfo(model, ps, wx, wy, wz);
            model.renderToBuffer(ps, vc, renderState.lightCoords, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1, passInfo);
            ps.popPose();
        });
    }

    @Override
    public abstract S createRenderState();

    @Override
    public void extractRenderState(T blockEntity, S renderState, float partialTick,
                                   Vec3 cameraPos, ModelFeatureRenderer.@Nullable CrumblingOverlay crumbling) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPos, crumbling);
    }

    public BedrockModel getModel() { return model; }
}