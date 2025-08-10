package net.sphen.magicmodbuns.screen.chalk;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.sphen.magicmodbuns.screen.ModMenuTypes;

public class ChalkMenu extends AbstractContainerMenu {

    public ChalkMenu(int pContainerId, Inventory pPlayerInventory) {
        super(ModMenuTypes.CHALK_MENU.get(), pContainerId);
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        return null;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return true;
    }
}
