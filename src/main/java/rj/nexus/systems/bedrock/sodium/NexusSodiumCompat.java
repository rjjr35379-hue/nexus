package rj.nexus.systems.bedrock.sodium;

import net.fabricmc.loader.api.FabricLoader;

public final class NexusSodiumCompat {
    public static final String SODIUM = "sodium";
    public static boolean IS_SODIUM_INSTALLED = false;

    private NexusSodiumCompat() {
    }

    public static void init() {
        IS_SODIUM_INSTALLED = FabricLoader.getInstance().isModLoaded(SODIUM);
    }

    public static boolean isSodiumInstalled() {
        return IS_SODIUM_INSTALLED;
    }
}
