package net.sphen.magicmodbuns.spells.runes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.sphen.magicmodbuns.block.entity.ChalkPatternBlockEntity;

import java.util.*;

public class RuneSearch {

    private static final int[][] CARDINAL_OFFSETS = {
            {1,0}, {-1,0}, {0,1}, {0,-1}
    };

    private static final int[][] DIAGONAL_OFFSETS = {
            {1,1}, {1,-1}, {-1,1}, {-1,-1}
    };

    public static RunePatternGraph findPattern(Level level, BlockPos origin, int maxRadius, boolean allowDiagonals) {
      if (!isRune(level, origin)){
          return new RunePatternGraph();
      }

      final int y = origin.getY();
      RunePatternGraph graph = new RunePatternGraph();
      graph.setyLevel(y);

        List<int[]> neighborOffsets = new ArrayList<>(Arrays.asList(CARDINAL_OFFSETS));
        if (allowDiagonals) neighborOffsets.addAll(Arrays.asList(DIAGONAL_OFFSETS));

        Deque<BlockPos> queue = new ArrayDeque<>();
        Set<BlockPos> visitedRunes = new HashSet<>();

        RuneNode originNode = makeNode(level, origin);
        graph.addNode(originNode);
        visitedRunes.add(origin);
        queue.add(origin);

        final int maxReach = maxRadius + 1;

        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();

            for (int[] off : neighborOffsets) {
                BlockPos n1 = current.offset(off[0], 0, off[1]);
                BlockPos n2 = current.offset(off[0] * 2, 0, off[1] *2);

                if (n1.getY() != y) continue;
                if (!withinReach(origin, n1, maxReach)) continue;

                if (isRune(level, n1)) {
                    if (!visitedRunes.contains(n1)) {
                        RuneNode node = makeNode(level, n1);
                        graph.addNode(node);
                        visitedRunes.add(n1);
                        queue.add(n1);
                    }
                    graph.addEdge(current, n1);
                } else {
                    if (!withinReach(origin, n2, maxReach)) continue;
                    if(n2.getY() != y) continue;
                    if (isRune(level, n2)) {
                        graph.addBlank(n1);
                        if (!visitedRunes.contains(n2)){
                            RuneNode node = makeNode(level, n2);
                            graph.addNode(node);
                            visitedRunes.add(n2);
                            queue.add(n2);
                        }
                        graph.addEdge(current, n2);
                    }
                }
            }
        }

        return graph;
    }

    private static boolean withinReach(BlockPos origin, BlockPos pos, int maxReach) {
        int dx = Math.abs(pos.getX() - origin.getX());
        int dz = Math.abs(pos.getY() - origin.getY());
        return Math.max(dx, dz) <= maxReach;
    }

    private static RuneNode makeNode(Level level, BlockPos pos) {
        ChalkPatternBlockEntity blockEntity = (ChalkPatternBlockEntity) level.getBlockEntity(pos);

        return new RuneNode(
                pos.immutable(),
                blockEntity.getRuneType(),
                blockEntity.getElementType()
        );

    }

    private static boolean isRune(Level level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        return blockEntity instanceof ChalkPatternBlockEntity;
    }

}
