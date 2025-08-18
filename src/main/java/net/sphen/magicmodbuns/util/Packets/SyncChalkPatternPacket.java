package net.sphen.magicmodbuns.util.Packets;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import net.sphen.magicmodbuns.block.custom.ChalkPatternBlock;
import net.sphen.magicmodbuns.block.entity.ChalkPatternBlockEntity;
import net.sphen.magicmodbuns.screen.elements.PatternObject;
import net.sphen.magicmodbuns.util.PatternTextureGenerator;
import net.sphen.magicmodbuns.util.PatternTextureLoader;

import java.awt.image.BufferedImage;
import java.util.function.Supplier;

public class SyncChalkPatternPacket {
    private final BlockPos pos;
    private final String patternData;
    private final String texturePath;

    public SyncChalkPatternPacket(BlockPos pos, String patternData, String texturePath) {
        this.pos = pos;
        this.patternData = patternData;
        System.out.println(patternData + " - pattern Data string");
        this.texturePath = texturePath;
    }

    public static void encode(SyncChalkPatternPacket packet, FriendlyByteBuf buf) {
        buf.writeBlockPos(packet.getPos());  // Serialize BlockPos
        buf.writeUtf(packet.getPattern().storeData());  // Serialize pattern data
        buf.writeUtf(packet.getTexturePath());  // Serialize texture path
    }

    public static SyncChalkPatternPacket decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();  // Deserialize BlockPos
        String patternData = buf.readUtf();  // Deserialize pattern data
        String texturePath = buf.readUtf();  // Deserialize texture path
        return new SyncChalkPatternPacket(pos, patternData, texturePath);
    }

    public static void handle(SyncChalkPatternPacket message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Minecraft client = Minecraft.getInstance();

            // Schedule a slight delay so the block has time to appear on the client
            client.execute(() -> tryApplyPatternWithDelay(message, 0));
        });

        ctx.get().setPacketHandled(true);
    }

    private static void tryApplyPatternWithDelay(SyncChalkPatternPacket message, int retryCount) {
        Minecraft client = Minecraft.getInstance();
        Level level = client.level;

        if (level == null || retryCount > 5) {
            System.out.println("❌ Level is null or too many retries");
            return;
        }

        BlockPos pos = message.getPos();
        Block block = level.getBlockState(pos).getBlock();

        // Make new pattern object to get pattern data
        PatternObject pattern = new PatternObject();
        pattern = pattern.loadData(message.patternData);


        if (block instanceof ChalkPatternBlock) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ChalkPatternBlockEntity chalkEntity) {
                System.out.println("✅ Found block + entity! Applying pattern and texture");

                // Generate texture from pattern
                System.out.println(pattern.getLines().size() + " - amount of lines. line data: " + pattern.getLines());
                BufferedImage generatedImage = PatternTextureGenerator.generateBufferedImage(pattern);
                String textureFileName = "pattern_" + pos.getX() + "_" + pos.getY() + "_" + pos.getZ();

                PatternTextureGenerator.saveTextureToFile(generatedImage, textureFileName);
                PatternTextureLoader.loadGeneratedTexture(textureFileName);

                chalkEntity.load(chalkEntity.getUpdateTag());
                chalkEntity.syncWithClient();
            } else {
                System.out.println("⚠ Block entity still not loaded, retrying...");
                retrySyncLater(message, retryCount + 1);
            }
        } else {
            System.out.println("⏳ Block is still " + block + ", retrying...");
            retrySyncLater(message, retryCount + 1);
        }
    }

    private static void retrySyncLater(SyncChalkPatternPacket message, int retryCount) {
        Minecraft client = Minecraft.getInstance();
        client.execute(() -> {
            try {
                Thread.sleep(100); // wait ~100ms
            } catch (InterruptedException ignored) {}
            tryApplyPatternWithDelay(message, retryCount);
        });
    }

    private String getTexturePath() {
        return texturePath;
    }

    public PatternObject getPattern() {
        return PatternObject.loadData(patternData);
    }

    private BlockPos getPos() {
        return pos;
    }
}