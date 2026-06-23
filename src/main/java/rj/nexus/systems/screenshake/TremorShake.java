package rj.nexus.systems.screenshake;


import com.mojang.blaze3d.vertex.PoseStack;

public final class TremorShake implements ShakeInstance {
    private final ShakeParams params;

    public TremorShake(ShakeParams params) {
        this.params = params;
    }

    @Override
    public void apply(PoseStack stack, int elapsed, float partial) {
        float env = params.envelope(elapsed + partial);
        if (env <= 0f) return;
        float x = (triNoise(elapsed,     0x9e3779b9) * 0.5f
                +  triNoise(elapsed * 2, 0x6c62272e) * 0.3f
                +  triNoise(elapsed * 4, 0x517cc1b7) * 0.2f) * params.amplitude * env;
        float y = (triNoise(elapsed,     0x517cc1b7) * 0.5f
                +  triNoise(elapsed * 2, 0x9e3779b9) * 0.3f
                +  triNoise(elapsed * 4, 0x6c62272e) * 0.2f) * params.amplitude * env;
        float xc = mix(triNoise(elapsed - 1, 0x9e3779b9), triNoise(elapsed, 0x9e3779b9), partial) * params.amplitude * env;
        float yc = mix(triNoise(elapsed - 1, 0x517cc1b7), triNoise(elapsed, 0x517cc1b7), partial) * params.amplitude * env;
        stack.translate(xc, yc, 0f);
    }

    private static float mix(float a, float b, float t) {
        return a + (b - a) * t;
    }

    private static float triNoise(int tick, int salt) {
        int h = tick * salt;
        h ^= h >>> 16;
        h *= 0x45d9f3b;
        h ^= h >>> 16;
        return ((h & 0xffff) / (float) 0xffff) * 2f - 1f;
    }

    @Override
    public boolean expired(int elapsed) {
        return elapsed > params.duration();
    }
}