package net.sphen.magicmodbuns.screen.spellbook;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.sphen.magicmodbuns.screen.ModMenuTypes;
import net.sphen.magicmodbuns.util.ModTags;

public class SpellBookMenu extends AbstractContainerMenu {
    private final IItemHandler handler;

    public SpellBookMenu(int pContainerId, Inventory pPlayerInventory, FriendlyByteBuf buffer){
        this(pContainerId, pPlayerInventory, buffer.readItem());
    }

    public SpellBookMenu(int pContainerId, Inventory pPlayerInventory, ItemStack stack) {
        super(ModMenuTypes.SPELL_BOOK_MENU.get(), pContainerId);

        this.handler = stack.getCapability(ForgeCapabilities.ITEM_HANDLER)
                .orElseThrow(() -> new IllegalStateException("Item does not have an item handler!"));

        for (int i = 0; i < handler.getSlots(); i++) {
            this.addSlot(new SlotItemHandler(handler, i, 8 + i * 18, 18));
        }

        addPlayerInventory(pPlayerInventory, 8, 84);
    }

    private void addPlayerInventory(Inventory pPlayerInventory, int x, int y) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(pPlayerInventory, col + row * 9 + 9, x + col * 18, y + row *18));
            }
        }

        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(pPlayerInventory, col, x + col * 18, y + 58));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {

            ItemStack stackInSlot = slot.getItem();
            itemstack = stackInSlot.copy();

            int containerSlots = handler.getSlots();

            if (index < containerSlots) {

                if (!this.moveItemStackTo(stackInSlot, containerSlots, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {

                if (stackInSlot.is(ModTags.Items.SPELL_BOOK_PAPER_TAG)) {
                    if (!this.moveItemStackTo(stackInSlot, 0, containerSlots, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    return ItemStack.EMPTY;
                }
            }

            if (stackInSlot.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return true;
    }


}
