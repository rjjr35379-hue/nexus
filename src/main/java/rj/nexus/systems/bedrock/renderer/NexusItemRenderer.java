package rj.nexus.systems.bedrock.renderer;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;
import rj.nexus.systems.bedrock.model.BedrockModel;
import rj.nexus.systems.bedrock.model.render.NexusRenderPassInfo;


public abstract class NexusItemRenderer<T extends Item> {

    protected final BedrockModel model;
    protected float scaleWidth  = 1f;
    protected float scaleHeight = 1f;
    protected boolean useEntityGuiLighting = false;

    protected NexusItemRenderer(BedrockModel model) {
        this.model = model;
    }

    // ────────── Abstract / Override API ──────────

    /// @return The texture for the given item stack and display context
    public abstract Identifier getTextureLocation(ItemStack stack, ItemDisplayContext context);

    /// @return The [RenderType] to use. Defaults to entityCutout.
    public @Nullable RenderType getRenderType(ItemStack stack, ItemDisplayContext context, Identifier texture) {
        return RenderTypes.entityCutout(texture);
    }

    protected void adjustModelForRender(ItemStack stack, ItemDisplayContext context) {}

    public NexusItemRenderer<T> withScale(float scale) { return withScale(scale, scale); }
    public NexusItemRenderer<T> withScale(float w, float h) { scaleWidth = w; scaleHeight = h; return this; }

    public NexusItemRenderer<T> useAlternateGuiLighting() { useEntityGuiLighting = true; return this; }


    public void setupLightingForGuiRender() {
        if (useEntityGuiLighting)
            Minecraft.getInstance().gameRenderer.getLighting()
                    .setupFor(Lighting.Entry.ENTITY_IN_UI);
        else
            Minecraft.getInstance().gameRenderer.getLighting()
                    .setupFor(Lighting.Entry.ITEMS_3D);
    }

    public void submit(ItemStack stack, ItemDisplayContext context,
                       PoseStack poseStack, SubmitNodeCollector renderTasks,
                       CameraRenderState cameraState, int packedLight) {
        Identifier texture = getTextureLocation(stack, context);
        RenderType renderType = getRenderType(stack, context, texture);
        if (renderType == null) return;

        renderTasks.submitCustomGeometry(poseStack, renderType, (pose, vc) -> {
            PoseStack ps = new PoseStack();
            ps.pushPose();
            ps.last().set(pose);

            // Standard item pose offset — mirrors GeckoLib's adjustRenderPose
            ps.translate(0.5f, 0.51f, 0.5f);

            if (scaleWidth != 1 || scaleHeight != 1) ps.scale(scaleWidth, scaleHeight, scaleWidth);

            adjustModelForRender(stack, context);

            NexusRenderPassInfo passInfo = new NexusRenderPassInfo(model, ps, 0, 0, 0);
            model.renderToBuffer(ps, vc, packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1, passInfo);
            ps.popPose();
        });
    }

    public BedrockModel getModel() { return model; }
}