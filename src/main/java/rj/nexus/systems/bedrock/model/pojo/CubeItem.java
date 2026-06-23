package rj.nexus.systems.bedrock.model.pojo;

import com.google.gson.*;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Type;

public class CubeItem {
    @Expose
    @SerializedName("origin")
    private float[] origin = {0, 0, 0};
    @Expose
    @SerializedName("size")
    private float[] size = {0, 0, 0};
    @Expose
    @SerializedName("inflate")
    private float inflate = 0;
    @Expose
    @SerializedName("rotation")
    private float[] rotation;
    @Expose
    @SerializedName("pivot")
    private float[] pivot;
    private float[] uv;
    private FaceUVsItem faceUv;
    private boolean mirror;

    public float[] getOrigin() {
        return origin;
    }

    public float[] getSize() {
        return size;
    }

    public float getInflate() {
        return inflate;
    }

    public float[] getRotation() {
        return rotation;
    }

    public float[] getPivot() {
        return pivot;
    }

    public float[] getUv() {
        return uv;
    }

    public FaceUVsItem getFaceUv() {
        return faceUv;
    }

    public boolean isMirror() {
        return mirror;
    }

    public static class Deserializer implements JsonDeserializer<CubeItem> {
        @Override
        public CubeItem deserialize(JsonElement json, Type type, JsonDeserializationContext ctx) throws JsonParseException {
            CubeItem c = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().fromJson(json, CubeItem.class);
            if (!json.isJsonObject()) return c;
            JsonObject obj = json.getAsJsonObject();
            JsonElement uvEl = obj.get("uv");
            if (uvEl != null) {
                if (uvEl.isJsonArray()) {
                    JsonArray a = uvEl.getAsJsonArray();
                    c.uv = new float[]{a.get(0).getAsFloat(), a.get(1).getAsFloat()};
                } else if (uvEl.isJsonObject()) {
                    c.faceUv = new Gson().fromJson(uvEl, FaceUVsItem.class);
                }
            }
            if (obj.has("mirror")) c.mirror = obj.get("mirror").getAsBoolean();
            return c;
        }
    }
}
