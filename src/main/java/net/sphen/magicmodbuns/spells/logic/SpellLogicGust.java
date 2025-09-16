package net.sphen.magicmodbuns.spells.logic;

import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.sphen.magicmodbuns.spells.SpellInstance;
import net.sphen.magicmodbuns.spells.SpellLogic;
import net.sphen.magicmodbuns.spells.runes.RunePatternGraph;
import org.joml.Vector3f;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SpellLogicGust extends SpellLogic {
    private static final ConcurrentHashMap<UUID, GustZone> activeGusts = new ConcurrentHashMap<>();

    @Override
    public void cast(SpellInstance instance, Player player, RunePatternGraph graph) {
        Level level = player.level();
        if (level.isClientSide()) return;
        ServerLevel serverLevel = (ServerLevel) level;

        JsonObject spellConfig = instance.definition.config;
        BlockPos origin = graph.getOrigin();

        //get opposite player direction
        Direction pushDirection = getDirectionFromPlayerToOrigin(player, origin);

        System.out.println("Casting From: " + origin.toShortString() +
                " | Player At: " + player.blockPosition().toShortString() +
                " | Calculated Direction: " + pushDirection.getName().toUpperCase());

        String shape = spellConfig.has("shape") ? spellConfig.get("shape").getAsString() : "line";
        double strength = spellConfig.has("strength") ? spellConfig.get("strength").getAsDouble() : 0.5;
        int duration = spellConfig.has("duration") ? spellConfig.get("duration").getAsInt() : 5;

        Vec3 forceVector = new Vec3(pushDirection.getStepX(), pushDirection.getStepY(), pushDirection.getStepZ()).scale(strength);

        switch (shape){

            case "line" -> {
                int length = spellConfig.has("length") ? spellConfig.get("length").getAsInt() : 5;

                BlockPos startPos = origin.relative(pushDirection);

                for (int i = 0; i < length; i++) {
                    BlockPos gustPos = startPos.relative(pushDirection, i);
                    AABB gustArea = new AABB(gustPos);
                    createGust(serverLevel, gustArea, forceVector, duration);
                }
            }

            case "wall" -> {
                int width = spellConfig.has("width") ? spellConfig.get("width").getAsInt() : 5;
                int height = spellConfig.has("height") ? spellConfig.get("height").getAsInt() : 3;
                int distance = spellConfig.has("distance") ? spellConfig.get("distance").getAsInt() : 1;

                Direction right = pushDirection.getClockWise();
                BlockPos wallCenter = origin.relative(pushDirection, distance);

                int halfWidth = width / 2;
                int halfHeight = height / 2;

                for (int w = -halfHeight; w <= halfHeight; w++) {
                    for (int h = 0; h < height; h++) {
                        BlockPos gustPos = wallCenter.relative(right, w).above(h);
                        AABB gustArea = new AABB(gustPos);
                        createGust(serverLevel, gustArea, forceVector, duration);
                    }
                }
            }

        }
    }

    public static void createGust(ServerLevel level, AABB aabb, Vec3 forceVector, int duration) {
       GustZone newGust = new GustZone(
               UUID.randomUUID(),
               aabb,
               forceVector,
               duration * 20,
               0,
               ParticleTypes.CLOUD
       );
       activeGusts.put(newGust.id(), newGust);
    }

    public static void onServerTick(ServerLevel level) {
        if (activeGusts.isEmpty()) return;

        activeGusts.forEach(((uuid, gust) -> {

            if (gust.isExpired()) {
                activeGusts.remove(uuid);
                return;
            }

            List<Entity> affectedEntities = level.getEntitiesOfClass(Entity.class, gust.areaOfEffect(), entity -> !entity.isSpectator());

            for (Entity entity : affectedEntities){
                entity.getTags().removeIf(tag -> tag.startsWith("gust_"));

                // Add new tags: one to identify, three to store the force vector
                entity.addTag("gust_push");
                entity.addTag("gust_x:" + gust.forceVector().x());
                entity.addTag("gust_y:" + gust.forceVector().y());
                entity.addTag("gust_z:" + gust.forceVector().z());
            }

            spawnParticlesInZone(level, gust);
            activeGusts.replace(uuid, gust.tick());
        }));
    }

    private static void spawnParticlesInZone(ServerLevel level, GustZone gust) {
        for (int i=0; i <5; i++) {
            AABB bounds = gust.areaOfEffect();
            double spawnX = (bounds.minX + bounds.maxX) / 2.0;
            double spawnY = (bounds.minY + bounds.maxY) / 2.0;
            double spawnZ = (bounds.minZ + bounds.maxZ) / 2.0;

            DustParticleOptions particleOptions = new DustParticleOptions(new Vector3f(0.8f, 0.8f, 0.8f), 1.0f);

            Vec3 baseVelocity = gust.forceVector();

            if (Math.abs(baseVelocity.y()) > Math.abs(baseVelocity.x()) && Math.abs(baseVelocity.y()) > Math.abs(baseVelocity.z())) {
                baseVelocity = new Vec3(0, baseVelocity.y(), 0);
            }

            RandomSource random = level.getRandom();
            double spread = 0.25; // Controls how much the particles spread out
            double velX = baseVelocity.x() + (random.nextDouble() - 0.5) * spread;
            double velY = baseVelocity.y() + (random.nextDouble() - 0.5) * spread;
            double velZ = baseVelocity.z() + (random.nextDouble() - 0.5) * spread;

            level.sendParticles(gust.particle(), spawnX, spawnY, spawnZ, 0,
                    velX, velY, velZ, 0.15);
        }

    }

    private Direction getDirectionFromPlayerToOrigin(Player player, BlockPos origin) {
        Vec3 vecToOrigin = Vec3.atCenterOf(origin).subtract(player.position());

        return Direction.getNearest(vecToOrigin.x, vecToOrigin.y, vecToOrigin.z);
    }
}
