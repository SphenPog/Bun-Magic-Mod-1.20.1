package net.sphen.magicmodbuns.block;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sphen.magicmodbuns.MagicMod;
import net.sphen.magicmodbuns.block.custom.MortarAndPestal;
import net.sphen.magicmodbuns.item.ModItems;

import java.util.function.Supplier;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, MagicMod.MODID);

    //blocks
    public static final RegistryObject<Block> POLISHED_LIMESTONE = registerBlock("polished_limestone",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.STONE)));
    public static final RegistryObject<Block> RAW_LIMESTONE = registerBlock("raw_limestone",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.ANDESITE)
                    .strength(2f).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> MORTAR_AND_PESTLE = registerBlock("mortar_and_pestle",
            () -> new MortarAndPestal(BlockBehaviour.Properties.of().noOcclusion()));
    //add new blocks here ^^
    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block){
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block){
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus){
        BLOCKS.register(eventBus);
    }

}
