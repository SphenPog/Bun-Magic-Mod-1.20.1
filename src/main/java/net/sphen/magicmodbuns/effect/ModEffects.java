package net.sphen.magicmodbuns.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sphen.magicmodbuns.MagicMod;

public class ModEffects {
    // Create a DeferredRegister for MobEffects
    public static final DeferredRegister<MobEffect> MOB_EFFECTS
            = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MagicMod.MODID);

    // Register our transformation effect
    public static final RegistryObject<MobEffect> TRANSFORMATION = MOB_EFFECTS.register("transformation",
            TransformationEffect::new);


    // This method is called from your main mod class to register the DeferredRegister to the event bus
    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
