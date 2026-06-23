package rj.nexus.systems.dialogue.condition;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ItemCondition implements DialogCondition {

    private final List<Object> matchers = new ArrayList<>();

    public ItemCondition(List<String> itemIds) {
        for (String raw : itemIds) {
            if (raw.startsWith("#")) {
                Identifier tagId = Identifier.parse(raw.substring(1));
                matchers.add(TagKey.create(Registries.ITEM, tagId));
            } else {
                Identifier itemId = Identifier.parse(raw);
                BuiltInRegistries.ITEM.getOptional(itemId).ifPresent(matchers::add);
            }
        }
    }

    @Override
    public boolean test(LocalPlayer player) {
        for (ItemStack stack : player.getInventory().getNonEquipmentItems()) {
            if (stack.isEmpty()) continue;
            for (Object m : matchers) {
                if (m instanceof Item item && stack.is(item)) return true;
                if (m instanceof TagKey<?> tag) {
                    @SuppressWarnings("unchecked")
                    TagKey<Item> itemTag = (TagKey<Item>) tag;
                    if (stack.is(itemTag)) return true;
                }
            }
        }
        return false;
    }
}
