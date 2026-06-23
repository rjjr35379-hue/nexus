package rj.nexus.systems.bedrock.animation.pojo;

import java.util.Objects;

public class CustomInstructionKeyframeData extends KeyFrameData {
    private final String instructions;

    public CustomInstructionKeyframeData(double time, String instructions) {
        super(time, null);
        this.instructions = instructions;
    }

    public String getInstructions() { return instructions; }

    @Override
    public int hashCode() { return Objects.hash(getTime(), instructions); }
}