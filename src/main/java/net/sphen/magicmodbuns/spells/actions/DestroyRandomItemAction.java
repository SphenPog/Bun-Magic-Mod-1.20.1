package net.sphen.magicmodbuns.spells.actions;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;

import java.util.List;

public class DestroyRandomItemAction {

    /**
     *
     * @param level The level where the items are.
     * @param items The items involved.
     * @param chance The chance modifier for an item to be destroyed.
     */
    public void execute(Level level, List<ItemEntity> items, float chance) {
        if (items.isEmpty() || level.isClientSide()) {
            return;
        }

        if (level.random.nextFloat() < chance) {
            // Get a random item from the list and discard it.
            items.get(level.random.nextInt(items.size())).discard();
        }
    }
}
