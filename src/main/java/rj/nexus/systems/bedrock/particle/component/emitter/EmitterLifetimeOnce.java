package rj.nexus.systems.bedrock.particle.component.emitter;

import com.google.gson.annotations.SerializedName;
import rj.nexus.systems.bedrock.particle.component.ParticleComponent;
import gg.moonflower.molangcompiler.api.MolangExpression;

public class EmitterLifetimeOnce implements ParticleComponent<EmitterLifetimeOnce> {
    @SerializedName("active_time")
    public MolangExpression activeTime = MolangExpression.of(10); // default: 10
}
