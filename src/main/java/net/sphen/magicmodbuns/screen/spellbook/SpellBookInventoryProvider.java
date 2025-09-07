package net.sphen.magicmodbuns.util.capabilities;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.sphen.magicmodbuns.screen.spellbook.SpellBookItemStackHandler;
import net.sphen.magicmodbuns.util.ModTags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.sphen.magicmodbuns.item.custom.SpellBookItem.maxPages;

public class SpellBookInventoryProvider implements ICapabilitySerializable<CompoundTag> {

    private final SpellBookItemStackHandler inventory = new SpellBookItemStackHandler(maxPages, ModTags.Items.SPELL_BOOK_PAPER_TAG);

    private final LazyOptional<IItemHandler> optional = LazyOptional.of(() -> inventory);

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return optional.cast();
        }
        return LazyOptional.empty();
    }

    // Forge calls this automatically to SAVE the inventory
    @Override
    public CompoundTag serializeNBT() {
        return inventory.serializeNBT();
    }

    // Forge calls this automatically to LOAD the inventory
    @Override
    public void deserializeNBT(CompoundTag nbt) {
        inventory.deserializeNBT(nbt);
    }
}

