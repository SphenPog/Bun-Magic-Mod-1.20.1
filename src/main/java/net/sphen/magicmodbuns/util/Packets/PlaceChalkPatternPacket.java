package net.sphen.magicmodbuns.util.Packets;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import net.sphen.magicmodbuns.MagicMod;
import net.sphen.magicmodbuns.block.ChalkType;
import net.sphen.magicmodbuns.screen.elements.PatternObject;
import net.sphen.magicmodbuns.util.BlockPlacementHelper;

import java.util.function.Supplier;

public class PlaceChalkPatternPacket {
    private final BlockPos pos;
    private final String patternData;
    private final String texturePath;
    private ChalkType chalkType;

    public PlaceChalkPatternPacket(BlockPos pos, String patternData, String texturePath, ChalkType chalkType) {
        this.pos = pos;
        this.patternData = patternData;
        this.texturePath = texturePath;
        this.chalkType = chalkType;
    }

    public static void handle(PlaceChalkPatternPacket message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            ServerLevel level = (ServerLevel) player.level();
            BlockPos pos = message.getPos(); // Accessing pos through getter

            // Ensure the chunk is loaded on the server side
            if (!level.hasChunkAt(pos)) {
                System.out.println("⚠ Chunk at " + pos + " is not loaded, sending packet anyway.");
            }

            // Proceed with placing the block and sending sync packet
            PatternObject pattern = new PatternObject();
            BlockPlacementHelper.placeChalkPatternBlock(level, pos, pattern.loadData(message.getPatternData()), message.getTexturePath(), message.chalkType);

            // Send the Sync packet to the client, making sure the chunk is tracked
            SyncChalkPatternPacket syncPacket = new SyncChalkPatternPacket(
                    pos,
                    message.getPatternData(),
                    message.getTexturePath(),
                    message.getChalkType());
            MagicMod.NETWORK.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(pos)), syncPacket);

            System.out.println("✅ Sent SyncChalkPatternPacket to clients at chunk: " + pos);
        });
        ctx.get().setPacketHandled(true);
    }

    // Encoder (serialization)
    public static void encode(PlaceChalkPatternPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.pos);  // Serialize BlockPos
        buffer.writeUtf(packet.patternData);  // Serialize pattern data
        buffer.writeUtf(packet.texturePath);  // Serialize texture path
        buffer.writeUtf(String.valueOf(packet.chalkType.getId()));  // Serialize chalk type
    }

    // Decoder (deserialization)
    public static PlaceChalkPatternPacket decode(FriendlyByteBuf buffer) {
        BlockPos pos = buffer.readBlockPos();  // Deserialize BlockPos
        String patternData = buffer.readUtf();  // Deserialize pattern data
        String texturePath = buffer.readUtf();  // Deserialize texture path
        String chalkColor = buffer.readUtf(); // Deserialize chalk type
        ChalkType chalkType = ChalkType.getById(Integer.valueOf(chalkColor));
        return new PlaceChalkPatternPacket(pos, patternData, texturePath, chalkType);
    }

    public String getPatternData() {
        return patternData;
    }

    public String getTexturePath() {
        return texturePath;
    }

    public BlockPos getPos() {
        return pos;
    }

    public ChalkType getChalkType(){
        return chalkType;
    }
}
