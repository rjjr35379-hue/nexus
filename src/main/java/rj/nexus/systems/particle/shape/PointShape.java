package rj.nexus.systems.particle.shape;

import net.minecraft.world.phys.Vec3;
import rj.nexus.systems.particle.NexusEmitterShape;

import java.util.Random;

public final class PointShape implements NexusEmitterShape {
    public static final PointShape INSTANCE = new PointShape();

    @Override
    public Vec3 getSpawnPoint(final Random rng, final boolean surface) {
        return Vec3.ZERO;
    }
}
