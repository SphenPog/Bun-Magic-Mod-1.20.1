package net.sphen.magicmodbuns.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.sphen.magicmodbuns.MagicMod;
import net.sphen.magicmodbuns.block.ModBlocks;
import net.sphen.magicmodbuns.item.ModItems;
import net.sphen.magicmodbuns.util.ModTags;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagGenerator extends ItemTagsProvider {

    public ModItemTagGenerator(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, CompletableFuture<TagLookup<Block>> pBlockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, pLookupProvider, pBlockTags, MagicMod.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        this.tag(ModTags.Items.CHALK_TYPE_TAG)
                .add(ModItems.CHALK.get().asItem())
                .add(ModItems.CHALK_FIRE.get().asItem())
                .add(ModItems.CHALK_AIR.get().asItem());

        this.tag(ModTags.Items.SPELL_BOOK_PAPER_TAG)
                .add(Items.PAPER)
                .add(ModItems.SPELL_PAPER.get());

        this.tag(ItemTags.LOGS_THAT_BURN)
                .add(ModBlocks.WILLOW_LOG.get().asItem())
                .add(ModBlocks.WILLOW_WOOD.get().asItem())
                .add(ModBlocks.STRIPPED_WILLOW_LOG.get().asItem())
                .add(ModBlocks.STRIPPED_WILLOW_WOOD.get().asItem());

        this.tag(ItemTags.PLANKS)
                .add(ModBlocks.WILLOW_PLANKS.get().asItem());
    }
}
