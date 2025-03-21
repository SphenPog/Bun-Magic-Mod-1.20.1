package net.sphen.magicmodbuns.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sphen.magicmodbuns.MagicMod;
import net.sphen.magicmodbuns.block.ModBlocks;


public class ModBlockStateProvider extends BlockStateProvider {

    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, MagicMod.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        //generic blocks
        blockWithItem(ModBlocks.POLISHED_LIMESTONE);
        blockWithItem(ModBlocks.RAW_LIMESTONE);

        //willow wood
        logBlock(((RotatedPillarBlock) ModBlocks.WILLOW_LOG.get()));
        axisBlock(((RotatedPillarBlock) ModBlocks.WILLOW_WOOD.get()), blockTexture(ModBlocks.WILLOW_LOG.get()), blockTexture(ModBlocks.WILLOW_LOG.get()));
        axisBlock(((RotatedPillarBlock) ModBlocks.STRIPPED_WILLOW_LOG.get()), blockTexture(ModBlocks.STRIPPED_WILLOW_LOG.get()),
                new ResourceLocation(MagicMod.MODID, "block/stripped_willow_log_top"));
        axisBlock(((RotatedPillarBlock) ModBlocks.STRIPPED_WILLOW_WOOD.get()), blockTexture(ModBlocks.STRIPPED_WILLOW_LOG.get()),
                blockTexture(ModBlocks.STRIPPED_WILLOW_LOG.get()));

        blockItem(ModBlocks.WILLOW_LOG);
        blockItem(ModBlocks.WILLOW_WOOD);
        blockItem(ModBlocks.STRIPPED_WILLOW_LOG);
        blockItem(ModBlocks.STRIPPED_WILLOW_WOOD);
        blockWithItem(ModBlocks.WILLOW_PLANKS);
        leavesBlock(ModBlocks.WILLOW_LEAVES);

        //modeled blocks
        horizontalBlock(ModBlocks.MORTAR_AND_PESTLE.get(),
                new ModelFile.UncheckedModelFile(modLoc("block/mortar_and_pestle")));
        simpleBlock(ModBlocks.POTION_BOTTLE_BLOCK.get(),
                new ModelFile.UncheckedModelFile(modLoc("block/potion_bottle")));
    }

    private void leavesBlock(RegistryObject<Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(),
                models().singleTexture(ForgeRegistries.BLOCKS.getKey(blockRegistryObject.get()).getPath(), new ResourceLocation("minecraft:block/leaves"),
                        "all", blockTexture(blockRegistryObject.get())).renderType("cutout"));
    }

    private void blockItem(RegistryObject<Block> blockRegistryObject){
        simpleBlockItem(blockRegistryObject.get(), new ModelFile.UncheckedModelFile(MagicMod.MODID + ":block/"
                + ForgeRegistries.BLOCKS.getKey(blockRegistryObject.get()).getPath()));
    }

    private void blockWithItem(RegistryObject<Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()));
    }
}
