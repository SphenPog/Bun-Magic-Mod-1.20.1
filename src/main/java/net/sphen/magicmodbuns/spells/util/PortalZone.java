package net.sphen.magicmodbuns.spells.util;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

    /**
     * A data record holding all the information for a single portal.
     */
    public record PortalZone(
            UUID id,
            UUID casterId,
            BlockPos pos,
            ResourceKey<Level> dimension,
            AABB areaOfEffect,
            Optional<UUID> linkedPortalId,
            long expirationTick,
            List<BlockPos> runePositions,
            BlockState originalBlockState
    ) {
        public PortalZone withLink(UUID otherId) {
            return new PortalZone(id, casterId, pos, dimension, areaOfEffect, Optional.of(otherId), expirationTick, runePositions, originalBlockState);
        }

        public boolean isExpired(long currentTime) {
            return currentTime > expirationTick;
        }
    }

