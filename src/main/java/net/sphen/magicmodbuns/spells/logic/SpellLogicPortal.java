package net.sphen.magicmodbuns.spells.logic;

import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.sphen.magicmodbuns.block.ModBlocks;
import net.sphen.magicmodbuns.block.entity.PuddlePortalBlockEntity;
import net.sphen.magicmodbuns.spells.SpellInstance;
import net.sphen.magicmodbuns.spells.SpellLogic;
import net.sphen.magicmodbuns.spells.actions.ClearAreaAction;
import net.sphen.magicmodbuns.spells.actions.PlaySoundAction;
import net.sphen.magicmodbuns.spells.runes.RunePatternGraph;
import net.sphen.magicmodbuns.spells.util.PortalManager;

import java.util.List;

public class SpellLogicPortal extends SpellLogic {

    private final ClearAreaAction clearAreaAction = new ClearAreaAction();
    private final PlaySoundAction playSoundAction = new PlaySoundAction();

    @Override
    public void cast(SpellInstance instance, Player player, RunePatternGraph graph) {
        Level level = player.level();
        if (level.isClientSide()) return;

        BlockPos origin = graph.getOrigin();
        JsonObject spellConfig = instance.definition.config;

        // Clear the rune blocks
        this.clearAreaAction.execute(level, List.of(graph.getBounds()));

        // Place the portal block
        level.setBlock(origin, ModBlocks.PUDDLE_PORTAL.get().defaultBlockState(), 3);

        // Get the Block Entity and set its initial data
        if (level.getBlockEntity(origin) instanceof PuddlePortalBlockEntity portalBE) {
            long durationTicks = (spellConfig.has("duration") ? spellConfig.get("duration").getAsInt() : 300) * 20;
            portalBE.setData(player.getUUID(), level.getGameTime() + durationTicks);

            boolean success = PortalManager.tryLinkPortal(portalBE);

            if (success) {
                this.playSoundAction.execute(level, origin, SoundEvents.END_PORTAL_SPAWN, SoundSource.MASTER, 1.0f, 1.0f);
                player.sendSystemMessage(Component.literal("The connection is stable!"));
            } else {
                this.playSoundAction.execute(level, origin, SoundEvents.AMBIENT_CAVE.get(), SoundSource.MASTER, 1.0f, 0.5f);
                player.sendSystemMessage(Component.literal("You feel a connection to another plane..."));
            }
        }
    }
}