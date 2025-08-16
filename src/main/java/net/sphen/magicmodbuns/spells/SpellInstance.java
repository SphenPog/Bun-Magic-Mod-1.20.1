package net.sphen.magicmodbuns.spells;

import net.minecraft.world.entity.player.Player;
import net.sphen.magicmodbuns.spells.runes.RunePatternGraph;

import java.util.Map;

public class SpellInstance {
    SpellDefinition definition;
    Player caster;
    RunePatternGraph graph;
    public Map<String, Object> properties;

    void cast() {
        definition.logic.cast(this, caster, graph);
    }
}
