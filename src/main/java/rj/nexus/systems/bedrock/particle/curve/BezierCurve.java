package rj.nexus.systems.bedrock.particle.curve;

import com.google.gson.annotations.SerializedName;
import gg.moonflower.molangcompiler.api.MolangEnvironment;
import gg.moonflower.molangcompiler.api.MolangExpression;
import gg.moonflower.molangcompiler.api.exception.MolangRuntimeException;
import java.util.List;

public class BezierCurve implements Curve {
    public List<MolangExpression> nodes;
    public MolangExpression input;
    @SerializedName("horizontal_range") public MolangExpression horizontalRange = MolangExpression.of(1f);

    @Override
    public float evaluate(MolangEnvironment env) throws MolangRuntimeException {
        float t = env.resolve(input) / env.resolve(horizontalRange);
        if (nodes.size() != 4) throw new MolangRuntimeException("BezierCurve needs exactly 4 nodes");
        float p0=env.resolve(nodes.get(0)), p1=env.resolve(nodes.get(1));
        float p2=env.resolve(nodes.get(2)), p3=env.resolve(nodes.get(3));
        return (float)(Math.pow(1-t,3)*p0+3*Math.pow(1-t,2)*t*p1+3*(1-t)*t*t*p2+t*t*t*p3);
    }
}
