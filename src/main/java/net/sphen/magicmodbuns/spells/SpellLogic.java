package net.sphen.magicmodbuns.spells;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.sphen.magicmodbuns.spells.runes.RunePatternGraph;

public abstract class SpellLogic {
    public abstract void cast(SpellInstance instance, Player player, RunePatternGraph graph);

    protected BlockPos getFacingDirection(Player player, BlockPos runePosition) {

        double deltaX = runePosition.getX() - player.getX();
        double deltaZ = runePosition.getZ() - player.getZ();

        if (Math.abs(deltaX) > Math.abs(deltaZ)) {
            return deltaX > 0 ? new BlockPos(1, 0, 0) : new BlockPos(-1, 0, 0);
        } else {
            return deltaZ > 0 ? new BlockPos(0, 0, 1) : new BlockPos(0, 0, -1);
        }
    }
}
