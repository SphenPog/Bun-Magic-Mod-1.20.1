package net.sphen.magicmodbuns.spells.actions;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.util.ITeleporter;

public class TeleportEntityAction {
    /**
     * @param entity The entity to teleport.
     * @param targetLevel The server level of the destination.
     * @param targetPos The block position of the destination.
     */
    public void execute(LivingEntity entity, ServerLevel targetLevel, BlockPos targetPos) {
        BlockPos destination = targetPos.above();

        if (entity.level() != targetLevel) {
            entity.changeDimension(targetLevel, new ITeleporter() {
                @Override
                public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld, float yaw, java.util.function.Function<Boolean, Entity> repositionEntity) {
                    Entity e = repositionEntity.apply(false);
                    e.teleportTo(destination.getX() + 0.5, destination.getY(), destination.getZ() + 0.5);
                    return e;
                }
            });
        } else {
            entity.teleportTo(destination.getX() + 0.5, destination.getY(), destination.getZ() + 0.5);
        }

        entity.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 10, 1, false, false, false));
    }
}