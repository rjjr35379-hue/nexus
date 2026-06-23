package rj.nexus.systems.bedrock.model.pojo;

import com.google.gson.*;
import net.minecraft.util.Mth;
import rj.nexus.systems.bedrock.model.BedrockBone;

import java.lang.reflect.Type;

public class LocatorItem {
    public float offsetX, offsetY, offsetZ;
    public float rotX, rotY, rotZ;

    /// Bake this LocatorItem into a [GeoLocator] for the given bone.
    public GeoLocator bake(String name, BedrockBone parentBone) {
        return new GeoLocator(
                parentBone, name,
                -offsetX, offsetY, offsetZ,
                -rotX * Mth.DEG_TO_RAD, -rotY * Mth.DEG_TO_RAD, rotZ * Mth.DEG_TO_RAD
        );
    }

    public static class Deserializer implements JsonDeserializer<LocatorItem> {
        @Override
        public LocatorItem deserialize(JsonElement json, Type type, JsonDeserializationContext ctx) throws JsonParseException {
            LocatorItem loc = new LocatorItem();
            if (json.isJsonArray()) {
                JsonArray a = json.getAsJsonArray();
                loc.offsetX = a.size() > 0 ? a.get(0).getAsFloat() : 0;
                loc.offsetY = a.size() > 1 ? a.get(1).getAsFloat() : 0;
                loc.offsetZ = a.size() > 2 ? a.get(2).getAsFloat() : 0;
            } else if (json.isJsonObject()) {
                JsonObject o = json.getAsJsonObject();
                if (o.has("offset") && o.get("offset").isJsonArray()) {
                    JsonArray a = o.getAsJsonArray("offset");
                    loc.offsetX = a.size() > 0 ? a.get(0).getAsFloat() : 0;
                    loc.offsetY = a.size() > 1 ? a.get(1).getAsFloat() : 0;
                    loc.offsetZ = a.size() > 2 ? a.get(2).getAsFloat() : 0;
                }
                if (o.has("rotation") && o.get("rotation").isJsonArray()) {
                    JsonArray a = o.getAsJsonArray("rotation");
                    loc.rotX = a.size() > 0 ? a.get(0).getAsFloat() : 0;
                    loc.rotY = a.size() > 1 ? a.get(1).getAsFloat() : 0;
                    loc.rotZ = a.size() > 2 ? a.get(2).getAsFloat() : 0;
                }
            }
            return loc;
        }
    }
}