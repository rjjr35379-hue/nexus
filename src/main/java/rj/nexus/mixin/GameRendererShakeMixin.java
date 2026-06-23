package rj.nexus.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rj.nexus.systems.screenshake.ShakeCenter;


@Mixin(GameRenderer.class)
public class GameRendererShakeMixin {
    @Unique private static float nexus_partial = 0f;

    @Inject(method = "renderLevel", at = @At("HEAD"))
    private void nexus_capturePartial(DeltaTracker dt, CallbackInfo ci) {
        nexus_partial = dt.getGameTimeDeltaPartialTick(false);
    }

    @Inject(
            method = "bobHurt(Lnet/minecraft/client/renderer/state/level/CameraRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;)V",
            at = @At("RETURN")
    )
    private void nexus_applyShake(CameraRenderState cameraState, PoseStack poseStack, CallbackInfo ci) {
        ShakeCenter.apply(poseStack, nexus_partial);
    }
}