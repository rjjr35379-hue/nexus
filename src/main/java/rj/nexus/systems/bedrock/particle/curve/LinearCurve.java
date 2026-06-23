package rj.nexus.systems.bedrock.particle.curve;

import com.google.gson.annotations.SerializedName;
import gg.moonflower.molangcompiler.api.MolangEnvironment;
import gg.moonflower.molangcompiler.api.MolangExpression;
import gg.moonflower.molangcompiler.api.exception.MolangRuntimeException;

public class LinearCurve implements Curve {
    public MolangExpression[] nodes;
    public MolangExpression input;
    @SerializedName("horizontal_range") public MolangExpression horizontalRange = MolangExpression.of(1f);

    @Override
    public float evaluate(MolangEnvironment env) throws MolangRuntimeException {
        float t = env.resolve(input) / env.resolve(horizontalRange);
        if (nodes.length < 2) throw new MolangRuntimeException("LinearCurve needs >= 2 nodes");
        int left = Math.min((int)(t * (nodes.length - 1)), nodes.length - 2);
        float l = env.resolve(nodes[left]);
        float r = env.resolve(nodes[left + 1]);
        return l + ((t * (nodes.length - 1)) - left) * (r - l);
    }
}
