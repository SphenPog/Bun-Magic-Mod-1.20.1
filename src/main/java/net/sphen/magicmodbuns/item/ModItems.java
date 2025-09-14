package net.sphen.magicmodbuns.item;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sphen.magicmodbuns.MagicMod;
import net.sphen.magicmodbuns.item.custom.LocateCompassItem;
import net.sphen.magicmodbuns.item.custom.MetalDetectorItem;
import net.sphen.magicmodbuns.item.custom.SpellBookItem;
import net.sphen.magicmodbuns.item.custom.SpellPaperItem;
import net.sphen.magicmodbuns.item.custom.chalks.AirChalkItem;
import net.sphen.magicmodbuns.item.custom.chalks.ChalkItem;
import net.sphen.magicmodbuns.item.custom.chalks.FireChalkItem;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MagicMod.MODID);

    public static final RegistryObject<Item> CHALK = ITEMS.register("chalk",
            () -> new ChalkItem(new Item.Properties()));
    public static final RegistryObject<Item> CHALK_FIRE = ITEMS.register("chalk_fire",
            () -> new FireChalkItem(new Item.Properties()));
    public static final RegistryObject<Item> CHALK_AIR = ITEMS.register("chalk_air",
            () -> new AirChalkItem(new Item.Properties()));

    public static final RegistryObject<Item> LOCATE_COMPASS = ITEMS.register("locate_compass",
            () -> new LocateCompassItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> SPELL_BOOK = ITEMS.register("spell_book",
            () -> new SpellBookItem(new Item.Properties()));
    public static final RegistryObject<Item> SPELL_PAPER = ITEMS.register("spell_paper",
            () -> new SpellPaperItem(new Item.Properties()));
    public static final RegistryObject<Item> LIMESTONE_CHUNK = ITEMS.register("limestone_chunk",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> METAL_DETECTOR = ITEMS.register("metal_detector",
            () -> new MetalDetectorItem(new Item.Properties().durability(100)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
