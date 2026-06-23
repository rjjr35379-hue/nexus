package rj.nexus.systems.particle;


public interface NexusParticleModule {

    @FunctionalInterface
    interface Init {
        void init(NexusWorldParticle particle);
    }

    @FunctionalInterface
    interface Update {
        void update(NexusWorldParticle particle, float deltaTime);
    }

    @FunctionalInterface
    interface Force {
        void applyForce(NexusWorldParticle particle, float deltaTime);
    }
}
