package net.sphen.magicmodbuns.util.Packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.sphen.magicmodbuns.item.custom.SpellBookItem;

import java.util.function.Supplier;

public class CycleSpellPacket {
    private final int scrollDirection;

    public CycleSpellPacket(int scrollDirection) {
        this.scrollDirection = scrollDirection;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(scrollDirection);
    }

    public static void handle(CycleSpellPacket packet, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();

        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                ItemStack stack = player.getMainHandItem();
                if (stack.getItem() instanceof SpellBookItem) {
                    SpellBookItem.adjustPageIndex(stack, packet.scrollDirection);


                }
            }
        });
        context.setPacketHandled(true);
    }

    public static CycleSpellPacket decode(FriendlyByteBuf buf) {
        return new CycleSpellPacket(buf.readInt());
    }
}
