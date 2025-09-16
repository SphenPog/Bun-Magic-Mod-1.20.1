package net.sphen.magicmodbuns.spells.logic;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public record GustZone(
        UUID id,
        AABB areaOfEffect,
        Vec3 forceVector,
        int durationTicks,
        int age,
        ParticleOptions particle
) {
    public GustZone tick() {
        return new GustZone(id, areaOfEffect, forceVector, durationTicks, age + 1, particle);
    }

    public boolean isExpired() {
        return this.age >= this.durationTicks;
    }
}
