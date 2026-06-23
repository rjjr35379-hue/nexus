package rj.nexus.systems.bedrock.particle.component.emitter;

import com.google.gson.annotations.SerializedName;
import rj.nexus.systems.bedrock.particle.component.ParticleComponent;
import gg.moonflower.molangcompiler.api.MolangExpression;

public class EmitterShapeCustom implements ParticleComponent<EmitterShapeCustom> {
    @SerializedName("offset")
    public MolangExpression[] offset = new MolangExpression[]{MolangExpression.ZERO, MolangExpression.ZERO, MolangExpression.ZERO}; // default: [0, 0, 0]

    @SerializedName("direction")
    public MolangExpression[] direction = new MolangExpression[]{MolangExpression.ZERO, MolangExpression.ZERO, MolangExpression.ZERO}; // default: [0, 0, 0]
}
