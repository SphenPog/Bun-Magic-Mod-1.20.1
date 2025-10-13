package net.sphen.magicmodbuns.spells.actions;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;
import net.sphen.magicmodbuns.MagicMod;

public class ApplyMobEffectAction {
    /**
     * applies a mob effect to a target entity based on a JSON configuration.
     * @param target The LivingEntity to apply the effect to.
     * @param config The JSON object containing effect details. Expected keys:
     * - "effect_id" (String): The resource location of the effect (e.g., "minecraft:speed").
     * - "duration" (int): The duration of the effect in ticks.
     * - "amplifier" (int): The strength of the effect.
     * - "ambient" (boolean, optional): If the effect is ambient.
     * - "visible" (boolean, optional): If the effect particles are visible.
     * - "show_icon" (boolean, optional): If the icon is shown on the HUD.
     */
    public void execute(LivingEntity target, JsonObject config) {
        if (!config.has("effect_id") || !config.has("duration") || !config.has("amplifier")) {
            MagicMod.LOGGER.error("ApplyMobEffectAction: has incorrect effect_id, duration, or amplifier value.");
            return;
        }

        ResourceLocation effectId = new ResourceLocation(config.get("effect_id").getAsString());
        MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(effectId);

        if (effect == null) {
            MagicMod.LOGGER.error("ApplyMobEffectAction: effect value is null.");
            return;
        }

        int duration = config.get("duration").getAsInt();
        int amplifier = config.get("amplifier").getAsInt();
        boolean ambient = config.has("ambient") && config.get("ambient").getAsBoolean();
        boolean visible = config.has("visible") ? config.get("visible").getAsBoolean() : true;
        boolean showIcon = config.has("show_icon") ? config.get("show_icon").getAsBoolean() : true;

        target.addEffect(new MobEffectInstance(effect, duration, amplifier, ambient, visible, showIcon));
    }
}
