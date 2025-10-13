package net.sphen.magicmodbuns.spells.actions;

import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

public class PlaceBlockAction {

    /**
     * places blocks within given shape and args at origin (or origin + offsets).
     *
     * @param player        The caster, used to get level.
     * @param configArgs    The config from the JSON for the spell, used for: block, shape, length, height, direction, radius, size, and x/y/z offsets.
     * @param origin        The origin of the RunePatternGraph.
     */
    public void execute(ServerPlayer player, JsonObject configArgs, BlockPos origin) {

        String blockId = configArgs.get("block").getAsString();
        Block blockToPlace = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockId));

        if (blockToPlace == null) {
            System.err.println("Invalid block ID in spell: " + blockId);
            return;
        }
        BlockState blockState = blockToPlace.defaultBlockState();

        String shape = configArgs.get("shape").getAsString();
        switch (shape) {
            case "single" -> placeSingleBlock(player.level(), origin, configArgs, blockState);
            case "line" -> placeLineOfBlocks(player, origin, configArgs, blockState);
            case "wall" -> placeWallOfBlocks(player.level(), origin, configArgs, blockState);
            case "circle" -> placeCircle(player.level(), origin, configArgs, blockState);
            case "square" -> placeSquare(player.level(), origin, configArgs, blockState);
            default -> System.err.println("Unknown style for PlaceBlockAction: " + shape);
        }
    }

    private void placeSingleBlock(Level level, BlockPos origin, JsonObject args, BlockState blockState) {
        BlockPos offset = getOffsetPosFromJson(args);
        level.setBlockAndUpdate(origin.offset(offset), blockState);
    }

    private void placeLineOfBlocks(ServerPlayer player, BlockPos origin, JsonObject args, BlockState blockState) {
        BlockPos startOffset = getOffsetPosFromJson(args);
        int length = args.get("length").getAsInt();
        // Use the player's facing direction
        Direction playerDirection = player.getDirection();

        BlockPos startPos = origin.offset(startOffset);
        for (int i = 0; i < length; i++) {
            player.level().setBlock(startPos.relative(playerDirection, i), blockState, 3);
        }
    }

    private void placeWallOfBlocks(Level level, BlockPos origin, JsonObject args, BlockState blockState) {
        BlockPos startOffset = getOffsetPosFromJson(args);
        int length = args.get("length").getAsInt();
        int height = args.get("height").getAsInt();
        String directionName = args.get("direction").getAsString().toUpperCase();
        Direction direction = Direction.byName(directionName);

        if (direction == null || direction.getAxis().isVertical()) {
            System.err.println("Invalid horizontal direction for wall style: " + directionName);
            return;
        }

        BlockPos startPos = origin.offset(startOffset);
        for (int i = 0; i < length; i++) { // length
            for (int j = 0; j < height; j++) { // height
                BlockPos currentBase = startPos.relative(direction, i);
                level.setBlock(currentBase.above(j), blockState, 3);
            }
        }
    }

    private void placeCircle(Level level, BlockPos origin, JsonObject args, BlockState blockState) {
        BlockPos centerOffset = getOffsetPosFromJson(args);
        BlockPos center = origin.offset(centerOffset);
        int radius = args.get("radius").getAsInt();

        int x = radius;
        int z = 0;
        int decision = 1 - x;
        while (z <= x) {
            level.setBlock(center.offset(x, 0, z), blockState, 3);
            level.setBlock(center.offset(z, 0, x), blockState, 3);
            level.setBlock(center.offset(-x, 0, z), blockState, 3);
            level.setBlock(center.offset(-z, 0, x), blockState, 3);
            level.setBlock(center.offset(-x, 0, -z), blockState, 3);
            level.setBlock(center.offset(-z, 0, -x), blockState, 3);
            level.setBlock(center.offset(x, 0, -z), blockState, 3);
            level.setBlock(center.offset(z, 0, -x), blockState, 3);
            z++;
            if (decision <= 0) {
                decision += 2 * z + 1;
            } else {
                x--;
                decision += 2 * (z - x) + 1;
            }
        }
    }

    private void placeSquare(Level level, BlockPos origin, JsonObject args, BlockState blockState) {
        BlockPos centerOffset = getOffsetPosFromJson(args);
        BlockPos center = origin.offset(centerOffset);
        int size = args.get("size").getAsInt(); // size of 3 = 7x7 square (-3 and +3 from origin)

        for (int x = -size; x <= size; x++) {
            for (int z = -size; z <= size; z++) {
                level.setBlock(center.offset(x, 0, z), blockState, 3);
            }
        }
    }

    //handles any preset position offset in the Json
    private BlockPos getOffsetPosFromJson(JsonObject args) {
        int x = args.has("x") ? args.get("x").getAsInt() : 0;
        int y = args.has("y") ? args.get("y").getAsInt() : 0;
        int z = args.has("z") ? args.get("z").getAsInt() : 0;
        return new BlockPos(x, y, z);
    }
}
