package net.sphen.magicmodbuns.events;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sphen.magicmodbuns.MagicMod;
import net.sphen.magicmodbuns.effect.ModEffects;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = MagicMod.MODID)
public class ModEvents {
    private static final UUID SWIM_SPEED_MODIFIER_ID = UUID.fromString("a4a2b9e4-4a2b-42d4-a1e8-9b8b7b7c8b4b");
    private static final AttributeModifier SWIM_MODIFIER = new AttributeModifier(SWIM_SPEED_MODIFIER_ID, "Fish swim speed", 1.5D, AttributeModifier.Operation.ADDITION);
    private static final Map<UUID, Integer> suffocationTimers = new HashMap<>();

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event){
        if (event.phase == TickEvent.Phase.START) return; // Process at the end of the tick
        if (event.player.level().isClientSide) return;

        ServerPlayer player = (ServerPlayer) event.player;
        UUID playerUUID = player.getUUID();
        AttributeInstance swimSpeedAttribute = player.getAttribute(ForgeMod.SWIM_SPEED.get());

        if (player.hasEffect(ModEffects.TRANSFORMATION.get())) {

            if (swimSpeedAttribute.getModifier(SWIM_SPEED_MODIFIER_ID) == null) {
                swimSpeedAttribute.addPermanentModifier(SWIM_MODIFIER);
            }

            //air supply when underwater
            if (player.isUnderWater() || player.isInWaterRainOrBubble()) {
                player.setAirSupply(player.getMaxAirSupply());

                suffocationTimers.put(playerUUID, 0);

                player.addEffect(new MobEffectInstance(
                        MobEffects.NIGHT_VISION,
                        60,
                        0,
                        false,
                        false,
                        true
                ));
            } else {
                int timer = suffocationTimers.getOrDefault(playerUUID, 0) + 1;
                suffocationTimers.put(playerUUID, timer);

                if (timer > 300 && timer % 20 == 0) {
                    player.hurt(player.damageSources().drown(), 2.0F); // Deals 1 heart of damage
                }

                //make slow
                if (player.onGround()) {
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 4, true, false));
                }
            }
        } else {
            //no speed buff when not fish
            if (swimSpeedAttribute.getModifier(SWIM_SPEED_MODIFIER_ID) != null) {
                swimSpeedAttribute.removeModifier(SWIM_SPEED_MODIFIER_ID);
            }
            suffocationTimers.remove(playerUUID);
        }
    }
}
