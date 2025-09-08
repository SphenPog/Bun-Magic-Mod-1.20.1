package net.sphen.magicmodbuns.util.Packets;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import net.sphen.magicmodbuns.block.ChalkType;
import net.sphen.magicmodbuns.block.entity.ChalkPatternBlockEntity;
import net.sphen.magicmodbuns.screen.elements.PatternObject;

import java.util.function.Supplier;

public class SyncChalkPatternPacket {
    private final BlockPos pos;
    private final String patternData;
    private final ChalkType chalkType;

    public SyncChalkPatternPacket(BlockPos pos, String patternData, ChalkType chalkType) {
        this.pos = pos;
        this.patternData = patternData;
        this.chalkType = chalkType;
    }

    public static void encode(SyncChalkPatternPacket packet, FriendlyByteBuf buf) {
        buf.writeBlockPos(packet.pos);  // Serialize BlockPos
        buf.writeUtf(packet.patternData);  // Serialize pattern data
        buf.writeEnum(packet.chalkType);
    }

    public static SyncChalkPatternPacket decode(FriendlyByteBuf buffer) {
        return new SyncChalkPatternPacket(
                buffer.readBlockPos(),
                buffer.readUtf(),
                buffer.readEnum(ChalkType.class)
        );
    }

    public static void handle(SyncChalkPatternPacket message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientPacketQueue.queuePacket(message);
        });

        ctx.get().setPacketHandled(true);
    }

    public static boolean tryApplyPattern(SyncChalkPatternPacket message) {

        Level level = Minecraft.getInstance().level;

        if (level == null) {
            return false;
        }

        BlockPos pos = message.getPos();

        if (!level.isLoaded(pos)) {
            return false;
        }


        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof ChalkPatternBlockEntity chalkEntity) {
            System.out.println("✅ Found block + entity! Applying pattern and texture");

            PatternObject patternObject = PatternObject.loadData(message.patternData);
            chalkEntity.updatePatternFromPacket(patternObject, message.chalkType);
            return true;
        }
        return false;
    }

    public ChalkType getChalkType(){
        return chalkType;
    }

    public String getPattern() {
        return patternData;
    }

    public BlockPos getPos() {
        return pos;
    }
}