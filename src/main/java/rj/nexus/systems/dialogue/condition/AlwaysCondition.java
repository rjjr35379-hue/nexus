package rj.nexus.systems.dialogue.condition;

import net.minecraft.client.player.LocalPlayer;

public class AlwaysCondition implements DialogCondition {
    public static final AlwaysCondition INSTANCE = new AlwaysCondition();

    @Override
    public boolean test(LocalPlayer p) {
        return true;
    }
}
