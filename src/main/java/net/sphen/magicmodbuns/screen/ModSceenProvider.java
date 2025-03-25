package net.sphen.magicmodbuns.screen;

import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;

public class ModSceenProvider implements MenuProvider {


    @Override
    public Component getDisplayName() {
        return Component.literal("Chalk Menu");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new ChalkMenu(pContainerId, pPlayerInventory);
    }

}
