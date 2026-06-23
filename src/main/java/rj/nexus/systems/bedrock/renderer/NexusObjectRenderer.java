package rj.nexus.systems.bedrock.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;
import rj.nexus.systems.bedrock.model.BedrockModel;
import rj.nexus.systems.bedrock.model.render.NexusRenderPassInfo;


public abstract class NexusObjectRenderer {

    protected final BedrockModel model;
    protected float scaleWidth  = 1f;
    protected float scaleHeight = 1f;

    protected NexusObjectRenderer(BedrockModel model) {
        this.model = model;
    }

    public abstract Identifier getTextureLocation();

    public @Nullable RenderType getRenderType(Identifier texture) {
        return RenderTypes.entityCutout(texture);
    }

    protected void adjustModelForRender() {}

    public NexusObjectRenderer withScale(float scale)          { return withScale(scale, scale); }
    public NexusObjectRenderer withScale(float w, float h)     { scaleWidth = w; scaleHeight = h; return this; }


    public void submit(PoseStack poseStack, SubmitNodeCollector renderTasks,
                       CameraRenderState cameraState,
                       int packedLight,
                       double worldX, double worldY, double worldZ) {
        Identifier texture  = getTextureLocation();
        RenderType renderType = getRenderType(texture);
        if (renderType == null) return;

        renderTasks.submitCustomGeometry(poseStack, renderType, (pose, vc) -> {
            PoseStack ps = new PoseStack();
            ps.pushPose();
            ps.last().set(pose);

            ps.translate(0.5f, 0.51f, 0.5f);
            if (scaleWidth != 1 || scaleHeight != 1) ps.scale(scaleWidth, scaleHeight, scaleWidth);

            adjustModelForRender();

            NexusRenderPassInfo passInfo = new NexusRenderPassInfo(model, ps, worldX, worldY, worldZ);
            model.renderToBuffer(ps, vc, packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1, passInfo);
            ps.popPose();
        });
    }

    public void submit(PoseStack poseStack, SubmitNodeCollector renderTasks,
                       CameraRenderState cameraState, int packedLight) {
        submit(poseStack, renderTasks, cameraState, packedLight, 0, 0, 0);
    }

    public BedrockModel getModel() { return model; }
}