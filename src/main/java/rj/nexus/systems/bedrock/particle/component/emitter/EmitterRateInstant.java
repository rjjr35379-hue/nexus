package rj.nexus.systems.bedrock.particle.component.emitter;

import com.google.gson.annotations.SerializedName;
import rj.nexus.systems.bedrock.particle.component.ParticleComponent;
import gg.moonflower.molangcompiler.api.MolangExpression;

public class EmitterRateInstant implements ParticleComponent<EmitterRateInstant> {
    @SerializedName("num_particles")
    public MolangExpression numParticles = MolangExpression.of(10); // default: 10
}
