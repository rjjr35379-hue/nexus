package rj.nexus.systems.particle.shape;

import net.minecraft.world.phys.Vec3;
import rj.nexus.systems.particle.NexusEmitterShape;

import java.util.Random;

public final class BoxShape implements NexusEmitterShape {

    private final float halfWidth;
    private final float halfHeight;
    private final float halfDepth;

    public BoxShape(final float halfWidth, final float halfHeight, final float halfDepth) {
        this.halfWidth = halfWidth;
        this.halfHeight = halfHeight;
        this.halfDepth = halfDepth;
    }

    @Override
    public Vec3 getSpawnPoint(final Random rng, final boolean surface) {
        return new Vec3(
                (rng.nextFloat() * 2f - 1f) * this.halfWidth,
                (rng.nextFloat() * 2f - 1f) * this.halfHeight,
                (rng.nextFloat() * 2f - 1f) * this.halfDepth
        );
    }
}
