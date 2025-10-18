package net.sphen.magicmodbuns.spells.actions;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class ApplySinkEffectAction {
    /**
     * @param entity The entity to apply the effect to.
     * @param sink True to apply a sinking effect, false to apply a rising effect.
     */
    public void execute(LivingEntity entity, boolean sink) {
        if (sink) {

            // Apply Slowness and downwards motion
            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 2, false, false, false));
            entity.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 40, -5, false, false, false));
        } else {

            entity.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 40, 1, false, false, false));
        }
    }
}
