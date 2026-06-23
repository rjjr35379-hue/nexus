package rj.nexus.systems.bedrock.animation.pojo;

import com.google.gson.*;
import com.google.gson.annotations.SerializedName;
import org.joml.Vector3f;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.TreeMap;

public class AnimationFile {
    @SerializedName("animations")
    private Map<String, AnimationDef> animations;

    public Map<String, AnimationDef> getAnimations() { return animations; }

    public static class AnimationDef {
        @SerializedName("loop")
        private boolean loop;
        @SerializedName("animation_length")
        private float length;
        @SerializedName("bones")
        private Map<String, BoneDef> bones;
        @SerializedName("sound_effects")
        private Map<String, SoundEffectEntry> soundEffects;
        @SerializedName("particle_effects")
        private Map<String, ParticleEffectEntry> particleEffects;
        @SerializedName("timeline")
        private Map<String, Object> timeline;

        public boolean isLoop() { return loop; }
        public float getAnimationLength() { return length; }
        public Map<String, BoneDef> getBones() { return bones; }
        public Map<String, SoundEffectEntry> getSoundEffects() { return soundEffects; }
        public Map<String, ParticleEffectEntry> getParticleEffects() { return particleEffects; }
        public Map<String, Object> getTimeline() { return timeline; }
    }

    public static class SoundEffectEntry {
        public String effect;
        public String locator;

        public static class Deserializer implements JsonDeserializer<SoundEffectEntry> {
            @Override
            public SoundEffectEntry deserialize(JsonElement json, Type t, JsonDeserializationContext ctx) throws JsonParseException {
                SoundEffectEntry e = new SoundEffectEntry();
                if (json.isJsonPrimitive()) {
                    e.effect = json.getAsString();
                } else if (json.isJsonObject()) {
                    JsonObject o = json.getAsJsonObject();
                    e.effect = o.has("effect") ? o.get("effect").getAsString() : null;
                    e.locator = o.has("locator") ? o.get("locator").getAsString() : null;
                }
                return e;
            }
        }
    }

    public static class ParticleEffectEntry {
        public String effect;
        public String locator;
        public String preEffectScript;
        public boolean bindToActor = true;

        public static class Deserializer implements JsonDeserializer<ParticleEffectEntry> {
            @Override
            public ParticleEffectEntry deserialize(JsonElement json, Type t, JsonDeserializationContext ctx) throws JsonParseException {
                ParticleEffectEntry e = new ParticleEffectEntry();
                if (json.isJsonPrimitive()) {
                    e.effect = json.getAsString();
                } else if (json.isJsonObject()) {
                    JsonObject o = json.getAsJsonObject();
                    e.effect = o.has("effect") ? o.get("effect").getAsString() : null;
                    e.locator = o.has("locator") ? o.get("locator").getAsString() : null;
                    e.preEffectScript = o.has("pre_effect_script") ? o.get("pre_effect_script").getAsString() : null;
                    e.bindToActor = !o.has("bind_to_actor") || o.get("bind_to_actor").getAsBoolean();
                }
                return e;
            }
        }
    }

    public static class BoneDef {
        @SerializedName("position") private KeyframeChannel position;
        @SerializedName("rotation") private KeyframeChannel rotation;
        @SerializedName("scale")    private KeyframeChannel scale;

        public KeyframeChannel getPosition() { return position; }
        public KeyframeChannel getRotation() { return rotation; }
        public KeyframeChannel getScale()    { return scale; }
    }

    public static class KeyframeEntry {
        public Vector3f pre, post, data;
        public String lerpMode;
    }

    public static class KeyframeChannel {
        private final TreeMap<Float, KeyframeEntry> entries = new TreeMap<>();
        public TreeMap<Float, KeyframeEntry> getEntries() { return entries; }

        public static class Deserializer implements JsonDeserializer<KeyframeChannel> {
            @Override
            public KeyframeChannel deserialize(JsonElement json, Type t, JsonDeserializationContext ctx) throws JsonParseException {
                KeyframeChannel ch = new KeyframeChannel();
                if (json.isJsonPrimitive()) {
                    float v = json.getAsFloat();
                    var e = new KeyframeEntry();
                    e.data = new Vector3f(v, v, v);
                    ch.entries.put(0f, e);
                } else if (json.isJsonArray()) {
                    var e = new KeyframeEntry();
                    e.data = v3(json.getAsJsonArray());
                    ch.entries.put(0f, e);
                } else if (json.isJsonObject()) {
                    for (var me : json.getAsJsonObject().entrySet()) {
                        float time;
                        try { time = Float.parseFloat(me.getKey()); }
                        catch (NumberFormatException ex) { continue; }
                        ch.entries.put(time, readEntry(me.getValue()));
                    }
                }
                return ch;
            }

            private KeyframeEntry readEntry(JsonElement el) {
                var e = new KeyframeEntry();
                if (el.isJsonArray()) {
                    e.data = v3(el.getAsJsonArray());
                } else if (el.isJsonObject()) {
                    var o = el.getAsJsonObject();
                    if (o.has("lerp_mode")) e.lerpMode = o.get("lerp_mode").getAsString();
                    if (o.has("pre") && o.get("pre").isJsonArray()) e.pre = v3(o.get("pre").getAsJsonArray());
                    if (o.has("post") && o.get("post").isJsonArray()) e.post = v3(o.get("post").getAsJsonArray());
                }
                return e;
            }

            private Vector3f v3(JsonArray a) {
                float x = 0, y = 0, z = 0;
                try { x = a.get(0).getAsFloat(); } catch (Exception ig) {}
                try { y = a.get(1).getAsFloat(); } catch (Exception ig) {}
                try { z = a.get(2).getAsFloat(); } catch (Exception ig) {}
                return new Vector3f(x, y, z);
            }
        }
    }
}