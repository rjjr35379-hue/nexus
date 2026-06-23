package rj.nexus.systems.dialogue.condition;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;

import java.util.List;

public class EquipmentCondition implements DialogCondition {
    private final List<String> items;

    public EquipmentCondition(List<String> items) {
        this.items = items;
    }

    @Override
    public boolean test(LocalPlayer p) {
        var equipped = List.of(p.getItemBySlot(EquipmentSlot.HEAD), p.getItemBySlot(EquipmentSlot.CHEST),
                p.getItemBySlot(EquipmentSlot.LEGS), p.getItemBySlot(EquipmentSlot.FEET),
                p.getMainHandItem(), p.getOffhandItem());
        for (String raw : items) {
            if (raw.startsWith("#")) {
                var tag = TagKey.create(Registries.ITEM, Identifier.parse(raw.substring(1)));
                for (var s : equipped) if (!s.isEmpty() && s.is(tag)) return true;
            } else {
                var item = BuiltInRegistries.ITEM.getOptional(Identifier.parse(raw)).orElse(null);
                if (item == null) continue;
                for (var s : equipped) if (!s.isEmpty() && s.is(item)) return true;
            }
        }
        return false;
    }
}
