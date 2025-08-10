package net.sphen.magicmodbuns.screen.spellbook;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class SpellBookItemStackHandler extends ItemStackHandler {
    private final TagKey<Item> allowedTag;

    public SpellBookItemStackHandler(int size, TagKey<Item> allowedTag) {
        super(size);
        this.allowedTag = allowedTag;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return stack.is(allowedTag);
    }

    public static void saveHandlerToStack(ItemStack stack, ItemStackHandler handler) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.put("Inventory", handler.serializeNBT());
    }

    public static void loadHandlerFromStack(ItemStack stack, ItemStackHandler handler) {
        CompoundTag tag = stack.getTag();
        if(tag != null && tag.contains("Inventory")) {
            handler.deserializeNBT(tag.getCompound("Inventory"));
        }
    }
}
