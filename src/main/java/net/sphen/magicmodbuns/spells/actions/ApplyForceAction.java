package net.sphen.magicmodbuns.spells.actions;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.sphen.magicmodbuns.MagicMod;
import net.sphen.magicmodbuns.util.Packets.PlayerVelocityPacket;

import java.util.List;

public class ApplyForceAction {

    /**
     * Applies a force to entities within the specified bounds.
     * @param level The server level.
     * @param bounds The AABB to check for entities.
     * @param forceVector The direction and magnitude of the force to apply.
     */
    public void execute(ServerLevel level, AABB bounds, Vec3 forceVector) {

        List<Entity> affectedEntities = level.getEntitiesOfClass(Entity.class, bounds, entity -> !entity.isSpectator());

        for (Entity entity : affectedEntities) {
            // Apply a multiplier
            Vec3 finalForce = forceVector.scale(0.2);

            if (entity instanceof ServerPlayer player) {
                // Use packets for players
                MagicMod.sendToPlayer(new PlayerVelocityPacket(finalForce), player);
            } else {
                entity.addDeltaMovement(finalForce);
            }
        }
    }
}
