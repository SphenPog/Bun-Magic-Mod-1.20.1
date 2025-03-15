package net.sphen.magicmodbuns.item;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sphen.magicmodbuns.MagicMod;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MagicMod.MODID);

    public static final RegistryObject<Item> CHALK = ITEMS.register("chalk", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> LIMESTONE_CHUNK = ITEMS.register("limestone_chunk", () -> new Item(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
