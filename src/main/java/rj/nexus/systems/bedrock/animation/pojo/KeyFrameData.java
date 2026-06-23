package rj.nexus.systems.bedrock.animation.pojo;

import org.jspecify.annotations.Nullable;
import java.util.Objects;

public abstract class KeyFrameData {
    private final double animationTime;
    private final @Nullable String locatorName;

    public KeyFrameData(double time, @Nullable String locatorName) {
        this.animationTime = time;
        this.locatorName = locatorName;
    }

    public double getTime() { return animationTime; }

    public @Nullable String getLocatorName() { return locatorName; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        return hashCode() == obj.hashCode();
    }

    @Override
    public int hashCode() { return Objects.hashCode(animationTime); }
}