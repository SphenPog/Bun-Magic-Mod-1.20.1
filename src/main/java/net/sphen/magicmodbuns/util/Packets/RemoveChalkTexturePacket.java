package net.sphen.magicmodbuns.util.Packets;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class RemoveChalkTexturePacket {
    private final String texturePath;

    public RemoveChalkTexturePacket(String texturePath) {
        this.texturePath = texturePath;
    }

    public static void encode(RemoveChalkTexturePacket packet, FriendlyByteBuf buf) {
        buf.writeUtf(packet.texturePath);
    }

    public static RemoveChalkTexturePacket decode(FriendlyByteBuf buf) {
        return new RemoveChalkTexturePacket(buf.readUtf());
    }

    //Fix this crap
    public static void handle(RemoveChalkTexturePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ResourceLocation texture = new ResourceLocation("magicmodbuns", packet.texturePath);
            Minecraft.getInstance().getTextureManager().release(texture);
            System.out.println("✅ Released texture: " + texture);
        });
        ctx.get().setPacketHandled(true);
    }
}
