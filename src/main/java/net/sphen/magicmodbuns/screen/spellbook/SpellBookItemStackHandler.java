package net.sphen.magicmodbuns.screen.spellbook;

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
}
