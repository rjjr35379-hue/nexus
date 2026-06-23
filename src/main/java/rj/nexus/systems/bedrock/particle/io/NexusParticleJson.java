package rj.nexus.systems.bedrock.particle.io;

import com.google.gson.*;
import gg.moonflower.molangcompiler.api.MolangExpression;
import gg.moonflower.molangcompiler.api.exception.MolangSyntaxException;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import rj.nexus.systems.bedrock.particle.NexusParticles;
import rj.nexus.systems.bedrock.particle.component.ParticleComponentMap;
import rj.nexus.systems.bedrock.particle.component.emitter.EmitterShapeBox;
import rj.nexus.systems.bedrock.particle.component.emitter.EmitterShapeDisc;
import rj.nexus.systems.bedrock.particle.component.emitter.EmitterShapeSphere;
import rj.nexus.systems.bedrock.particle.component.misc.ColorConfig;
import rj.nexus.systems.bedrock.particle.component.particle.ParticleAppearanceTinting;
import rj.nexus.systems.bedrock.particle.component.particle.ParticleExpireIfInBlocks;
import rj.nexus.systems.bedrock.particle.component.particle.ParticleExpireIfNotInBlocks;
import rj.nexus.systems.bedrock.particle.curve.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

public class NexusParticleJson {
    public static final Gson GSON = new GsonBuilder()
            .registerTypeHierarchyAdapter(Identifier.class, new IdentifierAdapter())
            .registerTypeHierarchyAdapter(Block.class, (JsonDeserializer<Block>) (j,t,c) ->
                    BuiltInRegistries.BLOCK.get(Identifier.parse(j.getAsString())).orElseThrow().value())
            .registerTypeHierarchyAdapter(Item.class, (JsonDeserializer<Item>) (j,t,c) ->
                    BuiltInRegistries.ITEM.get(Identifier.parse(j.getAsString())).orElseThrow().value())
            .registerTypeHierarchyAdapter(SoundEvent.class, (JsonDeserializer<SoundEvent>) (j,t,c) ->
                    BuiltInRegistries.SOUND_EVENT.get(Identifier.parse(j.getAsString())).orElseThrow().value())
            .registerTypeHierarchyAdapter(ParticleComponentMap.class, new ParticleComponentMap.Deserializer())
            .registerTypeHierarchyAdapter(MolangExpression.class, new MolangDeserializer())
            .registerTypeAdapter(ParticleExpireIfNotInBlocks.class, new ParticleExpireIfNotInBlocks.Deserializer())
            .registerTypeAdapter(ParticleExpireIfInBlocks.class, new ParticleExpireIfInBlocks.Deserializer())
            .registerTypeAdapter(ParticleAppearanceTinting.class, new ParticleAppearanceTinting.Deserializer())
            .registerTypeAdapter(EmitterShapeSphere.class, new EmitterShapeSphere.Deserializer())
            .registerTypeAdapter(EmitterShapeBox.class, new EmitterShapeBox.Deserializer())
            .registerTypeAdapter(EmitterShapeDisc.class, new EmitterShapeDisc.Deserializer())
            .registerTypeAdapter(Curve.class, new CurveDeserializer())
            .registerTypeAdapter(ColorConfig.class, new ColorConfig.Deserializer())
            .registerTypeAdapter(String[].class, new StringArrayDeserializer())
            .create();

    public static ParticleEffectFile parse(InputStream stream, ClassLoader classLoader) {
        var reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
        ParticleEffectFile file = GSON.fromJson(reader, ParticleEffectFile.class);
        if (file != null && file.effect != null && file.effect.description != null)
            file.effect.description.postProcess();
        return file;
    }

    public static class MolangDeserializer implements JsonDeserializer<MolangExpression> {
        @Override
        public MolangExpression deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext ctx) throws JsonParseException {
            try { return NexusParticles.MOLANG.compile(json.getAsString()); }
            catch (MolangSyntaxException e) { throw new JsonParseException(e); }
        }
    }

    public static class StringArrayDeserializer implements JsonDeserializer<String[]> {
        @Override
        public String[] deserialize(JsonElement json, Type t, JsonDeserializationContext ctx) throws JsonParseException {
            if (json.isJsonArray()) {
                var arr = json.getAsJsonArray();
                String[] res = new String[arr.size()];
                for (int i = 0; i < arr.size(); i++) res[i] = arr.get(i).getAsString();
                return res;
            } else if (json.isJsonPrimitive()) {
                return new String[]{json.getAsString()};
            }
            throw new JsonParseException("du bist dut genug");
        }
    }

    public static class CurveDeserializer implements JsonDeserializer<Curve> {
        @Override
        public Curve deserialize(JsonElement json, Type t, JsonDeserializationContext ctx) throws JsonParseException {
            if (!json.isJsonObject()) return null;
            var typeEl = json.getAsJsonObject().get("type");
            if (typeEl == null) return null;
            return switch (typeEl.getAsString()) {
                case "linear"       -> ctx.deserialize(json, LinearCurve.class);
                case "catmull_rom"  -> ctx.deserialize(json, CatmullRomCurve.class);
                case "bezier"       -> ctx.deserialize(json, BezierCurve.class);
                case "bezier_chain" -> ctx.deserialize(json, BezierChainCurve.class);
                default -> null;
            };
        }
    }

    private static class IdentifierAdapter implements JsonDeserializer<Identifier>, JsonSerializer<Identifier> {
        public Identifier deserialize(JsonElement j, Type t, JsonDeserializationContext c) throws JsonParseException {
            return Identifier.parse(GsonHelper.convertToString(j, "identifier"));
        }
        public JsonElement serialize(Identifier id, Type t, JsonSerializationContext c) {
            return new JsonPrimitive(id.toString());
        }
    }
}