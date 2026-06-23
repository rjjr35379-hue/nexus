package rj.nexus.systems.particle.module;

import rj.nexus.systems.particle.NexusParticleModule;
import rj.nexus.systems.particle.NexusWorldParticle;

public final class ColorOverLifeModule implements NexusParticleModule.Update {

    private final int colorStart;
    private final int colorEnd;

    public ColorOverLifeModule(final int colorStart, final int colorEnd) {
        this.colorStart = colorStart;
        this.colorEnd = colorEnd;
    }

    private static int lerpChannel(final int from, final int to, final float t, final int shift) {
        final int a = (from >> shift) & 0xFF;
        final int b = (to >> shift) & 0xFF;
        return (int) (a + (b - a) * t);
    }

    @Override
    public void update(final NexusWorldParticle p, final float deltaTime) {
        final float t = p.progress();
        final int r = lerpChannel(this.colorStart, this.colorEnd, t, 16);
        final int g = lerpChannel(this.colorStart, this.colorEnd, t, 8);
        final int b = lerpChannel(this.colorStart, this.colorEnd, t, 0);
        final int a = lerpChannel(this.colorStart, this.colorEnd, t, 24);
        p.color = (a << 24) | (r << 16) | (g << 8) | b;
    }
}
