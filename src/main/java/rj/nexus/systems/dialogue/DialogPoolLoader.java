package rj.nexus.systems.dialogue;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import rj.nexus.systems.dialogue.condition.*;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Environment(EnvType.CLIENT)
public class DialogPoolLoader extends SimplePreparableReloadListener<Map<DialogCategory, List<DialogPool>>> {
    public static final DialogPoolLoader INSTANCE = new DialogPoolLoader();
    private static final Gson GSON = new GsonBuilder().create();
    private final Map<DialogCategory, List<DialogPool>> pools = new EnumMap<>(DialogCategory.class);

    private DialogPoolLoader() {
        for (var c : DialogCategory.values()) pools.put(c, new ArrayList<>());
    }

    @Override
    protected Map<DialogCategory, List<DialogPool>> prepare(ResourceManager mgr, ProfilerFiller p) {
        Map<DialogCategory, List<DialogPool>> r = new EnumMap<>(DialogCategory.class);
        for (var c : DialogCategory.values()) r.put(c, new ArrayList<>());
        for (var cat : DialogCategory.values()) {
            mgr.listResources("dialog/" + cat.folderName(), id -> id.getPath().endsWith(".json")).forEach((id, res) -> {
                try (Reader rd = new InputStreamReader(res.open(), StandardCharsets.UTF_8)) {
                    DialogPool pool = parse(GSON.fromJson(rd, JsonObject.class));
                    if (pool != null) r.get(cat).add(pool);
                } catch (Exception e) {
                    throw new RuntimeException("Failed: " + id, e);
                }
            });
        }
        return r;
    }

    @Override
    protected void apply(Map<DialogCategory, List<DialogPool>> data, ResourceManager m, ProfilerFiller p) {
        for (var c : DialogCategory.values()) {
            pools.get(c).clear();
            pools.get(c).addAll(data.get(c));
        }
    }

    private DialogPool parse(JsonObject o) {
        String text = o.has("text") ? o.get("text").getAsString() : "";
        int w = o.has("weight") ? o.get("weight").getAsInt() : 1;
        DialogCondition cond = o.has("condition") ? parseCond(o.getAsJsonObject("condition")) : AlwaysCondition.INSTANCE;
        return new DialogPool(cond, w, text);
    }

    private DialogCondition parseCond(JsonObject o) {
        if (!o.has("type")) return AlwaysCondition.INSTANCE;
        return switch (o.get("type").getAsString()) {
            case "nexus:equipment_conditional" -> new EquipmentCondition(list(o, "items"));
            case "nexus:effect_conditional" -> new EffectCondition(list(o, "effects"));
            case "nexus:item_conditional" -> new ItemCondition(list(o, "items"));
            case "nexus:entity_conditional" -> new EntityCondition(list(o, "entity"));
            default -> AlwaysCondition.INSTANCE;
        };
    }

    private List<String> list(JsonObject o, String k) {
        if (!o.has(k)) return List.of();
        JsonElement el = o.get(k);
        if (el.isJsonPrimitive()) return List.of(el.getAsString());
        List<String> l = new ArrayList<>();
        if (el.isJsonArray()) for (var e : el.getAsJsonArray()) l.add(e.getAsString());
        return l;
    }

    public List<DialogPool> getPools(DialogCategory c) {
        return Collections.unmodifiableList(pools.get(c));
    }
}
