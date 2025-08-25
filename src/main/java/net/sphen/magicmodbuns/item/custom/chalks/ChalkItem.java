package net.sphen.magicmodbuns.item.custom.chalks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.network.NetworkHooks;
import net.sphen.magicmodbuns.block.ChalkType;
import net.sphen.magicmodbuns.screen.chalk.ChalkScreenProvider;

public class ChalkItem extends Item {

    int maxStackSize = 64;
    int maxDamage = 0;
    public ChalkItem(Properties pProperties) {
        super(pProperties);
    }

    public ChalkType getChalkType(){
        return ChalkType.UNKNOWN;
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return maxStackSize;
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return maxDamage;
    }

    //overrides the useOn function to get clicked location and open the ChalkScreen.
    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if(!pContext.getLevel().isClientSide()){
            BlockPos positionAbove = pContext.getClickedPos().above(1);
            Player player = pContext.getPlayer();
            Level world = pContext.getLevel();

            if(world.getBlockState(positionAbove) == Blocks.AIR.defaultBlockState()){
                NetworkHooks.openScreen((ServerPlayer) player, new ChalkScreenProvider(), buf -> {});
                pContext.getItemInHand().setCount(pContext.getItemInHand().getCount() - 1);
            }
        }
        return InteractionResult.SUCCESS;
    }

}
