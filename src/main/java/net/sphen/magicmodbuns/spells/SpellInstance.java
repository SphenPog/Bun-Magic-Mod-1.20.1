package net.sphen.magicmodbuns.spells;

import net.minecraft.world.entity.player.Player;
import net.sphen.magicmodbuns.spells.runes.RunePatternGraph;

import java.util.Map;

public class SpellInstance {
    public SpellDefinition definition;
    public Player caster;
    public RunePatternGraph graph;
    public Map<String, Object> properties;

    public void cast() {
        definition.logic.cast(this, caster, graph);
    }
}
