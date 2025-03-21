package net.sphen.magicmodbuns.datagen.loot;

import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.registries.RegistryObject;
import net.sphen.magicmodbuns.block.ModBlocks;
import net.sphen.magicmodbuns.item.ModItems;

import java.util.Set;


public class ModBlockLootTables extends BlockLootSubProvider {

    public ModBlockLootTables() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        this.dropSelf(ModBlocks.POLISHED_LIMESTONE.get());
        this.add(ModBlocks.RAW_LIMESTONE.get(),
                block -> createRedstoneOreLikeDrops(ModBlocks.RAW_LIMESTONE.get(), ModItems.LIMESTONE_CHUNK.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(4.0F, 4.0F))));

        this.dropSelf(ModBlocks.MORTAR_AND_PESTLE.get());

        this.dropSelf(ModBlocks.POTION_BOTTLE_BLOCK.get());

        this.dropSelf(ModBlocks.WILLOW_LOG.get());
        this.dropSelf(ModBlocks.WILLOW_WOOD.get());
        this.dropSelf(ModBlocks.STRIPPED_WILLOW_LOG.get());
        this.dropSelf(ModBlocks.STRIPPED_WILLOW_WOOD.get());
        this.dropSelf(ModBlocks.WILLOW_PLANKS.get());

        this.add(ModBlocks.WILLOW_LEAVES.get(), block ->
                createLeavesDrops(block, ModBlocks.RAW_LIMESTONE.get(), NORMAL_LEAVES_SAPLING_CHANCES)); //LIMESTONE is temp for sapling
    }

    protected LootTable.Builder createRedstoneOreLikeDrops(Block pBlock, Item item) {
        return createSilkTouchDispatchTable(pBlock,
                this.applyExplosionDecay(pBlock,
                        LootItem.lootTableItem(item)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(4.0F, 5.0F)))
                            .apply(ApplyBonusCount.addUniformBonusCount(Enchantments.BLOCK_FORTUNE))));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
    }
}
