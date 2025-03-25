package net.sphen.magicmodbuns.screen;

import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sphen.magicmodbuns.MagicMod;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, MagicMod.MODID);

    public static final RegistryObject<MenuType<MortarPestleMenu>> MORTAR_PESTLE_MENU =
            registerMenuType("mortar_pestle_menu", MortarPestleMenu :: new);

    public static final RegistryObject<MenuType<ChalkMenu>> CHALK_MENU =
            MENUS.register("chalk_menu", () -> new MenuType<>(ChalkMenu::new, FeatureFlags.VANILLA_SET));

    private static <T extends AbstractContainerMenu>RegistryObject<MenuType<T>> registerMenuType(String name, IContainerFactory<T> factory){
        return MENUS.register(name, () -> IForgeMenuType.create(factory));
    }

    public static void register(IEventBus eventBus){
        MENUS.register(eventBus);
    }

}
