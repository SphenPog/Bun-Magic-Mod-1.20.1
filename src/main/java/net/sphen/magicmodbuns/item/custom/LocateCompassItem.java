package net.sphen.magicmodbuns.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LocateCompassItem extends Item {
    public static final String tagTargetX = "TargetX";
    public static final String tagTargetY = "TargetY";
    public static final String tagTargetZ = "TargetZ";
    public static final String tagTargetDimension = "TargetDimension";

    public LocateCompassItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean isFoil(ItemStack pStack) {
        return true;
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        if (pLevel.isClientSide) {
            CompoundTag nbt = pStack.getTag();
            if (nbt != null && nbt.contains(tagTargetX)) {
                nbt.put("LodestonePos", NbtUtils.writeBlockPos(
                        new BlockPos(nbt.getInt(tagTargetX), nbt.getInt(tagTargetY), nbt.getInt(tagTargetZ))));
                nbt.putString("LodestoneDimension", nbt.getString(tagTargetDimension));
                nbt.putBoolean("LodestoneTracked", true);
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        CompoundTag nbt = pStack.getTag();
        if (nbt != null && nbt.contains(tagTargetX)) {
            String dimension = nbt.getString(tagTargetDimension);

            pTooltipComponents.add(Component.translatable("item.magicmodbuns.locate_compass.tooltip.dimension", dimension)
                    .withStyle(ChatFormatting.LIGHT_PURPLE));
        } else {
            pTooltipComponents.add(Component.translatable("item.magicmodbuns.locate_compass.tooltip.unattuned")
                    .withStyle(ChatFormatting.GRAY));
        }

        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }
}
