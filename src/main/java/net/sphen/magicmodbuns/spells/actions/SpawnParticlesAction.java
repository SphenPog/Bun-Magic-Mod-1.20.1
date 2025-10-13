package net.sphen.magicmodbuns.spells.actions;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class SpawnParticlesAction {
    /**
     * spawns particles inside a bounding box.
     *
     * @param level        The server level to spawn particles in.
     * @param bounds       The AABB that defines the particle zone.
     * @param particle     The type of particle to spawn.
     * @param baseVelocity The directional velocity of the particles.
     * @param count        The number of particles to spawn.
     * @param spread       A factor controlling how much the particle velocity varies.
     */
    public void execute(ServerLevel level, AABB bounds, ParticleOptions particle, Vec3 baseVelocity, int count, double spread) {
        RandomSource random = level.getRandom();

        for (int i = 0; i < count; i++) {
            // get a random position inside the bounding box
            double spawnX = bounds.minX + random.nextDouble() * (bounds.getXsize());
            double spawnY = bounds.minY + random.nextDouble() * (bounds.getYsize());
            double spawnZ = bounds.minZ + random.nextDouble() * (bounds.getZsize());

            // directional spread
            double baseVelX = baseVelocity.x();
            double baseVelY = baseVelocity.y();
            double baseVelZ = baseVelocity.z();

            // determine the axis to create a column effect
            double absX = Math.abs(baseVelX);
            double absY = Math.abs(baseVelY);
            double absZ = Math.abs(baseVelZ);

            double spreadX = spread;
            double spreadY = spread;
            double spreadZ = spread;


            if (absX > absY && absX > absZ) { // moving along X axis
                spreadX *= 0.1; // reduce random X velocity
            } else if (absY > absX && absY > absZ) { // moving along Y axis
                spreadY *= 0.1; // reduce random Y velocity
            } else { // moving along Z axis
                spreadZ *= 0.1; // reduce random Z velocity
            }

            // add the variation to the velocity
            double velX = baseVelX + (random.nextDouble() - 0.5) * spreadX;
            double velY = baseVelY + (random.nextDouble() - 0.5) * spreadY;
            double velZ = baseVelZ + (random.nextDouble() - 0.5) * spreadZ;

            level.sendParticles(particle, spawnX, spawnY, spawnZ, 0, velX, velY, velZ, 1);
        }
    }
}