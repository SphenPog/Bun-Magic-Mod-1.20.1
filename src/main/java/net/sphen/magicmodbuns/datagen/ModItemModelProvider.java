package net.sphen.magicmodbuns.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import net.sphen.magicmodbuns.MagicMod;
import net.sphen.magicmodbuns.block.ModBlocks;
import net.sphen.magicmodbuns.item.ModItems;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, MagicMod.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        simpleItem(ModItems.CHALK);
        simpleItem(ModItems.LIMESTONE_CHUNK);
        simpleItem(ModItems.METAL_DETECTOR);

        simpleBlockItem(ModBlocks.POLISHED_LIMESTONE);
        simpleBlockItem(ModBlocks.RAW_LIMESTONE);
        simpleBlockItem(ModBlocks.POTION_BOTTLE_BLOCK);

        BlockSpecialItem(ModBlocks.MORTAR_AND_PESTLE);
    }

    private ItemModelBuilder simpleItem(RegistryObject<Item> item){
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(MagicMod.MODID, "item/" + item.getId().getPath()));
    }

    private ItemModelBuilder simpleBlockItem(RegistryObject<Block> block){
        return withExistingParent(block.getId().getPath(),
                new ResourceLocation(MagicMod.MODID, "block/" + block.getId().getPath()));
    }

    private ItemModelBuilder BlockSpecialItem(RegistryObject<Block> block){
        return withExistingParent(block.getId().getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(MagicMod.MODID, "item/" + block.getId().getPath()));
    }
}
