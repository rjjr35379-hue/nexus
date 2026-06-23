package rj.nexus.systems.util;

import net.minecraft.ChatFormatting;
import net.minecraft.world.item.Rarity;
import rj.nexus.mixin.RarityAccessor;

import java.util.Arrays;
import java.util.Locale;

public final class NexusRarity {

    private NexusRarity() {
    }

    public static synchronized Rarity create(final String name, final ChatFormatting color) {
        for (final Rarity existing : Rarity.values()) {
            if (existing.name().equalsIgnoreCase(name)) return existing;
        }

        final Rarity[] previous = RarityAccessor.nexus$getValues();
        final int ordinal = previous.length;
        final String enumName = name.toUpperCase(Locale.ROOT);

        final Rarity newRarity = RarityAccessor.nexus$construct(enumName, ordinal, ordinal, name, color);
        final Rarity[] newValues = Arrays.copyOf(previous, previous.length + 1);
        newValues[previous.length] = newRarity;
        RarityAccessor.nexus$setValues(newValues);
        return newRarity;
    }
}
