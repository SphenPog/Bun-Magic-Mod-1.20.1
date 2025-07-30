package net.sphen.magicmodbuns.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.sphen.magicmodbuns.block.ModBlocks;
import net.sphen.magicmodbuns.block.entity.ChalkPatternBlockEntity;
import net.sphen.magicmodbuns.screen.elements.PatternObject;


public class BlockPlacementHelper {

    public static void placeChalkPatternBlock(Level world, BlockPos pos, PatternObject pattern, String texturePath) {

        if (pattern instanceof PatternObject){
            System.out.println("pattern received (block placement helper data:) <----");
            System.out.println(pattern.getLines().size() + " - amount of lines. Lines: " + pattern.getLines());
            System.out.println(texturePath.toString());
        }

        // Place the block first
        BlockState blockState = ModBlocks.CHALK_PATTERN.get().defaultBlockState();
        world.setBlockAndUpdate(pos, blockState);

        // Retrieve the block entity
        BlockEntity entity = world.getBlockEntity(pos);
        if (entity instanceof ChalkPatternBlockEntity blockEntity) {
            blockEntity.setPattern(pattern);
            blockEntity.setTexturePath(texturePath);
            blockEntity.setChanged();
            blockEntity.syncWithClient();
        }
    }
}
