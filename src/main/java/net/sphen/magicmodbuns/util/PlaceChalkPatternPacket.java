package net.sphen.magicmodbuns.util;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.sphen.magicmodbuns.screen.elements.PatternObject;

import java.util.function.Supplier;

public class PlaceChalkPatternPacket {
    private final BlockPos pos;
    private final String patternData;
    private final String texturePath;

    public PlaceChalkPatternPacket(BlockPos pos, String patternData, String texturePath) {
        this.pos = pos;
        this.patternData = patternData;
        this.texturePath = texturePath;
    }

    public static void handle(PlaceChalkPatternPacket message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                PatternObject pattern = new PatternObject();
                BlockPlacementHelper.placeChalkPatternBlock(player.level(), message.pos, pattern.loadData(message.patternData), message.texturePath);
            }
        });
        ctx.get().setPacketHandled(true);
    }

    // Encoder (serialization)
    public static void encode(PlaceChalkPatternPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.pos);  // Serialize BlockPos
        buffer.writeUtf(packet.patternData);  // Serialize pattern data
        buffer.writeUtf(packet.texturePath);  // Serialize texture path
    }

    // Decoder (deserialization)
    public static PlaceChalkPatternPacket decode(FriendlyByteBuf buffer) {
        BlockPos pos = buffer.readBlockPos();  // Deserialize BlockPos
        String patternData = buffer.readUtf();  // Deserialize pattern data
        String texturePath = buffer.readUtf();  // Deserialize texture path
        return new PlaceChalkPatternPacket(pos, patternData, texturePath);
    }
}
