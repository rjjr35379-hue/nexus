package rj.nexus.mixin;

import net.minecraft.ChatFormatting;
import net.minecraft.world.item.Rarity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Rarity.class)
public interface RarityAccessor {

    @Accessor("$VALUES")
    static Rarity[] nexus$getValues() {
        throw new AssertionError("ouushii");
    }

    @Accessor("$VALUES")
    @Mutable
    static void nexus$setValues(Rarity[] values) {
        throw new AssertionError("ouushii");
    }

    @Invoker("<init>")
    static Rarity nexus$construct(String enumName, int ordinal, int id, String displayName, ChatFormatting color) {
        throw new AssertionError("ouushii");
    }
}