package net.sphen.magicmodbuns.spells.util.recipes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;

import java.util.List;

public interface ILocateRecipes {

    boolean matches(List<ItemEntity> items, Level level, BlockPos pos);

    void performCraft(List<ItemEntity> items, Level level, BlockPos pos);

}
