package rj.nexus.mixin;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rj.nexus.systems.dialogue.DialogRenderer;
@Mixin(Gui.class)
public class GuiHudMixin {
    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void onExtract(GuiGraphicsExtractor gfx, DeltaTracker dt, CallbackInfo ci) {
        DialogRenderer.render(gfx);
    }
}
