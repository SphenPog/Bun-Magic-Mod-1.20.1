package net.sphen.magicmodbuns.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import net.sphen.magicmodbuns.MagicMod;
import net.sphen.magicmodbuns.block.ChalkType;
import net.sphen.magicmodbuns.block.ModBlocks;
import net.sphen.magicmodbuns.item.ModItems;

import java.util.ArrayList;
import java.util.List;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, MagicMod.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        simpleItem(ModItems.CHALK);
        simpleItem(ModItems.CHALK_FIRE);
        simpleItem(ModItems.CHALK_AIR);
        simpleItem(ModItems.LIMESTONE_CHUNK);
        simpleItem(ModItems.METAL_DETECTOR);

        simpleItem(ModItems.SPELL_BOOK);
        spellPaperBuilder(ModItems.SPELL_PAPER);

        compassItem(ModItems.LOCATE_COMPASS);

        simpleBlockItem(ModBlocks.POLISHED_LIMESTONE);
        simpleBlockItem(ModBlocks.RAW_LIMESTONE);
        simpleBlockItem(ModBlocks.POTION_BOTTLE_BLOCK);
        simpleBlockItem(ModBlocks.CHALK_PATTERN);

        blockSpecialItem(ModBlocks.MORTAR_AND_PESTLE);
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

    private ItemModelBuilder blockSpecialItem(RegistryObject<Block> block){
        return withExistingParent(block.getId().getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(MagicMod.MODID, "item/" + block.getId().getPath()));
    }

    private void spellPaperBuilder(RegistryObject<Item> item) {
        ItemModelBuilder spellPaperBuilder = withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated"))
                .texture("layer0", new ResourceLocation(MagicMod.MODID, "item/spell_paper_base"));

        for (ChalkType chalk : ChalkType.values()) {

            String variantName = "spell_paper_" + ChalkType.getColorById(chalk.getId());
            ResourceLocation variantModelLocation = new ResourceLocation(MagicMod.MODID, "item/variants/" + variantName);

            withExistingParent("item/variants/" + variantName, new ResourceLocation("item/generated"))
                    .texture("layer0", new ResourceLocation(MagicMod.MODID, "item/variants/" + variantName));

            float predicateValue = (float)chalk.getId();

            spellPaperBuilder.override()
                    .predicate(new ResourceLocation(MagicMod.MODID, "spell_variant"), predicateValue)
                    .model(new ModelFile.UncheckedModelFile(variantModelLocation))
                    .end();
        }
    }

    private void compassItem(RegistryObject<Item> item) {
        List<ItemModelBuilder> angleModels = new ArrayList<>();

        for (int i = 0; i <32; i++) {
            String formattedIndex = String.format("%02d", i);
            String modelName = item.getId().getPath() + "_" + formattedIndex;

            angleModels.add(
                getBuilder(modelName)
                        .parent(getExistingFile(mcLoc("item/generated")))
                      .texture("layer0", modLoc("item/compass/" + modelName))
            );
        }

        ItemModelBuilder mainModelBuilder = getBuilder(item.getId().getPath())
                .parent(getExistingFile(mcLoc("item/generated")))
                .texture("layer0", modLoc("item/compass/" + item.getId().getPath() + "_00"));

        for (int i = 0; i <32; i++){
            mainModelBuilder.override()
                    .predicate(mcLoc("angle"), i / 32.0f)
                    .model(angleModels.get(i))
                    .end();
        }
    }
}
