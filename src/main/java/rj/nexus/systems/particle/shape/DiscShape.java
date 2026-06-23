package rj.nexus.systems.particle.shape;

import net.minecraft.world.phys.Vec3;
import rj.nexus.systems.particle.NexusEmitterShape;

import java.util.Random;

public final class DiscShape implements NexusEmitterShape {

    private final float radius;

    public DiscShape(final float radius) {
        this.radius = radius;
    }

    @Override
    public Vec3 getSpawnPoint(final Random rng, final boolean surface) {
        final float angle = (float) (rng.nextFloat() * Math.PI * 2.0);
        final float rad = surface ? this.radius : this.radius * (float) Math.sqrt(rng.nextFloat());
        return new Vec3(Math.cos(angle) * rad, 0.0, Math.sin(angle) * rad);
    }
}
