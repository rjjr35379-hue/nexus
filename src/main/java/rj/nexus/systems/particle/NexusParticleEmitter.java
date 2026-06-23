package rj.nexus.systems.particle;

import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class NexusParticleEmitter {

    private static final Random RNG = new Random();

    private final NexusParticleData data;
    private final List<NexusWorldParticle> particles = new ArrayList<>();
    private Vec3 origin;

    public NexusParticleEmitter(final NexusParticleData data, final Vec3 origin) {
        this.data = data;
        this.origin = origin;
    }

    private static float lerp(final float a, final float b, final float t) {
        return a + (b - a) * t;
    }

    private static float lerpChannel(final int a, final int b, final float t, final int shift) {
        final float ca = ((a >> shift) & 0xFF) / 255f;
        final float cb = ((b >> shift) & 0xFF) / 255f;
        return ca + (cb - ca) * t;
    }

    public void burst() {
        this.burst(this.data.count);
    }

    public void burst(final int count) {
        for (int i = 0; i < count; i++) this.spawn();
    }

    private void spawn() {
        final Vec3 offset = this.data.shape.getSpawnPoint(RNG, false);
        final Vec3 pos = this.origin.add(offset);
        final Vec3 vel = offset.normalize().scale(this.data.randomSpeed(RNG));
        final float life = this.data.randomLife(RNG);
        final NexusWorldParticle particle =
                new NexusWorldParticle(pos, vel, life, this.data.sizeStart, this.data.colorStart);
        this.data.initModules.forEach(m -> m.init(particle));
        this.particles.add(particle);
    }

    public void tick(final float deltaTime) {
        this.particles.removeIf(p -> !p.alive);
        for (final NexusWorldParticle p : this.particles) {
            this.data.forceModules.forEach(m -> m.applyForce(p, deltaTime));
            p.pos = p.pos.add(p.vel.scale(deltaTime));
            p.life -= deltaTime;
            if (p.life <= 0f) {
                p.alive = false;
                continue;
            }
            this.data.updateModules.forEach(m -> m.update(p, deltaTime));
            final float t = p.progress();
            p.size = lerp(this.data.sizeStart, this.data.sizeEnd, t);
            p.alpha = lerpChannel(this.data.colorStart, this.data.colorEnd, t, 24);
        }
    }

    public List<NexusWorldParticle> getParticles() {
        return Collections.unmodifiableList(this.particles);
    }

    public Vec3 getOrigin() {
        return this.origin;
    }

    public void setOrigin(final Vec3 o) {
        this.origin = o;
    }

    public boolean isDead() {
        return this.particles.isEmpty();
    }
}
