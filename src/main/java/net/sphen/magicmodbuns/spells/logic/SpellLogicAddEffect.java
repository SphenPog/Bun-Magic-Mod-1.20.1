package net.sphen.magicmodbuns.spells.logic;

import com.google.gson.JsonObject;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.sphen.magicmodbuns.MagicMod;
import net.sphen.magicmodbuns.effect.ModEffects;
import net.sphen.magicmodbuns.spells.SpellInstance;
import net.sphen.magicmodbuns.spells.SpellLogic;
import net.sphen.magicmodbuns.spells.runes.RunePatternGraph;

public class SpellLogicAddEffect extends SpellLogic {
    @Override
    public void cast(SpellInstance instance, Player player, RunePatternGraph graph) {
        Level level = player.level();
        if (level.isClientSide()) return;

        JsonObject spellConfig = instance.definition.config;
        String appliedEffect = spellConfig.has("effect") ? spellConfig.get("effect").getAsString() : "null";

        //add custom effect types here
        switch (appliedEffect) {

            case "null" -> {
                MagicMod.LOGGER.error(instance.definition.logic + " effect is null or not properly defined.");
            }

            case "transform" -> {
                String transformType = spellConfig.has("type") ? spellConfig.get("type").getAsString() : "null";
                transform(instance, player, appliedEffect, transformType);
            }
        }
    }

    //add custom transform logic here
    private static void transform(SpellInstance instance, Player nearestPlayer, String effect, String transformType){

        switch (transformType) {
            case "null" -> {
                MagicMod.LOGGER.error(instance.definition.logic + " transform type is null or not properly defined.");
            }

            case "aquatic" -> {
                if (nearestPlayer != null) {
                    int fishTypeAmplifier = nearestPlayer.getRandom().nextInt(3);
                    int durationInTicks = 2400; //2min

                    nearestPlayer.addEffect(new MobEffectInstance(
                            ModEffects.TRANSFORMATION.get(),
                            durationInTicks,
                            fishTypeAmplifier,
                            false,
                            false,
                            true
                    ));

                    nearestPlayer.sendSystemMessage(Component.literal("You feel a little more... Fishy."));
                }
            }
        }
    }


}
