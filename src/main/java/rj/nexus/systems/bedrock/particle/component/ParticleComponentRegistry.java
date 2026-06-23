package rj.nexus.systems.bedrock.particle.component;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import java.util.Map;

public class ParticleComponentRegistry {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<Identifier, ParticleComponentType<? extends ParticleComponent<?>>> REGISTRY = new Object2ObjectOpenHashMap<>();

    public static <T extends ParticleComponent<?>> ParticleComponentType<T> registerComponent(Identifier id, Class<T> type) {
        var ct = new ParticleComponentType<>(id, type);
        REGISTRY.put(id, ct);
        return ct;
    }

    @SuppressWarnings("unchecked")
    public static <T extends ParticleComponent<?>> ParticleComponentType<T> getType(Identifier key) {
        var info = REGISTRY.get(key);
        if (info == null) { LOGGER.error("Unknown particle component: {}", key); return null; }
        return (ParticleComponentType<T>) info;
    }
}
