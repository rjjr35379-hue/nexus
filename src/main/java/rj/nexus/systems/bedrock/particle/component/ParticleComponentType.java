package rj.nexus.systems.bedrock.particle.component;

import net.minecraft.resources.Identifier;

public record ParticleComponentType<T extends ParticleComponent<?>>(Identifier id, Class<T> type) {}
