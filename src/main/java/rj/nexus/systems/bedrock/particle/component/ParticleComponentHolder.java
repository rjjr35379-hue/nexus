package rj.nexus.systems.bedrock.particle.component;

public interface ParticleComponentHolder {
    ParticleComponentMap components();
    default <T extends ParticleComponent<?>> T get(ParticleComponentType<T> type) { return components().get(type); }
    default <T extends ParticleComponent<?>> boolean has(ParticleComponentType<T> type) { return components().has(type); }
    default <T extends ParticleComponent<?>> void set(ParticleComponentType<T> type, T value) { components().set(type, value); }
    default void initComponents(ParticleComponentMap map) { components().from(map); }
}
