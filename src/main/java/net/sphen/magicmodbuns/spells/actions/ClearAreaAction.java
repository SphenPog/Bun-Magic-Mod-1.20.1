package net.sphen.magicmodbuns.spells.actions;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class ClearAreaAction {

    /**
     * Clears all blocks within the provided list of AABBs.
     * @param level The level in which to clear blocks.
     * @param boundsList A list of AABBs to clear.
     */
    public void execute(Level level, List<AABB> boundsList) {

        if (level.isClientSide()) return;

        for (AABB bounds : boundsList) {
            // The max corner of an AABB is exclusive, so we subtract 1 to get the last block inside.
            BlockPos min = BlockPos.containing(bounds.minX, bounds.minY, bounds.minZ);
            BlockPos max = BlockPos.containing(bounds.maxX - 1, bounds.maxY - 1, bounds.maxZ - 1);

            for (BlockPos pos : BlockPos.betweenClosed(min, max)) {
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            }
        }
    }
}
