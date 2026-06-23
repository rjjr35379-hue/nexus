package rj.nexus.systems.particle.module;

import net.minecraft.world.phys.Vec3;
import rj.nexus.systems.particle.NexusParticleModule;
import rj.nexus.systems.particle.NexusWorldParticle;

public final class WindModule implements NexusParticleModule.Force {

    private final float turbulence;
    private Vec3 wind;
    private float time = 0f;

    public WindModule(final Vec3 wind, final float turbulence) {
        this.wind = wind;
        this.turbulence = turbulence;
    }

    public void setWind(final Vec3 wind) {
        this.wind = wind;
    }

    @Override
    public void applyForce(final NexusWorldParticle p, final float deltaTime) {
        this.time += deltaTime;
        final float nx = (float) (Math.sin(this.time * 1.3 + p.pos.x * 0.1) * this.turbulence);
        final float nz = (float) (Math.cos(this.time * 0.9 + p.pos.z * 0.1) * this.turbulence);
        p.vel = p.vel.add(
                this.wind.x * deltaTime + nx * deltaTime,
                this.wind.y * deltaTime,
                this.wind.z * deltaTime + nz * deltaTime
        );
    }
}
