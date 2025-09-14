package net.sphen.magicmodbuns.spells.logic.recipes;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.sphen.magicmodbuns.MagicMod;
import net.sphen.magicmodbuns.item.ModItems;
import net.sphen.magicmodbuns.item.custom.LocateCompassItem;
import net.sphen.magicmodbuns.spells.logic.SpellLogicLocate;
import net.sphen.magicmodbuns.util.ModTags;

import java.util.List;
import java.util.Optional;


public class LocateMineralRecipe implements ILocateRecipes{

    @Override
    public boolean matches(List<ItemEntity> items, Level level, BlockPos pos) {
        if (items.size() != 2) return false;

        boolean hasCompass = items.stream().anyMatch(e -> e.getItem().is(Items.COMPASS));
        boolean hasMineral = items.stream().anyMatch(e -> e.getItem().is(ModTags.Items.LOCATE_MINERALS));

        return hasMineral && hasCompass;
    }

    @Override
    public void performCraft(List<ItemEntity> items, Level level, BlockPos pos) {

        ItemStack compassStack = null;
        ItemStack mineralStack = null;

        for (ItemEntity entity : items) {
            ItemStack currentStack = entity.getItem();
            if (currentStack.is(Items.COMPASS)) {
                compassStack = currentStack;
            } else if (currentStack.is(ModTags.Items.LOCATE_MINERALS)) {
                mineralStack = currentStack;
            }
        }

        System.out.println("compass stack: " + compassStack);
        System.out.println("mineral stack: " + mineralStack);

        Block mineralBlock;
        if (mineralStack.getItem() instanceof BlockItem) {
            mineralBlock = ((BlockItem) mineralStack.getItem()).getBlock();
            System.out.println("instance of blockitem");
        } else {
            MagicMod.LOGGER.error("[LocateMineralRecipe] Item used is not an instance of a BlockItem.");
            ServerLevel serverLevel = (ServerLevel) level;
            SpellLogicLocate.handleFailureGeneral(serverLevel, pos);
            return;
        }

        Optional<BlockPos> nearestMineralPos = BlockPos.findClosestMatch(
                pos,
                200,
                64,
                blockPos -> level.getBlockState(blockPos).is(mineralBlock)
        );

        if (nearestMineralPos.isPresent()) {
            BlockPos targetPos = nearestMineralPos.get();

            ResourceKey<Level> dimensionKey = level.dimension();
            String dimensionId = dimensionKey.location().toString();

            ItemStack resultCompass = new ItemStack(ModItems.LOCATE_COMPASS.get());
            CompoundTag nbt = resultCompass.getOrCreateTag();
            nbt.putInt(LocateCompassItem.tagTargetX, targetPos.getX());
            nbt.putInt(LocateCompassItem.tagTargetY, targetPos.getY());
            nbt.putInt(LocateCompassItem.tagTargetZ, targetPos.getZ());
            nbt.putString(LocateCompassItem.tagTargetDimension, dimensionId);

            long expirationTime = level.getGameTime() + LocateCompassItem.maxExpirationTicks;
            nbt.putLong(LocateCompassItem.tagExpiration, expirationTime);

            items.forEach(ItemEntity::discard);

            level.playSound(null, pos, SoundEvents.ZOMBIE_VILLAGER_CURE, SoundSource.BLOCKS, 1.0f, 0.8f);

            ItemEntity resultEntity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, resultCompass);
            level.addFreshEntity(resultEntity);
        }
    }
}
