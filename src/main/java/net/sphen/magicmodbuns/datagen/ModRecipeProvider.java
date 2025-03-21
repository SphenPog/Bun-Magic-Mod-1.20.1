package net.sphen.magicmodbuns.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.sphen.magicmodbuns.MagicMod;
import net.sphen.magicmodbuns.block.ModBlocks;
import net.sphen.magicmodbuns.item.ModItems;
import java.util.List;
import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {

    public ModRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        //raw limestone from limestone chunks
        twoByTwoPacker(pWriter, RecipeCategory.BUILDING_BLOCKS, ModBlocks.RAW_LIMESTONE.get().asItem(), ModItems.LIMESTONE_CHUNK.get(), 1);
        //polished limestone from raw limestone
        twoByTwoPacker(pWriter, RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_LIMESTONE.get().asItem(), ModBlocks.RAW_LIMESTONE.get().asItem(), 4);
        //willow wood from willow logs
        woodFromLogs(pWriter, ModBlocks.WILLOW_WOOD.get().asItem(), ModBlocks.WILLOW_LOG.get().asItem());
        //willow planks from willow logs
        planksFromLogs(pWriter, ModBlocks.WILLOW_PLANKS.get().asItem(), ModBlocks.WILLOW_LOG.get().asItem(), 4);
    }

    protected static void planksFromLogs(Consumer<FinishedRecipe> pFinishedRecipeConsumer, ItemLike pPlanks, ItemLike pLogs, int pResultCount) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, pPlanks, pResultCount)
                .requires(pLogs).group("planks").unlockedBy("has_logs", has(pLogs))
                .save(pFinishedRecipeConsumer, MagicMod.MODID + ":" + getItemName(pLogs) + "_to_" + getItemName(pPlanks));
    }

    protected static void woodFromLogs(Consumer<FinishedRecipe> pFinishedRecipeConsumer, ItemLike pWood, ItemLike pLog) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, pWood, 3)
                .define('#', pLog)
                .pattern("##").pattern("##").group("bark")
                .unlockedBy("has_log", has(pLog))
                .save(pFinishedRecipeConsumer, MagicMod.MODID + ":" + getItemName(pLog) + "_to_" + getItemName(pWood));
    }

    protected static void twoByTwoPacker(Consumer<FinishedRecipe> pFinishedRecipeConsumer, RecipeCategory pCategory, ItemLike pPacked, ItemLike pUnpacked, int amountResult) {
        ShapedRecipeBuilder.shaped(pCategory, pPacked, amountResult)
                .define('#', pUnpacked)
                .pattern("##").pattern("##")
                .unlockedBy(getHasName(pUnpacked), has(pUnpacked))
                .save(pFinishedRecipeConsumer, MagicMod.MODID + ":-2x2-" + getItemName(pUnpacked) + "_to_" + getItemName(pPacked));
    }

    protected static void oreSmelting(Consumer<FinishedRecipe> pFinishedRecipeConsumer, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTIme, String pGroup) {
        oreCooking(pFinishedRecipeConsumer, RecipeSerializer.SMELTING_RECIPE, pIngredients, pCategory, pResult, pExperience, pCookingTIme, pGroup, "_from_smelting");
    }

    protected static void oreBlasting(Consumer<FinishedRecipe> pFinishedRecipeConsumer, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTime, String pGroup) {
        oreCooking(pFinishedRecipeConsumer, RecipeSerializer.BLASTING_RECIPE, pIngredients, pCategory, pResult, pExperience, pCookingTime, pGroup, "_from_blasting");
    }

    protected static void oreCooking(Consumer<FinishedRecipe> pFinishedRecipeConsumer, RecipeSerializer<? extends AbstractCookingRecipe> pCookingSerializer, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTime, String pGroup, String pRecipeName) {
        for(ItemLike itemlike : pIngredients) {
            SimpleCookingRecipeBuilder.generic(Ingredient.of(itemlike), pCategory, pResult,
                    pExperience, pCookingTime, pCookingSerializer)
                    .group(pGroup).unlockedBy(getHasName(itemlike), has(itemlike))
                    .save(pFinishedRecipeConsumer, MagicMod.MODID + ":" + getItemName(pResult) + pRecipeName + "_" + getItemName(itemlike));
        }

    }
}
