package rj.nexus.mixin;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import rj.nexus.systems.cutscene.NexusCutsceneHandler;
@Mixin(LocalPlayer.class)
public class LocalPlayerCutsceneMixin {
    @Inject(method = "isControlledCamera", at = @At("HEAD"), cancellable = true)
    private void nexus_isCutsceneCamera(CallbackInfoReturnable<Boolean> cir) {
        if (NexusCutsceneHandler.isActive()) cir.setReturnValue(true);
    }
}
