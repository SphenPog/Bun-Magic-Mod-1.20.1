package net.sphen.magicmodbuns.spells.logic.recipes;

import org.apache.commons.compress.utils.Lists;

import java.util.List;

public class LocateRecipeRegistry {

    public static final List<ILocateRecipes> RECIPES = Lists.newArrayList();

    public static void registerRecipes() {
        RECIPES.add(new LocateMineralRecipe());
        //add new recipes here
    }

}
