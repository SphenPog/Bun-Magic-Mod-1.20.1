package net.sphen.magicmodbuns.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.sphen.magicmodbuns.MagicMod;
import net.sphen.magicmodbuns.block.ModBlocks;
import net.sphen.magicmodbuns.util.ModTags;
import org.jetbrains.annotations.Nullable;
import java.util.concurrent.CompletableFuture;

public class ModBlockTagGenerator extends BlockTagsProvider {

    public ModBlockTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, MagicMod.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        this.tag(ModTags.Blocks.METAL_DETECTOR_VALUABLES)
                .addTag(Tags.Blocks.ORES);

        this.tag(BlockTags.LOGS_THAT_BURN)
                .add(ModBlocks.WILLOW_LOG.get())
                .add(ModBlocks.WILLOW_WOOD.get())
                .add(ModBlocks.STRIPPED_WILLOW_LOG.get())
                .add(ModBlocks.STRIPPED_WILLOW_WOOD.get());

        this.tag(BlockTags.PLANKS)
                .add(ModBlocks.WILLOW_PLANKS.get());

        this.tag(BlockTags.MINEABLE_WITH_AXE);

        this.tag(BlockTags.MINEABLE_WITH_HOE);

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.MORTAR_AND_PESTLE.get())
                .add(ModBlocks.RAW_LIMESTONE.get())
                .add(ModBlocks.POLISHED_LIMESTONE.get());

        this.tag(BlockTags.MINEABLE_WITH_SHOVEL);

        this.tag(BlockTags.NEEDS_STONE_TOOL);

        this.tag(BlockTags.NEEDS_IRON_TOOL);

        this.tag(BlockTags.NEEDS_DIAMOND_TOOL);

        this.tag(Tags.Blocks.NEEDS_NETHERITE_TOOL);

    }

}
