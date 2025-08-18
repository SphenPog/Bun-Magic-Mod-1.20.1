package net.sphen.magicmodbuns.spells.logic;

import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import net.sphen.magicmodbuns.spells.SpellInstance;
import net.sphen.magicmodbuns.spells.SpellLogic;
import net.sphen.magicmodbuns.spells.runes.RunePatternGraph;

public class SpellLogicPlaceBlock extends SpellLogic {

    @Override
    public void cast(SpellInstance instance, Player player, RunePatternGraph graph) {
        Level level = player.level();
        if (level.isClientSide()) return;

        JsonObject spellConfig = instance.definition.config;

        String blockId = spellConfig.has("block") ? spellConfig.get("block").getAsString() : "minecraft:air";
        Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockId));

        if (block == null) {
            block = Blocks.AIR;
        }

        //getting block data for placement
        BlockState state = block.defaultBlockState();
        BlockPos origin = graph.getOrigin();
        String shape = spellConfig.has("shape") ? spellConfig.get("shape").getAsString() : "single";

        switch (shape) {

            case "single" -> placeBlock(level, origin, state);

            case "line" -> {
                int length = spellConfig.has("length") ? spellConfig.get("length").getAsInt() : 5;

                BlockPos direction = getFacingDirection(player, origin);

                for (int i = 0; i < length; i++) {
                    placeBlock(level, origin.offset(direction.getX() * i, direction.getY() * i, direction.getZ() * i), state);
                }
            }

            case "circle" -> {
                int radius = spellConfig.has("radius") ? spellConfig.get("radius").getAsInt() : 3;
                drawCircle(level, origin, radius, state);
            }

            case "square" -> {
                int size = spellConfig.has("size") ? spellConfig.get("size").getAsInt() : 3;
                for (int x = -size; x <= size; x++) {
                    for (int z = -size; z <= size; z++) {
                        placeBlock(level, origin.offset(x, 0, z), state);
                    }
                }
            }
        }
    }

    private void placeBlock(Level level, BlockPos pos, BlockState state) {
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.setBlockAndUpdate(pos, state);
        }
    }

    private void drawCircle(Level level, BlockPos center, int radius, BlockState state) {
        int x = radius;
        int z = 0;
        int decision = 1 - x;

        while (z <= x) {
            placeBlock(level, center.offset(x, 0, z), state);
            placeBlock(level, center.offset(z, 0, x), state);
            placeBlock(level, center.offset(-x, 0, z), state);
            placeBlock(level, center.offset(-z, 0, x), state);
            placeBlock(level, center.offset(-x, 0, -z), state);
            placeBlock(level, center.offset(-z, 0, -x), state);
            placeBlock(level, center.offset(x, 0, -z), state);
            placeBlock(level, center.offset(z, 0, -x), state);

            //following the circle curve
            z++;
            if (decision <= 0) {
                decision += 2 * z + 1;
            } else {
                x--;
                decision += 2 * (z - x) + 1;
            }
        }
    }

    private BlockPos getFacingDirection(Player player, BlockPos runePosition) {

        double deltaX = runePosition.getX() - player.getX();
        double deltaZ = runePosition.getZ() - player.getZ();

        if (Math.abs(deltaX) > Math.abs(deltaZ)) {
            return deltaX > 0 ? new BlockPos(1, 0, 0) : new BlockPos(-1, 0, 0);
        } else {
            return deltaZ > 0 ? new BlockPos(0, 0, 1) : new BlockPos(0, 0, -1);
        }
    }
}
