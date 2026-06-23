package rj.nexus.mixin;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.boss.enderdragon.EnderDragonPart;
import net.minecraft.world.phys.Vec2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import rj.nexus.systems.cutscene.NexusCutsceneHandler;

@Mixin(LocalPlayer.class)
public class LocalPlayerInputMixin {

    @ModifyVariable(method = "applyInput", at = @At("STORE"), ordinal = 0)
    private Vec2 nexus_zeroMoveVector(Vec2 modifiedInput) {
        if (NexusCutsceneHandler.isActive()) {
            return Vec2.ZERO;
        }

        return modifiedInput;
    }
}