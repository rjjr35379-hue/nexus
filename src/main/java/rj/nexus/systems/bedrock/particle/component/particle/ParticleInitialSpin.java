package rj.nexus.systems.bedrock.particle.component.particle;

import com.google.gson.annotations.SerializedName;
import rj.nexus.systems.bedrock.particle.component.ParticleComponent;
import gg.moonflower.molangcompiler.api.MolangExpression;

public class ParticleInitialSpin implements ParticleComponent<ParticleInitialSpin> {
    @SerializedName("rotation")
    public MolangExpression rotation = MolangExpression.ZERO;
    @SerializedName("rotation_rate")
    public MolangExpression rotationRate = MolangExpression.ZERO;
}
