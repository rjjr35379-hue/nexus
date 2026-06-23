package rj.nexus.systems.particle.module;

import rj.nexus.systems.particle.NexusParticleModule;
import rj.nexus.systems.particle.NexusWorldParticle;

public final class VelocityDampModule implements NexusParticleModule.Update {

    public static final VelocityDampModule DEFAULT = new VelocityDampModule(0.02f);

    private final float damping;

    public VelocityDampModule(final float damping) {
        this.damping = damping;
    }

    @Override
    public void update(final NexusWorldParticle p, final float deltaTime) {
        p.vel = p.vel.scale(1.0 - this.damping);
    }
}
