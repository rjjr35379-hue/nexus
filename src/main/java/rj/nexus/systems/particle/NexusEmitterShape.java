package rj.nexus.systems.particle;

import net.minecraft.world.phys.Vec3;

import java.util.Random;

@FunctionalInterface
public interface NexusEmitterShape {

    Vec3 getSpawnPoint(Random rng, boolean surface);
}
