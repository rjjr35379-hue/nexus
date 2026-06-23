package rj.nexus.systems.bedrock.animation.pojo;

import org.jspecify.annotations.Nullable;
import java.util.Objects;

public class ParticleKeyframeData extends KeyFrameData {
    private final String effect;
    private final boolean bedrockParticle;

    public ParticleKeyframeData(double time, String effect, @Nullable String locator, boolean bedrockParticle) {
        super(time, locator);
        this.effect = effect;
        this.bedrockParticle = bedrockParticle;
    }

    public String getEffect() { return effect; }

    public boolean isBedrockParticle() { return bedrockParticle; }

    @Override
    public int hashCode() { return Objects.hash(getTime(), effect, getLocatorName()); }
}