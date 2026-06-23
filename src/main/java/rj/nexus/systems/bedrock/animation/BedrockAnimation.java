package rj.nexus.systems.bedrock.animation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;
import org.joml.Vector3f;
import rj.nexus.systems.bedrock.animation.pojo.AnimationFile;
import rj.nexus.systems.bedrock.animation.pojo.ParticleKeyframeData;
import rj.nexus.systems.bedrock.animation.pojo.SoundKeyframeData;
import rj.nexus.systems.bedrock.model.BedrockBone;
import rj.nexus.systems.bedrock.model.BedrockModel;
import rj.nexus.systems.bedrock.particle.NexusParticles;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class BedrockAnimation {
    public final String name;
    public final boolean loop;
    public final float length;

    private final Map<String, BoneChannels> bones = new HashMap<>();
    private final TreeMap<Float, SoundKeyframeData> sounds = new TreeMap<>();
    private final TreeMap<Float, ParticleKeyframeData> particles = new TreeMap<>();

    public BedrockAnimation(String name, boolean loop, float length) {
        this.name = name;
        this.loop = loop;
        this.length = length;
    }

    public static BedrockAnimation from(String name, AnimationFile.AnimationDef def, BedrockModel model) {
        BedrockAnimation a = new BedrockAnimation(name, def.isLoop(), def.getAnimationLength());

        if (def.getBones() != null)
            def.getBones().forEach((k, v) -> {
                if (model.getBone(k) != null) a.bones.put(k, BoneChannels.from(v));
            });

        if (def.getSoundEffects() != null)
            def.getSoundEffects().forEach((timeStr, entry) -> {
                try {
                    float t = Float.parseFloat(timeStr);
                    if (entry != null && entry.effect != null)
                        a.sounds.put(t, new SoundKeyframeData(t, entry.effect, entry.locator));
                } catch (NumberFormatException ignored) {}
            });

        if (def.getParticleEffects() != null)
            def.getParticleEffects().forEach((timeStr, entry) -> {
                try {
                    float t = Float.parseFloat(timeStr);
                    if (entry != null && entry.effect != null) {
                        boolean bedrock = !entry.effect.startsWith("minecraft:");
                        a.particles.put(t, new ParticleKeyframeData(t, entry.effect, entry.locator, bedrock));
                    }
                } catch (NumberFormatException ignored) {}
            });

        return a;
    }

    public void apply(BedrockModel model, float time) {
        bones.forEach((boneName, ch) -> {
            BedrockBone bone = model.getBone(boneName);
            if (bone == null) return;
            if (!ch.translation.isEmpty()) {
                Vector3f t = ch.translation.evaluate(time);
                bone.x += -t.x;
                bone.y += t.y;
                bone.z += t.z;
            }
            if (!ch.rotation.isEmpty()) {
                Vector3f r = ch.rotation.evaluate(time);
                float rx = (float) -Math.toRadians(r.x);
                float ry = (float) -Math.toRadians(r.y);
                float rz = (float)  Math.toRadians(r.z);
                bone.rotation.identity().rotateZYX(rz, ry, rx);
            }
            if (!ch.scale.isEmpty()) {
                Vector3f s = ch.scale.evaluate(time);
                bone.xScale *= s.x;
                bone.yScale *= s.y;
                bone.zScale *= s.z;
            }
        });
    }
    public void fireKeyframeEvents(BedrockModel model, float from, float to, double x, double y, double z) {
        fireSounds(model, from, to, x, y, z);
        fireParticles(model, from, to, x, y, z);
    }

    private void fireSounds(BedrockModel model, float from, float to, double ex, double ey, double ez) {
        sounds.subMap(from, false, to, true).forEach((t, data) -> {
            String soundId = data.getSound();
            String[] parts = soundId.split("\\|");
            float volume = parts.length > 1 ? Float.parseFloat(parts[1]) : 1f;
            float pitch  = parts.length > 2 ? Float.parseFloat(parts[2]) : 1f;

            var holder = BuiltInRegistries.SOUND_EVENT.get(Identifier.parse(parts[0]));
            if (holder.isEmpty()) return;
            var event = holder.get().value();

            double sx = ex, sy = ey, sz = ez;
            if (data.getLocatorName() != null) {
                var pos = getLocatorWorldOffset(model, data.getLocatorName(), ex, ey, ez);
                sx = pos[0]; sy = pos[1]; sz = pos[2];
            }

            double finalSx = sx, finalSy = sy, finalSz = sz;
            Minecraft.getInstance().execute(() ->
                    Minecraft.getInstance().level.playLocalSound(
                            finalSx, finalSy, finalSz, event, SoundSource.NEUTRAL, volume, pitch, false)
            );
        });
    }

    private void fireParticles(BedrockModel model, float from, float to, double ex, double ey, double ez) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;

        particles.subMap(from, false, to, true).forEach((t, data) -> {
            double px = ex, py = ey, pz = ez;
            if (data.getLocatorName() != null) {
                var pos = getLocatorWorldOffset(model, data.getLocatorName(), ex, ey, ez);
                px = pos[0]; py = pos[1]; pz = pos[2];
            }

            double finalPx = px, finalPy = py, finalPz = pz;
            if (data.isBedrockParticle()) {
                Identifier id = Identifier.tryParse(data.getEffect());
                if (id != null) Minecraft.getInstance().execute(() ->
                        NexusParticles.emit(id, level, finalPx, finalPy, finalPz, 0, 0));
            } else {
                Identifier id = Identifier.parse(data.getEffect());
                Minecraft.getInstance().execute(() -> {
                    var particleType = net.minecraft.core.registries.BuiltInRegistries.PARTICLE_TYPE.get(id);
                    if (particleType.isPresent() && particleType.get().value() instanceof net.minecraft.core.particles.SimpleParticleType spt) {
                        level.addParticle(spt, finalPx, finalPy, finalPz, 0, 0, 0);
                    }
                });
            }
        });
    }

    private double[] getLocatorWorldOffset(BedrockModel model, String locatorName, double ex, double ey, double ez) {
        BedrockBone bone = model.getBone(locatorName);
        if (bone == null) return new double[]{ex, ey, ez};
        float bx = 0, by = 0, bz = 0;
        BedrockBone b = bone;
        while (b != null && b.parent != null) {
            bx += b.x;
            by += b.y;
            bz += b.z;
            b = b.parent;
        }
        return new double[]{ex + bx / 16.0, ey + by / 16.0, ez + bz / 16.0};
    }

    public TreeMap<Float, SoundKeyframeData>    getSounds()    { return sounds; }
    public TreeMap<Float, ParticleKeyframeData> getParticles() { return particles; }

    public static class BoneChannels {
        public final AnimationKeyframes translation = new AnimationKeyframes();
        public final AnimationKeyframes rotation    = new AnimationKeyframes();
        public final AnimationKeyframes scale       = new AnimationKeyframes();

        public static BoneChannels from(AnimationFile.BoneDef def) {
            BoneChannels ch = new BoneChannels();
            if (def.getPosition() != null) fill(ch.translation, def.getPosition());
            if (def.getRotation() != null) fill(ch.rotation,    def.getRotation());
            if (def.getScale()    != null) fill(ch.scale,       def.getScale());
            return ch;
        }

        private static void fill(AnimationKeyframes kfs, AnimationFile.KeyframeChannel src) {
            src.getEntries().forEach((t, e) ->
                    kfs.add(t, new AnimationKeyframes.Keyframe(e.pre, e.post, e.data, EasingType.fromString(e.lerpMode))));
        }
    }
}