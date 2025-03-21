package net.sphen.magicmodbuns.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.sphen.magicmodbuns.MagicMod;
import net.sphen.magicmodbuns.block.ModBlocks;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MagicMod.MODID);

    public static final RegistryObject<CreativeModeTab> MAGIC_TAB = CREATIVE_MODE_TABS.register("magic_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.CHALK.get()))
                    .title(Component.translatable("creativetab.magic_tab"))
                    .displayItems(((pParameters, pOutput) -> {
                        pOutput.accept(ModItems.CHALK.get());
                        pOutput.accept(ModItems.LIMESTONE_CHUNK.get());
                        pOutput.accept(ModItems.METAL_DETECTOR.get());

                        pOutput.accept(ModBlocks.POLISHED_LIMESTONE.get());
                        pOutput.accept(ModBlocks.RAW_LIMESTONE.get());

                        pOutput.accept(ModBlocks.WILLOW_LOG.get());
                        pOutput.accept(ModBlocks.WILLOW_WOOD.get());
                        pOutput.accept(ModBlocks.STRIPPED_WILLOW_LOG.get());
                        pOutput.accept(ModBlocks.STRIPPED_WILLOW_WOOD.get());
                        pOutput.accept(ModBlocks.WILLOW_PLANKS.get());
                        pOutput.accept(ModBlocks.WILLOW_LEAVES.get());

                        pOutput.accept(ModBlocks.MORTAR_AND_PESTLE.get());
                        pOutput.accept(ModBlocks.POTION_BOTTLE_BLOCK.get());
                        //add more items here (includes them in the creative menu)
                    }))
                    .build());

    public static void register (IEventBus eventBus){
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
