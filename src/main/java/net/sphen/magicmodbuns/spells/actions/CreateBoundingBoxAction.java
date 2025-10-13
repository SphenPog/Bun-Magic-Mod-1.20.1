package net.sphen.magicmodbuns.spells.actions;

import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.sphen.magicmodbuns.spells.runes.RunePatternGraph;

import java.util.ArrayList;
import java.util.List;

public class CreateBoundingBoxAction {

    /**
     * creates a list of AABB bounds based on shape in config.
     *
     * @param player The casting player used for direction.
     * @param graph The rune graph providing the origin.
     * @param config The spell's JSON config, defining the shape and its args.
     * @return A list of AABBs representing the desired shape.
     */
    public List<AABB> execute(Player player, RunePatternGraph graph, JsonObject config) {
        BlockPos origin = graph.getOrigin();
        String shape = config.has("shape") ? config.get("shape").getAsString() : "single";

        switch (shape) {
            case "line":
                return createLineBounds(player, origin, config);
            case "wall":
                return createWallBounds(player, origin, config);
            case "rune_area":
                AABB bounds = graph.getBounds();
                if (config.has("expand")) {
                    double expandValue = config.get("expand").getAsDouble();
                    bounds = bounds.inflate(expandValue);
                }
                return List.of(bounds);
            case "single":
            default:
                return List.of(new AABB(origin));
        }
    }

    private List<AABB> createLineBounds(Player player, BlockPos origin, JsonObject config) {
        List<AABB> boundsList = new ArrayList<>();
        int length = config.has("length") ? config.get("length").getAsInt() : 5;
        Direction direction = getDirectionFromPlayerToOrigin(player, origin);

        for (int i = 0; i < length; i++) {
            boundsList.add(new AABB(origin.relative(direction, i)));
        }
        return boundsList;
    }

    private List<AABB> createWallBounds(Player player, BlockPos origin, JsonObject config) {
        List<AABB> boundsList = new ArrayList<>();
        int width = config.has("width") ? config.get("width").getAsInt() : 5;
        int height = config.has("height") ? config.get("height").getAsInt() : 3;
        int distance = config.has("distance") ? config.get("distance").getAsInt() : 1;

        Direction direction = getDirectionFromPlayerToOrigin(player, origin);
        Direction right = direction.getClockWise();
        int halfWidth = width / 2;

        for (int d = 0; d < distance; d++) {
            BlockPos wallLayerCenter = origin.relative(direction, d);
            for (int w = -halfWidth; w <= halfWidth; w++) {
                for (int h = 0; h < height; h++) {
                    BlockPos wallPos = wallLayerCenter.relative(right, w).above(h);
                    boundsList.add(new AABB(wallPos));
                }
            }
        }
        return boundsList;
    }

    private Direction getDirectionFromPlayerToOrigin(Player player, BlockPos origin) {
        Vec3 vecToOrigin = Vec3.atCenterOf(origin).subtract(player.position());
        return Direction.getNearest(vecToOrigin.x, -vecToOrigin.y, vecToOrigin.z);
    }
}
