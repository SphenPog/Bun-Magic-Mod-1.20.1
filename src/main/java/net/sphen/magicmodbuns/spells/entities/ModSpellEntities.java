package net.sphen.magicmodbuns.spells.entities;

import net.minecraft.world.entity.EntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.sphen.magicmodbuns.MagicMod;


public class ModSpellEntities {

    public static final DeferredRegister<EntityType<?>> SPELL_ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MagicMod.MODID);



    public static void register(IEventBus eventBus){
        SPELL_ENTITIES.register(eventBus);
    }
}
