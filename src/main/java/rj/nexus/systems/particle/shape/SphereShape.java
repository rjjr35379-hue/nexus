package rj.nexus.systems.particle.shape;

import net.minecraft.world.phys.Vec3;
import rj.nexus.systems.particle.NexusEmitterShape;

import java.util.Random;

public final class SphereShape implements NexusEmitterShape {

    private final float radius;

    public SphereShape(final float radius) {
        this.radius = radius;
    }

    @Override
    public Vec3 getSpawnPoint(final Random rng, final boolean surface) {
        final float theta = (float) (rng.nextFloat() * Math.PI * 2.0);
        final float phi = (float) Math.acos(2.0 * rng.nextFloat() - 1.0);
        final float rad = surface ? this.radius : this.radius * (float) Math.cbrt(rng.nextFloat());
        return new Vec3(
                rad * Math.sin(phi) * Math.cos(theta),
                rad * Math.cos(phi),
                rad * Math.sin(phi) * Math.sin(theta)
        );
    }
}
