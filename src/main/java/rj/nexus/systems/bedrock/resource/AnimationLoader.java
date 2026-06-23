package rj.nexus.systems.bedrock.resource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import rj.nexus.systems.bedrock.animation.BedrockAnimation;
import rj.nexus.systems.bedrock.animation.pojo.AnimationFile;
import rj.nexus.systems.bedrock.model.BedrockModel;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class AnimationLoader implements SimpleSynchronousResourceReloadListener {
    public static final AnimationLoader INSTANCE = new AnimationLoader();

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(AnimationFile.KeyframeChannel.class,    new AnimationFile.KeyframeChannel.Deserializer())
            .registerTypeAdapter(AnimationFile.SoundEffectEntry.class,   new AnimationFile.SoundEffectEntry.Deserializer())
            .registerTypeAdapter(AnimationFile.ParticleEffectEntry.class, new AnimationFile.ParticleEffectEntry.Deserializer())
            .create();

    private static final String FOLDER = "animations";
    private static final Identifier ID = Identifier.fromNamespaceAndPath("nexus", "anim_loader");

    private final Map<Identifier, AnimationFile> raw = new HashMap<>();

    private AnimationLoader() {}

    private static Identifier strip(Identifier id) {
        String path = id.getPath().replace(FOLDER + "/", "").replace(".animation.json", "");
        return path.isEmpty() ? null : Identifier.fromNamespaceAndPath(id.getNamespace(), path);
    }

    @Override
    public Identifier getFabricId() { return ID; }

    @Override
    public void onResourceManagerReload(ResourceManager mgr) {
        raw.clear();
        mgr.listResources(FOLDER, id -> id.getPath().endsWith(".animation.json")).forEach((id, res) -> {
            Identifier key = strip(id);
            if (key == null) return;
            try (Reader rd = new InputStreamReader(res.open(), StandardCharsets.UTF_8)) {
                AnimationFile file = GSON.fromJson(rd, AnimationFile.class);
                if (file != null) raw.put(key, file);
            } catch (Exception ignored) {}
        });
    }

    public BedrockAnimation getAnimationForModel(Identifier fileId, String name, BedrockModel model) {
        AnimationFile file = raw.get(fileId);
        if (file == null || file.getAnimations() == null) return null;
        var def = file.getAnimations().get(name);
        return def == null ? null : BedrockAnimation.from(name, def, model);
    }
}