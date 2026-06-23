package rj.nexus.systems.cutscene;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public class NexusClientCameraEntity extends LivingEntity {

    public NexusClientCameraEntity(final EntityType<? extends LivingEntity> type, final Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    @Override
    public boolean hasEffect(final Holder<MobEffect> effect) {
        final var player = Minecraft.getInstance().player;
        return player != null && player.hasEffect(effect);
    }

    @Override
    public @Nullable MobEffectInstance getEffect(final Holder<MobEffect> effect) {
        final var player = Minecraft.getInstance().player;
        return player == null ? null : player.getEffect(effect);
    }

    @Override
    public HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

    @Override
    public ItemStack getItemBySlot(final EquipmentSlot slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(final EquipmentSlot slot, final ItemStack stack) {
    }

    @Override
    protected void readAdditionalSaveData(final ValueInput input) {
    }

    @Override
    protected void addAdditionalSaveData(final ValueOutput output) {
    }
}
