package rj.nexus.systems.particle.shape;

import net.minecraft.world.phys.Vec3;
import rj.nexus.systems.particle.NexusEmitterShape;

import java.util.Random;

public final class CylinderShape implements NexusEmitterShape {

    private final float radius;
    private final float height;

    public CylinderShape(final float radius, final float height) {
        this.radius = radius;
        this.height = height;
    }

    @Override
    public Vec3 getSpawnPoint(final Random rng, final boolean surface) {
        final float angle = (float) (rng.nextFloat() * Math.PI * 2.0);
        final float rad = surface ? this.radius : this.radius * (float) Math.sqrt(rng.nextFloat());
        final float y = (rng.nextFloat() * 2f - 1f) * this.height * 0.5f;
        return new Vec3(Math.cos(angle) * rad, y, Math.sin(angle) * rad);
    }
}
