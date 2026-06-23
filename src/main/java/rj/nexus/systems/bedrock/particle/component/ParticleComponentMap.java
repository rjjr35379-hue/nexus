package rj.nexus.systems.bedrock.particle.component;

import com.google.gson.*;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import java.lang.reflect.*;
import java.util.Map;
import java.util.function.BiConsumer;

@SuppressWarnings("unchecked")
public class ParticleComponentMap {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Map<ParticleComponentType<?>, ParticleComponent<?>> map = new Object2ObjectOpenHashMap<>();

    public void put(ParticleComponentType<?> type, ParticleComponent<?> comp) { map.put(type, comp); }
    public <T extends ParticleComponent<?>> T get(ParticleComponentType<T> type) { return (T) map.get(type); }
    public <T extends ParticleComponent<?>> boolean has(ParticleComponentType<T> type) { return map.containsKey(type); }
    public <T extends ParticleComponent<?>> void set(ParticleComponentType<T> type, ParticleComponent<?> val) { map.put(type, val); }
    public boolean isEmpty() { return map.isEmpty(); }
    public <T extends ParticleComponent<?>> void forEach(BiConsumer<ParticleComponentType<T>, ParticleComponent<?>> fn) {
        for (var entry : map.entrySet()) fn.accept((ParticleComponentType<T>) entry.getKey(), entry.getValue());
    }
    public void from(ParticleComponentMap other) { if (other != null) other.forEach(this::put); }

    public static class Deserializer implements JsonDeserializer<ParticleComponentMap> {
        @Override
        public ParticleComponentMap deserialize(JsonElement json, Type type, JsonDeserializationContext ctx) throws JsonParseException {
            var obj = json.getAsJsonObject();
            var result = new ParticleComponentMap();
            for (var entry : obj.entrySet()) {
                Identifier id = entry.getKey().contains(":") ? Identifier.parse(entry.getKey()) : Identifier.withDefaultNamespace(entry.getKey());
                ParticleComponentType<ParticleComponent<?>> ct = ParticleComponentRegistry.getType(id);
                if (ct == null) { LOGGER.warn("Unknown particle component: {}", id); continue; }
                ParticleComponent<?> comp;
                if (entry.getValue().isJsonPrimitive()) {
                    comp = createFromPrimitive(ct, entry.getValue().getAsString());
                } else {
                    comp = ctx.deserialize(entry.getValue(), ct.type());
                }
                result.put(ct, comp);
            }
            return result;
        }

        private static <T> ParticleComponent<?> createFromPrimitive(ParticleComponentType<ParticleComponent<?>> ct, String val) {
            try {
                Constructor<?> c = ct.type().getConstructor(String.class);
                return (ParticleComponent<?>) c.newInstance(val);
            } catch (Exception e) { throw new RuntimeException(e); }
        }
    }
}
