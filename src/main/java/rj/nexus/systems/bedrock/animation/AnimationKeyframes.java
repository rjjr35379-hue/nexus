package rj.nexus.systems.bedrock.animation;

import org.joml.Vector3f;

import java.util.Map;
import java.util.TreeMap;

public class AnimationKeyframes {
    private final TreeMap<Float, Keyframe> kfs = new TreeMap<>();

    public void add(float t, Keyframe kf) {
        kfs.put(t, kf);
    }

    public boolean isEmpty() {
        return kfs.isEmpty();
    }

    public Vector3f evaluate(float time) {
        if (kfs.isEmpty()) return new Vector3f();
        if (kfs.size() == 1) return new Vector3f(kfs.firstEntry().getValue().value());
        var floor = kfs.floorEntry(time);
        var ceil = kfs.ceilingEntry(time);
        if (floor == null) return new Vector3f(kfs.firstEntry().getValue().value());
        if (ceil == null) return new Vector3f(kfs.lastEntry().getValue().value());
        if (floor.getKey().equals(ceil.getKey())) return new Vector3f(floor.getValue().value());
        float alpha = (time - floor.getKey()) / (ceil.getKey() - floor.getKey());
        Keyframe k0 = floor.getValue(), k1 = ceil.getValue();
        if (k0.easing() == EasingType.CATMULLROM) return catmullRom(floor, ceil, alpha);
        Vector3f from = k0.post() != null ? k0.post() : k0.value();
        Vector3f to = k1.pre() != null ? k1.pre() : k1.value();
        float e = k0.easing().ease(alpha);
        return new Vector3f(from.x + (to.x - from.x) * e, from.y + (to.y - from.y) * e, from.z + (to.z - from.z) * e);
    }

    private Vector3f catmullRom(Map.Entry<Float, Keyframe> e1, Map.Entry<Float, Keyframe> e2, float a) {
        var e0 = kfs.lowerEntry(e1.getKey());
        var e3 = kfs.higherEntry(e2.getKey());
        Vector3f p0 = e0 != null ? new Vector3f(e0.getValue().value()) : new Vector3f(e1.getValue().value());
        Vector3f p1 = new Vector3f(e1.getValue().post() != null ? e1.getValue().post() : e1.getValue().value());
        Vector3f p2 = new Vector3f(e2.getValue().pre() != null ? e2.getValue().pre() : e2.getValue().value());
        Vector3f p3 = e3 != null ? new Vector3f(e3.getValue().value()) : new Vector3f(e2.getValue().value());
        float t2 = a * a, t3 = t2 * a;
        return new Vector3f(cm(p0.x, p1.x, p2.x, p3.x, a, t2, t3), cm(p0.y, p1.y, p2.y, p3.y, a, t2, t3), cm(p0.z, p1.z, p2.z, p3.z, a, t2, t3));
    }

    private float cm(float a, float b, float c, float d, float t, float t2, float t3) {
        return .5f * (2 * b + (-a + c) * t + (2 * a - 5 * b + 4 * c - d) * t2 + (-a + 3 * b - 3 * c + d) * t3);
    }

    public record Keyframe(Vector3f pre, Vector3f post, Vector3f data, EasingType easing) {
        public Vector3f value() {
            return data != null ? data : post != null ? post : pre != null ? pre : new Vector3f();
        }
    }
}
