package rj.nexus.systems.particle;

import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;


public final class NexusWorldParticle {

    public final float maxLife;
    public final float[] floatData = new float[4];
    public Vec3 pos;
    public Vec3 vel;
    public float life;
    public float size;
    public int color;
    public float alpha;
    public boolean alive = true;
    public @Nullable Object customData;

    public NexusWorldParticle(
            final Vec3 pos,
            final Vec3 vel,
            final float life,
            final float size,
            final int color
    ) {
        this.pos = pos;
        this.vel = vel;
        this.life = life;
        this.maxLife = life;
        this.size = size;
        this.color = color;
        this.alpha = 1f;
    }

    public float progress() {
        return 1f - (this.life / this.maxLife);
    }
}
