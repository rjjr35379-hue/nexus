package rj.nexus.systems.dialogue.condition;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.EntityHitResult;

import java.util.List;

public class EntityCondition implements DialogCondition {
    private final List<String> entities;

    public EntityCondition(List<String> entities) {
        this.entities = entities;
    }

    @Override
    public boolean test(LocalPlayer p) {
        for (String raw : entities) {
            var type = BuiltInRegistries.ENTITY_TYPE.getOptional(Identifier.parse(raw)).orElse(null);
            if (type == null) continue;
            var hit = Minecraft.getInstance().hitResult;
            if (hit instanceof EntityHitResult ehr && ehr.getEntity().getType() == type) return true;
            if (!p.level().getEntities(p, p.getBoundingBox().inflate(16), e -> e.getType() == type).isEmpty())
                return true;
        }
        return false;
    }
}
