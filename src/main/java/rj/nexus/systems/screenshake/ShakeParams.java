package rj.nexus.systems.screenshake;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public final class ShakeParams {
    public static final StreamCodec<FriendlyByteBuf, ShakeParams> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT,   p -> p.attack,
                    ByteBufCodecs.INT,   p -> p.hold,
                    ByteBufCodecs.INT,   p -> p.decay,
                    ByteBufCodecs.FLOAT, p -> p.amplitude,
                    ByteBufCodecs.FLOAT, p -> p.frequency,
                    ShakeParams::new
            );

    public final int attack;
    public final int hold;
    public final int decay;
    public final float amplitude;
    public final float frequency;

    private ShakeParams(int attack, int hold, int decay, float amplitude, float frequency) {
        this.attack    = attack;
        this.hold      = hold;
        this.decay     = decay;
        this.amplitude = amplitude;
        this.frequency = frequency;
    }

    public int duration() {
        return attack + hold + decay;
    }

    float envelope(float t) {
        if (t < attack) return attack == 0 ? 1f : t / attack;
        float afterAttack = t - attack;
        if (afterAttack < hold) return 1f;
        float inDecay = afterAttack - hold;
        return decay == 0 ? 0f : Math.max(0f, 1f - inDecay / decay);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private int attack = 2, hold = 4, decay = 8;
        private float amplitude = 5f, frequency = 1.5f;

        public Builder attack(int v)    { attack    = v; return this; }
        public Builder hold(int v)      { hold      = v; return this; }
        public Builder decay(int v)     { decay     = v; return this; }
        public Builder amplitude(float v){ amplitude = v; return this; }
        public Builder frequency(float v){ frequency = v; return this; }

        public ShakeParams build() {
            return new ShakeParams(attack, hold, decay, amplitude, frequency);
        }
    }
}