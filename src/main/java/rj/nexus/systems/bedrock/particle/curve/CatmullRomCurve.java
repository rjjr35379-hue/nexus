package rj.nexus.systems.bedrock.particle.curve;

import com.google.gson.annotations.SerializedName;
import gg.moonflower.molangcompiler.api.MolangEnvironment;
import gg.moonflower.molangcompiler.api.MolangExpression;
import gg.moonflower.molangcompiler.api.exception.MolangRuntimeException;
import java.util.List;

public class CatmullRomCurve implements Curve {
    public List<MolangExpression> nodes;
    public MolangExpression input;
    @SerializedName("horizontal_range") public MolangExpression horizontalRange = MolangExpression.of(1f);

    @Override
    public float evaluate(MolangEnvironment env) throws MolangRuntimeException {
        float t = env.resolve(input) / env.resolve(horizontalRange);
        if (nodes.size() < 4) throw new MolangRuntimeException("CatmullRomCurve needs >= 4 nodes");
        int eff = nodes.size() - 2;
        int p1i = Math.min(Math.max((int)(t * (eff - 1)), 0), eff - 2) + 1;
        float p0 = env.resolve(nodes.get(p1i - 1)), p1 = env.resolve(nodes.get(p1i));
        float p2 = env.resolve(nodes.get(p1i + 1)), p3 = env.resolve(nodes.get(p1i + 2));
        float f = (t * (eff - 1)) - (p1i - 1);
        return 0.5f*((2*p1)+(-p0+p2)*f+(2*p0-5*p1+4*p2-p3)*f*f+(-p0+3*p1-3*p2+p3)*f*f*f);
    }
}
