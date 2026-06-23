package rj.nexus.systems.bedrock.animation.pojo;

import org.jspecify.annotations.Nullable;
import java.util.Objects;

public class SoundKeyframeData extends KeyFrameData {
    private final String sound;

    public SoundKeyframeData(double time, String sound, @Nullable String locator) {
        super(time, locator);
        this.sound = sound;
    }

    public String getSound() { return sound; }

    @Override
    public int hashCode() { return Objects.hash(getTime(), sound); }
}