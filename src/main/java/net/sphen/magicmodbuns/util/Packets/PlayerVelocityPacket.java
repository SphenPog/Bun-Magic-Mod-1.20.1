package net.sphen.magicmodbuns.util.Packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PlayerVelocityPacket {
    private final Vec3 velocity;

    public PlayerVelocityPacket(Vec3 velocity) {
        this.velocity = velocity;
    }

    public static void encode(PlayerVelocityPacket pkt, FriendlyByteBuf buf) {
        buf.writeDouble(pkt.velocity.x);
        buf.writeDouble(pkt.velocity.y);
        buf.writeDouble(pkt.velocity.z);
    }

    public static PlayerVelocityPacket decode(FriendlyByteBuf buf) {
        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();
        return new PlayerVelocityPacket(new Vec3(x, y, z));
    }

    public static void handle(PlayerVelocityPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientPacketHandler.handlePlayerVelocity(packet.velocity);
        });
        ctx.get().setPacketHandled(true);
    }
}
