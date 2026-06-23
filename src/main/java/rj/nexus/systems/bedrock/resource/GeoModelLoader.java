package rj.nexus.systems.bedrock.resource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import rj.nexus.systems.bedrock.model.BedrockModel;
import rj.nexus.systems.bedrock.model.pojo.BedrockModelFile;
import rj.nexus.systems.bedrock.model.pojo.CubeItem;
import rj.nexus.systems.bedrock.model.pojo.LocatorItem;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class GeoModelLoader implements SimpleSynchronousResourceReloadListener {
    public static final GeoModelLoader INSTANCE = new GeoModelLoader();

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(CubeItem.class,    new CubeItem.Deserializer())
            .registerTypeAdapter(LocatorItem.class, new LocatorItem.Deserializer())
            .create();

    private static final String FOLDER = "geo";
    private static final Identifier ID = Identifier.fromNamespaceAndPath("nexus", "geo_loader");

    private final Map<Identifier, BedrockModel> models = new HashMap<>();

    private GeoModelLoader() {}

    private static Identifier strip(Identifier id) {
        String path = id.getPath().replace(FOLDER + "/", "").replace(".geo.json", "");
        return path.isEmpty() ? null : Identifier.fromNamespaceAndPath(id.getNamespace(), path);
    }

    @Override
    public Identifier getFabricId() { return ID; }

    @Override
    public void onResourceManagerReload(ResourceManager mgr) {
        models.clear();
        mgr.listResources(FOLDER, id -> id.getPath().endsWith(".geo.json")).forEach((id, res) -> {
            Identifier key = strip(id);
            if (key == null) return;
            try (Reader rd = new InputStreamReader(res.open(), StandardCharsets.UTF_8)) {
                BedrockModelFile file = GSON.fromJson(rd, BedrockModelFile.class);
                if (file != null) models.put(key, new BedrockModel(file));
            } catch (Exception ignored) {}
        });
    }

    public BedrockModel getModel(Identifier id) {
        return models.get(id);
    }
}