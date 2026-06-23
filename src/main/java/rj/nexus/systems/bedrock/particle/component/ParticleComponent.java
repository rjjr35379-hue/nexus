package rj.nexus.systems.bedrock.particle.component;

public interface ParticleComponent<P> {
    @SuppressWarnings("unchecked")
    default P value() { return (P) this; }
}
