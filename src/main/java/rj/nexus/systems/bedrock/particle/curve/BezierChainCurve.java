package rj.nexus.systems.bedrock.particle.curve;

import gg.moonflower.molangcompiler.api.MolangEnvironment;
import gg.moonflower.molangcompiler.api.MolangExpression;
import gg.moonflower.molangcompiler.api.exception.MolangRuntimeException;
import java.util.Map;

public class BezierChainCurve implements Curve {
    public Map<String, Node> nodes;
    public MolangExpression input;

    @Override
    public float evaluate(MolangEnvironment env) throws MolangRuntimeException {
        throw new UnsupportedOperationException("bezier_chain is not yet supported");
    }

    public static class Node {
        public float value, left_value, right_value, slope, left_slope, right_slope;
    }
}
