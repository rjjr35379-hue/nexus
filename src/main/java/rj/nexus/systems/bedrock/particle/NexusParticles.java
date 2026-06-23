package rj.nexus.systems.bedrock.particle;

import gg.moonflower.molangcompiler.api.MolangCompiler;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import rj.nexus.systems.bedrock.particle.component.ParticleComponents;
import rj.nexus.systems.bedrock.particle.io.NexusParticleJson;
import rj.nexus.systems.bedrock.particle.io.ParticleEffectFile;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class NexusParticles {
    public static final String MOD_ID = "nexus";

    public static final MolangCompiler MOLANG = MolangCompiler.create(
            MolangCompiler.DEFAULT_FLAGS,
            NexusParticles.class.getClassLoader()
    );

    public static final float TIME_SCALE = 1f / 20f;

    private static final List<ParticleEffectFile> ALL = new ArrayList<>();
    static final List<NexusParticleEmitter> ACTIVE_EMITTERS = new ArrayList<>();

    public static ParticleEffectFile load(InputStream jsonStream, InputStream textureStream) {
        ParticleEffectFile file = NexusParticleJson.GSON.fromJson(new InputStreamReader(jsonStream), ParticleEffectFile.class);
        if (file.effect != null && file.effect.description != null)
            file.effect.description.postProcess();
        NexusParticleTextures.register(file, textureStream);
        ALL.add(file);
        return file;
    }

    public static ParticleEffectFile load(InputStream jsonStream, ClassLoader classLoader) {
        ParticleEffectFile file = NexusParticleJson.GSON.fromJson(new InputStreamReader(jsonStream), ParticleEffectFile.class);
        if (file.effect != null && file.effect.description != null)
            file.effect.description.postProcess();
        String texPath = file.effect.description.texturePath;
        InputStream tex = classLoader.getResourceAsStream(texPath + ".png");
        if (tex == null) tex = classLoader.getResourceAsStream("/" + texPath + ".png");
        NexusParticleTextures.register(file, tex);
        ALL.add(file);
        return file;
    }

    public static void clearAll() {
        ALL.clear();
        ACTIVE_EMITTERS.clear();
    }

    public static void emit(Identifier id, ClientLevel level, double x, double y, double z, float yRot, float xRot) {
        ParticleEffectFile file = find(id);
        if (file == null) return;
        try {
            ACTIVE_EMITTERS.add(new NexusParticleEmitter(file, level, x, y, z, xRot, yRot));
        } catch (Exception ignored) {}
    }

    public static void emit(Identifier id, Entity entity) {
        if (!(entity.level() instanceof ClientLevel cl)) return;
        ParticleEffectFile file = find(id);
        if (file == null) return;
        try {
            NexusParticleEmitter emitter = new NexusParticleEmitter(file, cl,
                    entity.getX(), entity.getY(), entity.getZ(),
                    entity.getXRot(), entity.getYRot());
            emitter.attachToEntity(entity);
            ACTIVE_EMITTERS.add(emitter);
        } catch (Exception ignored) {}
    }

    private static ParticleEffectFile find(Identifier id) {
        for (var f : ALL) if (id.equals(f.effect.description.identifier)) return f;
        return null;
    }

    public static boolean isLoaded(Identifier id) { return find(id) != null; }

    public static void init() {
        ParticleComponents.init();
        NexusParticleManager.register();
    }
}