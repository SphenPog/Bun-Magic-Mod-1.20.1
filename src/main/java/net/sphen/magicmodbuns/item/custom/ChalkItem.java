package net.sphen.magicmodbuns.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.network.NetworkHooks;
import net.sphen.magicmodbuns.screen.ModSceenProvider;
import net.sphen.magicmodbuns.util.ModTags;


public class ChalkItem extends Item {
    public ChalkItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 64;
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return 0;
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if(!pContext.getLevel().isClientSide()){
            BlockPos positionAbove = pContext.getClickedPos().above(1);
            Player player = pContext.getPlayer();
            Level world = pContext.getLevel();

            if(world.getBlockState(positionAbove) == Blocks.AIR.defaultBlockState()){
                NetworkHooks.openScreen((ServerPlayer) player, new ModSceenProvider(), buf -> {});
                pContext.getItemInHand().setCount(pContext.getItemInHand().getCount() - 1);
            }
        }
        return InteractionResult.SUCCESS;
    }

}
