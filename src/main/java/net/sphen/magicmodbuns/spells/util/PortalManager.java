package net.sphen.magicmodbuns.spells.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.sphen.magicmodbuns.block.entity.PuddlePortalBlockEntity;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages the LINKING of portals. The portals themselves handle their own ticking logic.
 */
public class PortalManager {

    // Stores the BlockPos of unlinked portals
    private static final ConcurrentHashMap<UUID, BlockPos> unlinkedPortalsByCaster = new ConcurrentHashMap<>();

    public static boolean tryLinkPortal(PuddlePortalBlockEntity newPortal) {
        UUID casterId = newPortal.getCasterId();
        Level level = newPortal.getLevel();
        if (level == null || level.isClientSide()) return false;

        if (unlinkedPortalsByCaster.containsKey(casterId)) {
            BlockPos partnerPos = unlinkedPortalsByCaster.remove(casterId);
            BlockEntity partnerBE = level.getBlockEntity(partnerPos);

            if (partnerBE instanceof PuddlePortalBlockEntity partnerPortal) {
                newPortal.linkTo(partnerPortal);
                return true;
            }
        }

        // No partner found, add this portal as unlinked
        unlinkedPortalsByCaster.put(casterId, newPortal.getBlockPos());
        return false;
    }
}
