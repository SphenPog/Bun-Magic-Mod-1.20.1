package net.sphen.magicmodbuns.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
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
    public static final String tagExpiration = "ExpirationTime";
    public static final int maxExpirationTicks = 3600; //(20 ticks/sec) 3min

    public LocateCompassItem(Properties pProperties) {
        super(pProperties.defaultDurability(maxExpirationTicks));
    }

    @Override
    public boolean isFoil(ItemStack pStack) {
        return true;
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        if (!pLevel.isClientSide) {
            CompoundTag nbt = pStack.getTag();

            if (nbt != null && nbt.contains(tagExpiration)) {
                long expirationTime = nbt.getLong(tagExpiration);

                if (pLevel.getGameTime() >= expirationTime) {
                    pLevel.playSound(null, pEntity.getX(), pEntity.getY(), pEntity.getZ(), SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 1.0f, 1.0f);
                    pLevel.playSound(null, pEntity.getX(), pEntity.getY(), pEntity.getZ(), SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 1.0f, 1.0f);

                    pStack.setCount(0);
                    return;
                }
            }
        }

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

    @Override
    public boolean isBarVisible(ItemStack pStack) {
        return pStack.getTag() != null && pStack.getTag().contains(tagExpiration);
    }

    @Override
    public int getBarWidth(ItemStack pStack) {
        CompoundTag nbt = pStack.getTag();
        if (nbt == null || !nbt.contains(tagExpiration)){
            return 0;
        }

        long expirationTime = nbt.getLong(tagExpiration);
        long currentTime = Minecraft.getInstance().level.getGameTime();
        long remainingTicks = expirationTime - currentTime;

        if (remainingTicks <=0){
            return 0;
        }

        return Math.round(13.0f * remainingTicks / maxExpirationTicks);
    }

    @Override
    public int getBarColor(ItemStack pStack) {
        CompoundTag nbt = pStack.getTag();
        if (nbt == null || !nbt.contains(tagExpiration)){
            return super.getBarColor(pStack);
        }

        long expirationTime = nbt.getLong(tagExpiration);
        long currentTime = Minecraft.getInstance().level.getGameTime();
        long remainingTicks = expirationTime - currentTime;

        float fraction = Math.max(0.0F, (float) remainingTicks / maxExpirationTicks);

        return Mth.hsvToRgb(fraction / 3.0F, 1.0F, 1.0F);
    }
}
