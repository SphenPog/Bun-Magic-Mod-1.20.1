package net.sphen.magicmodbuns.spells.logic;

import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.sphen.magicmodbuns.spells.SpellInstance;
import net.sphen.magicmodbuns.spells.SpellLogic;
import net.sphen.magicmodbuns.spells.actions.PlaceBlockAction;
import net.sphen.magicmodbuns.spells.runes.RunePatternGraph;

public class SpellLogicPlaceBlock extends SpellLogic {

    private final PlaceBlockAction placeBlockAction = new PlaceBlockAction();

    @Override
    public void cast(SpellInstance instance, Player player, RunePatternGraph graph) {
        Level level = player.level();
        if (level.isClientSide()) return;

        BlockPos origin = graph.getOrigin();
        JsonObject spellConfig = instance.definition.config;

        // Delegate the actual work to our reusable action class
        this.placeBlockAction.execute((ServerPlayer) player, spellConfig, origin);
    }
}
