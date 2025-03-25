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
import net.sphen.magicmodbuns.screen.ChalkScreen;
import net.sphen.magicmodbuns.screen.ModMenuTypes;
import net.sphen.magicmodbuns.screen.ModSceenProvider;


public class ChalkItem extends Item {
    public ChalkItem(Properties pProperties) {
        super(pProperties);
    }
    private static ChalkScreen chalkScreen;

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
            BlockPos positionClicked = pContext.getClickedPos();
            BlockPos positionAbove = pContext.getClickedPos().above(1);
            Player player = pContext.getPlayer();
            Level world = pContext.getLevel();

            //testing
            NetworkHooks.openScreen((ServerPlayer) player, new ModSceenProvider(), buf -> {});

            if(world.getBlockState(positionAbove) == Blocks.AIR.defaultBlockState()){
                world.setBlock(positionAbove, Blocks.WHITE_CARPET.defaultBlockState(), 1);
                pContext.getItemInHand().setCount(pContext.getItemInHand().getCount() - 1);
                //player.sendSystemMessage(Component.literal("can be placed!"));
                //NetworkHooks.openScreen((ServerPlayer) player, new ModSceenProvider(), buf -> {


            } else {
                player.sendSystemMessage(Component.literal("Nope!"));
            }



        }

        return InteractionResult.SUCCESS;
    }

}
