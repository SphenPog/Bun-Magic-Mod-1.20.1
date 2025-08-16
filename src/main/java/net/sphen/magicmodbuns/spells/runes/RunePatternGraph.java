package net.sphen.magicmodbuns.spells.runes;

import com.google.common.collect.HashMultimap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import com.google.common.collect.Multimap;

import java.util.*;

public class RunePatternGraph {
    private final Map<BlockPos, RuneNode> nodes = new HashMap<>();
    private final Set<BlockPos> blanks = new HashSet<>();
    private final Multimap<BlockPos, BlockPos> edges = HashMultimap.create();
    private int yLevel;
    private AABB bounds = new AABB(0,0,0,0,0,0);

    public void setyLevel(int yLevel) {
        this.yLevel = yLevel;
    }

    public int getyLevel() {
        return yLevel;
    }

    public boolean hasNode(BlockPos pos) {
        return nodes.containsKey(pos);
    }

    public RuneNode getNode(BlockPos pos) {
        return nodes.get(pos);
    }

    public Collection<RuneNode> getNodes() {
        return nodes.values();
    }

    public void addNode(RuneNode node){
        nodes.put(node.pos, node);
        expandBounds(node.pos);
    }

    public void addBlank(BlockPos pos){
        blanks.add(pos);
        expandBounds(pos);
    }

    public Set<BlockPos> getBlanks() {
        return Collections.unmodifiableSet(blanks);
    }

    public void addEdge(BlockPos from, BlockPos to){
        edges.put(from, to);
        edges.put(to, from);
    }

    public Iterable<BlockPos> neighbors(BlockPos pos){
        return edges.get(pos);
    }

    public AABB getBounds() {
        return bounds;
    }

    private void expandBounds(BlockPos pos) {
        if (nodes.size() + blanks.size() == 1){
            bounds = new AABB(pos);
        } else {
            bounds = bounds.minmax(new AABB(pos));
        }
    }

}
