package net.sphen.magicmodbuns.spells.logic;

import com.google.gson.JsonObject;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.sphen.magicmodbuns.MagicMod;
import net.sphen.magicmodbuns.spells.SpellInstance;
import net.sphen.magicmodbuns.spells.SpellLogic;
import net.sphen.magicmodbuns.spells.actions.ApplyMobEffectAction;
import net.sphen.magicmodbuns.spells.runes.RunePatternGraph;

public class SpellLogicTransform extends SpellLogic {
    private final ApplyMobEffectAction applyMobEffectAction = new ApplyMobEffectAction();

    @Override
    public void cast(SpellInstance instance, Player player, RunePatternGraph graph) {
        Level level = player.level();
        if (level.isClientSide()) return;

        JsonObject spellConfig = instance.definition.config;
        String transformType = spellConfig.has("type") ? spellConfig.get("type").getAsString() : "null";

        switch (transformType) {
            case "aquatic" -> handleAquaticTransform(player, spellConfig);
            default -> {
                MagicMod.LOGGER.error("transformType in SpellLogicTransform is unknown.");
            }
        }
    }

    private void handleAquaticTransform(Player player, JsonObject spellConfig) {

        int fishTypeAmplifier = player.getRandom().nextInt(3);
        int durationInTicks = (spellConfig.has("duration") ? spellConfig.get("duration").getAsInt() : 120) * 20;
        boolean showIcon = spellConfig.has("show_icon") && spellConfig.get("show_icon").getAsBoolean();

        JsonObject effectConfig = new JsonObject();
        effectConfig.addProperty("effect_id", "magicmodbuns:transformation");
        effectConfig.addProperty("duration", durationInTicks);
        effectConfig.addProperty("amplifier", fishTypeAmplifier);
        effectConfig.addProperty("show_icon", showIcon);

        this.applyMobEffectAction.execute(player, effectConfig);

        player.sendSystemMessage(Component.literal("You feel a little more... Fishy."));
    }
}
