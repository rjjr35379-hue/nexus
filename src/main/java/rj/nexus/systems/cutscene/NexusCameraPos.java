package rj.nexus.systems.cutscene;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public record NexusCameraPos(Vec3 pos, float yaw, float pitch, float roll) {

    public static final StreamCodec<FriendlyByteBuf, NexusCameraPos> STREAM_CODEC =
            StreamCodec.of(
                    (buf, p) -> {
                        buf.writeDouble(p.pos.x);
                        buf.writeDouble(p.pos.y);
                        buf.writeDouble(p.pos.z);
                        buf.writeFloat(p.yaw);
                        buf.writeFloat(p.pitch);
                        buf.writeFloat(p.roll);
                    },
                    buf -> new NexusCameraPos(
                            new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()),
                            buf.readFloat(), buf.readFloat(), buf.readFloat()
                    )
            );

    public NexusCameraPos(final Vec3 pos, final float yaw, final float pitch, final float roll) {
        this.pos = pos;
        this.yaw = yaw;
        this.pitch = Mth.clamp(pitch, -90f, 90f);
        this.roll = roll;
    }

    public NexusCameraPos(final Vec3 pos, final Vec3 lookDir) {
        this(pos, yawFrom(lookDir.normalize()), pitchFrom(lookDir.normalize()), 0f);
    }

    private static float yawFrom(final Vec3 d) {
        return (float) Math.toDegrees(Math.atan2(-d.x, d.z));
    }

    private static float pitchFrom(final Vec3 d) {
        return (float) Math.toDegrees(-Math.asin(Mth.clamp((float) d.y, -1f, 1f)));
    }

    public Vec3 lerpPos(final NexusCameraPos next, final float t) {
        return new Vec3(
                this.pos.x + (next.pos.x - this.pos.x) * t,
                this.pos.y + (next.pos.y - this.pos.y) * t,
                this.pos.z + (next.pos.z - this.pos.z) * t
        );
    }
}
