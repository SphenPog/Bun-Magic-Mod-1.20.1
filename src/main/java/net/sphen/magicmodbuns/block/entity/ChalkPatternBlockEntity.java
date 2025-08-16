package net.sphen.magicmodbuns.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.loading.FMLPaths;
import net.sphen.magicmodbuns.MagicMod;
import net.sphen.magicmodbuns.screen.elements.PatternObject;
import net.sphen.magicmodbuns.spells.runes.RuneType;
import net.sphen.magicmodbuns.util.Packets.RemoveChalkTexturePacket;
import net.sphen.magicmodbuns.util.PatternTextureGenerator;
import net.sphen.magicmodbuns.util.PatternTextureLoader;
import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.annotation.ElementType;

public class ChalkPatternBlockEntity extends BlockEntity {
    private PatternObject pattern;
    private ResourceLocation texturePath;
    private BlockPos pos;
    private ElementType element;
    private RuneType runeType;

    //constructor
    public ChalkPatternBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.CHALK_PATTERN.get(), pPos, pBlockState);
        this.pattern = new PatternObject();
        this.pos = pPos;
        this.texturePath = null;
        getTexturePath();
        System.out.println("ChalkPatternBlockEntity CREATED at " + pPos);
    }

    //takes pattern object and sets it if NOT null, otherwise creates new PatternObject for assignment.
    public void setPattern(PatternObject pattern) {
        if (pattern == null) {
            this.pattern = new PatternObject();  // Make sure pattern is always initialized
            System.out.println("pattern is null when setting");
        } else {
            this.pattern = pattern;
        }
    }

    public void setTexturePath(String texturePath) {
        this.texturePath = new ResourceLocation(MagicMod.MODID, texturePath);
    }

    public ResourceLocation getTexturePath() {
        return texturePath != null ? texturePath : new ResourceLocation(MagicMod.MODID, "textures/block/chalk_pattern_base.png");
    }

    //overrides getUpdateTag to saveAdditional.
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        System.out.println("getUpdateTag called for ChalkPatternBlockEntity at " + worldPosition);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        System.out.println("Loading data for ChalkPatternBlockEntity at " + worldPosition);
        load(tag);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return ClientboundBlockEntityDataPacket.create(this, be -> tag);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        load(pkt.getTag()); // Load the updated data from the server
    }

    public void syncWithClient() {
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    //overrides saveAdditonal to store pattern data and texture path data in block.
    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        System.out.println("Saving ChalkPatternBlockEntity at " + worldPosition);

        // Save pattern data
        if (pattern != null) {
            String patternData = pattern.storeData();
            System.out.println(pattern.getLines() + " pattern lines");
            System.out.println(patternData.toString() + " pattern data");
            if (!patternData.isEmpty()) {
                pTag.putString("patternData", patternData);
                System.out.println("Pattern Data Saved: " + patternData);
            } else {
                System.out.println("⚠ Warning: Pattern Data is EMPTY when saving!");
            }
        } else {
            System.out.println("pattern = null");
        }

        // Save texture path
        if (texturePath != null) {
            pTag.putString("texturePath", texturePath.toString());
        } else {
            System.out.println("texture = null");
        }
    }

    //overrides load function to load pattern data and dynamically reload the generated pattern textures.
    @Override
    public void load(CompoundTag pTag) {
        if (pTag == null) {
            System.out.println("tag is null upon load!!");
            return;
        }

        super.load(pTag);
        System.out.println("Loading ChalkPatternBlockEntity at " + worldPosition);

        // Load pattern data
        if (pTag.contains("patternData")) {
            String data = pTag.getString("patternData");
            if (!data.isEmpty()) {
                pattern = PatternObject.loadData(data);
                System.out.println("Loaded pattern data: " + data);
            } else {
                System.out.println("⚠ Warning: Pattern data is empty when loading!");
            }
        }

        // Load texture path
        if (pTag.contains("texturePath")) {
            texturePath = new ResourceLocation(pTag.getString("texturePath"));
            System.out.println("Loaded texture path: " + texturePath);

            // Attempt to reload the texture here
            String filename = texturePath.getPath().replace("generated_textures/", ""); // Remove prefix to get filename
            ResourceLocation result = PatternTextureLoader.loadGeneratedTexture(filename.replace(".png", ""));
            if (result != null) {
                texturePath = result; // Update to use registered texture location
                System.out.println("✅ Successfully reloaded texture: " + texturePath);
            } else {
                System.err.println("❌ Failed to reload texture for: " + filename);
                System.err.println("Trying to recreate texture now!_________");

                BufferedImage generatedImage = PatternTextureGenerator.generateBufferedImage(pattern);
                String textureFileName = "pattern_" + pos.getX() + "_" + pos.getY() + "_" + pos.getZ();

                PatternTextureGenerator.saveTextureToFile(generatedImage, textureFileName);
                result = PatternTextureLoader.loadGeneratedTexture(textureFileName);
                texturePath = result;
            }
        }

        syncWithClient();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
        invalidateCaps();
    }

    //gets filename of texture associated with block entity, deletes it, and sends delete packet to the server.
    public void deleteAssociatedTexture() {

        if (texturePath == null) {
            System.out.println("⚠ No texture path to delete.");
            return;
            }

        String path = texturePath.getPath();

        if (!path.startsWith("generated_textures/")) {
            System.out.println("⚠ Texture path is not from generated folder: " + path);
            return;
        }

        // Extract filename and delete the corresponding file
        String filename = path.substring("generated_textures/".length()); // e.g., "pattern_abc123"

        File file = new File(FMLPaths.GAMEDIR.get().resolve("generated_textures").toFile(), filename + ".png");

        if (file.exists()) {
            if (file.delete()) {
                System.out.println("🗑 Deleted texture file: " + file.getAbsolutePath());
            } else {
                System.err.println("❌ Failed to delete texture file: " + file.getAbsolutePath());
            }
        } else {
            System.out.println("⚠ Texture file not found during deletion: " + file.getAbsolutePath());
        }

        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            RemoveChalkTexturePacket packet = new RemoveChalkTexturePacket(texturePath.toString());

            serverLevel.getServer().getPlayerList().getPlayers().forEach(player ->
                    MagicMod.NETWORK.sendToServer(packet)
            );

            System.out.println("Sent texture unload packet to all players: " + texturePath);
        }
    }

    public RuneType getRuneType() {
        return runeType;
    }

    public ElementType getElementType() {
        return element;
    }
}
