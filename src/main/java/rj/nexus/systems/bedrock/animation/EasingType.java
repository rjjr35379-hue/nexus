package rj.nexus.systems.bedrock.animation;

public enum EasingType {
    LINEAR {
        public float ease(float t) {
            return t;
        }
    },
    SMOOTH {
        public float ease(float t) {
            return t * t * (3 - 2 * t);
        }
    },
    STEP {
        public float ease(float t) {
            return t < 1 ? 0 : 1;
        }
    },
    CATMULLROM {
        public float ease(float t) {
            return t;
        }

        public boolean needsFour() {
            return true;
        }

        public float interpolate(float t, float p0, float p1, float p2, float p3) {
            float t2 = t * t, t3 = t2 * t;
            return .5f * (2 * p1 + (-p0 + p2) * t + (2 * p0 - 5 * p1 + 4 * p2 - p3) * t2 + (-p0 + 3 * p1 - 3 * p2 + p3) * t3);
        }
    },
    BEZIER {
        public float ease(float t) {
            float mt = 1 - t;
            return 3 * mt * mt * t + 3 * mt * t * t + t * t * t;
        }

        public boolean needsFour() {
            return true;
        }

        public float interpolate(float t, float p0, float p1, float p2, float p3) {
            float mt = 1 - t;
            return mt * mt * mt * p0 + 3 * mt * mt * t * p1 + 3 * mt * t * t * p2 + t * t * t * p3;
        }
    };

    public static EasingType fromString(String s) {
        if (s == null) return LINEAR;
        return switch (s.toLowerCase()) {
            case "catmullrom", "catmull_rom" -> CATMULLROM;
            case "smooth", "ease" -> SMOOTH;
            case "step" -> STEP;
            case "bezier" -> BEZIER;
            default -> LINEAR;
        };
    }

    public abstract float ease(float t);

    public boolean needsFour() {
        return false;
    }

    public float interpolate(float t, float p0, float p1, float p2, float p3) {
        return p0 + (p1 - p0) * ease(t);
    }
}
