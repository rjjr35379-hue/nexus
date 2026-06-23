package rj.nexus.systems.init;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import rj.nexus.systems.cutscene.NexusClientCameraEntity;

public final class NexusEntities {

    public static EntityType<NexusClientCameraEntity> CLIENT_CAMERA;

    private NexusEntities() {
    }

    public static void register() {
        final ResourceKey<EntityType<?>> key = ResourceKey.create(
                Registries.ENTITY_TYPE,
                Identifier.fromNamespaceAndPath("nexus", "client_camera")
        );
        CLIENT_CAMERA = Registry.register(
                BuiltInRegistries.ENTITY_TYPE,
                Identifier.fromNamespaceAndPath("nexus", "client_camera"),
                EntityType.Builder.of(NexusClientCameraEntity::new, MobCategory.MISC)
                        .sized(0f, 0f)
                        .noSave()
                        .noSummon()
                        .build(key)
        );
    }
}
