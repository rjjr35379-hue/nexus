package rj.nexus.systems.dialogue.condition;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;

import java.util.ArrayList;
import java.util.List;

public class EffectCondition implements DialogCondition {

    private final List<Holder<MobEffect>> effects = new ArrayList<>();

    public EffectCondition(List<String> effectIds) {
        for (String raw : effectIds) {
            Identifier id = Identifier.parse(raw);
            BuiltInRegistries.MOB_EFFECT.get(id).ifPresent(h -> effects.add(h));
        }
    }

    @Override
    public boolean test(LocalPlayer player) {
        for (Holder<MobEffect> holder : effects) {
            if (player.hasEffect(holder)) return true;
        }
        return false;
    }
}