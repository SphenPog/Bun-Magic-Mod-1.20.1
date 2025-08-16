package net.sphen.magicmodbuns.spells;

import net.minecraft.world.entity.player.Player;
import net.sphen.magicmodbuns.spells.runes.RunePatternGraph;

abstract class SpellLogic {
    abstract void cast(SpellInstance instance, Player player, RunePatternGraph graph);
}
