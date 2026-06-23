package rj.nexus.systems.particle.module;

import rj.nexus.systems.particle.NexusParticleModule;
import rj.nexus.systems.particle.NexusWorldParticle;

public final class GravityModule implements NexusParticleModule.Force {

    public static final GravityModule DEFAULT = new GravityModule(9.8f);

    private final float gravity;

    public GravityModule(final float gravity) {
        this.gravity = gravity;
    }

    @Override
    public void applyForce(final NexusWorldParticle p, final float deltaTime) {
        p.vel = p.vel.add(0.0, -this.gravity * deltaTime * deltaTime, 0.0);
    }
}
