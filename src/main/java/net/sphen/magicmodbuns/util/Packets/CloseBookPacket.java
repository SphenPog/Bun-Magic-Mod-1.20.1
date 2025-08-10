package net.sphen.magicmodbuns.util.Packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.sphen.magicmodbuns.item.custom.SpellBookItem;
import software.bernie.geckolib.animatable.GeoItem;

import java.util.function.Supplier;

public class CloseBookPacket {

    public CloseBookPacket(){

    }

    public static void handle(CloseBookPacket message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();
            if (player != null) {
                ItemStack stack = player.getMainHandItem();
                if (stack.getItem() instanceof SpellBookItem book) {
                    book.isOpen = false;
                    book.triggerAnim(player, GeoItem.getId(stack), "controller", "close");
                }
            }
        });
    }

    public static CloseBookPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new CloseBookPacket();
    }

    public static <MSG> void encode(MSG msg, FriendlyByteBuf friendlyByteBuf) {
    }
}
