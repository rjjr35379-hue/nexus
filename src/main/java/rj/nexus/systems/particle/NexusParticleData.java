package rj.nexus.systems.particle;

import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class NexusParticleData {

    public final NexusEmitterShape shape;
    public final Identifier texture;
    public final int count;
    public final float lifeMin;
    public final float lifeMax;
    public final float speedMin;
    public final float speedMax;
    public final float sizeStart;
    public final float sizeEnd;
    public final int colorStart;
    public final int colorEnd;
    public final List<NexusParticleModule.Init> initModules;
    public final List<NexusParticleModule.Update> updateModules;
    public final List<NexusParticleModule.Force> forceModules;

    private NexusParticleData(final Builder builder) {
        this.shape = builder.shape;
        this.texture = builder.texture;
        this.count = builder.count;
        this.lifeMin = builder.lifeMin;
        this.lifeMax = builder.lifeMax;
        this.speedMin = builder.speedMin;
        this.speedMax = builder.speedMax;
        this.sizeStart = builder.sizeStart;
        this.sizeEnd = builder.sizeEnd;
        this.colorStart = builder.colorStart;
        this.colorEnd = builder.colorEnd;
        this.initModules = List.copyOf(builder.initModules);
        this.updateModules = List.copyOf(builder.updateModules);
        this.forceModules = List.copyOf(builder.forceModules);
    }

    public static Builder create(final NexusEmitterShape shape) {
        return new Builder(shape);
    }

    public float randomLife(final Random rng) {
        return this.lifeMin + rng.nextFloat() * (this.lifeMax - this.lifeMin);
    }

    public float randomSpeed(final Random rng) {
        return this.speedMin + rng.nextFloat() * (this.speedMax - this.speedMin);
    }

    public static final class Builder {
        private final NexusEmitterShape shape;
        private final List<NexusParticleModule.Init> initModules = new ArrayList<>();
        private final List<NexusParticleModule.Update> updateModules = new ArrayList<>();
        private final List<NexusParticleModule.Force> forceModules = new ArrayList<>();
        private Identifier texture = Identifier.withDefaultNamespace("particle/generic_0");
        private int count = 10;
        private float lifeMin = 1f;
        private float lifeMax = 2f;
        private float speedMin = 0.05f;
        private float speedMax = 0.15f;
        private float sizeStart = 0.2f;
        private float sizeEnd = 0f;
        private int colorStart = 0xFFFFFFFF;
        private int colorEnd = 0x00FFFFFF;

        private Builder(final NexusEmitterShape shape) {
            this.shape = shape;
        }

        public Builder texture(final Identifier t) {
            this.texture = t;
            return this;
        }

        public Builder count(final int c) {
            this.count = c;
            return this;
        }

        public Builder life(final float min, final float max) {
            this.lifeMin = min;
            this.lifeMax = max;
            return this;
        }

        public Builder speed(final float min, final float max) {
            this.speedMin = min;
            this.speedMax = max;
            return this;
        }

        public Builder size(final float start, final float end) {
            this.sizeStart = start;
            this.sizeEnd = end;
            return this;
        }

        public Builder color(final int start, final int end) {
            this.colorStart = start;
            this.colorEnd = end;
            return this;
        }

        public Builder init(final NexusParticleModule.Init m) {
            this.initModules.add(m);
            return this;
        }

        public Builder update(final NexusParticleModule.Update m) {
            this.updateModules.add(m);
            return this;
        }

        public Builder force(final NexusParticleModule.Force m) {
            this.forceModules.add(m);
            return this;
        }

        public NexusParticleData build() {
            return new NexusParticleData(this);
        }
    }
}
