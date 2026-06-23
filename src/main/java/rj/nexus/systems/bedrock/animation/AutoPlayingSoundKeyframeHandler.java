package rj.nexus.systems.bedrock.animation;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import rj.nexus.systems.bedrock.animation.pojo.SoundKeyframeData;
import rj.nexus.systems.util.ClientUtil;

public class AutoPlayingSoundKeyframeHandler {

    public static void handle(SoundKeyframeData data, Vec3 worldPos) {
        final Level level = ClientUtil.getLevel();
        if (level == null) return;

        String[] segments = data.getSound().split("\\|");
        float volume = segments.length > 1 ? safeFloat(segments[1], 1f) : 1f;
        float pitch  = segments.length > 2 ? safeFloat(segments[2], 1f) : 1f;

        var holder = BuiltInRegistries.SOUND_EVENT.get(Identifier.parse(segments[0]));
        if (holder.isEmpty()) return;

        level.playLocalSound(
                worldPos.x, worldPos.y, worldPos.z,
                holder.get().value(),
                SoundSource.NEUTRAL,
                volume, pitch, false
        );
    }

    private static float safeFloat(String s, float fallback) {
        try { return Float.parseFloat(s.trim()); }
        catch (NumberFormatException e) { return fallback; }
    }

    private AutoPlayingSoundKeyframeHandler() {}
}