package rj.nexus.systems.dialogue.condition;

import net.minecraft.client.player.LocalPlayer;

public interface DialogCondition {
    boolean test(LocalPlayer p);
}
