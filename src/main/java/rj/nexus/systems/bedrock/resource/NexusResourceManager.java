package rj.nexus.systems.bedrock.resource;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;

@Environment(EnvType.CLIENT)
public final class NexusResourceManager {

    private NexusResourceManager() {
    }

    public static void register() {
        var h = ResourceManagerHelper.get(PackType.CLIENT_RESOURCES);
        h.registerReloadListener(GeoModelLoader.INSTANCE);
        h.registerReloadListener(AnimationLoader.INSTANCE);
    }
}