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
import net.sphen.magicmodbuns.MagicMod;
import net.sphen.magicmodbuns.screen.elements.PatternObject;
import org.jetbrains.annotations.Nullable;

public class ChalkPatternBlockEntity extends BlockEntity {
    private PatternObject pattern;
    private ResourceLocation texturePath;

    public ChalkPatternBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.CHALK_PATTERN.get(), pPos, pBlockState);
        this.pattern = new PatternObject();
        this.texturePath = null;
        setChanged();
        System.out.println("ChalkPatternBlockEntity CREATED at " + pPos);
    }

    public void setPattern(PatternObject pattern) {
        this.pattern = pattern;
        setChanged();
        if (level != null && !level.isClientSide()) {
            syncWithClient(); // Ensures the update is synced with the client
        }
    }

    public void setTexturePath(String texturePath) {
        this.texturePath = new ResourceLocation(MagicMod.MODID, texturePath);
        setChanged();

        if (level != null && !level.isClientSide()) {
            syncWithClient();
        }
    }

    public ResourceLocation getTexturePath() {
        return texturePath != null ? texturePath : new ResourceLocation(MagicMod.MODID, "textures/block/chalk_pattern_base.png");
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        System.out.println("getUpdateTag called for ChalkPatternBlockEntity at " + worldPosition);
        tag.putString("patternData", pattern.storeData());
        if (texturePath != null) {
            tag.putString("texturePath", texturePath.toString());
        }
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        System.out.println("Loading data for ChalkPatternBlockEntity at " + worldPosition);
        load(tag);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        System.out.println("getUpdatePacket called for ChalkPatternBlockEntity at " + worldPosition);
        return ClientboundBlockEntityDataPacket.create(this);
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

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        System.out.println("Saving ChalkPatternBlockEntity at " + worldPosition);

        // Save pattern data
        if (pattern != null) {
            String patternData = pattern.storeData();
            if (!patternData.isEmpty()) {
                pTag.putString("patternData", patternData);
                System.out.println("Pattern Data Saved: " + patternData);
            } else {
                System.out.println("⚠ Warning: Pattern Data is EMPTY when saving!");
            }
        } else {
            System.out.println("Pattern is NULL when saving!");
        }

        // Save texture path
        if (texturePath != null) {
            pTag.putString("texturePath", texturePath.toString());
        } else {
            System.out.println("⚠ Warning: Texture Path is NULL when saving!");
        }
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        System.out.println("Loading ChalkPatternBlockEntity at " + worldPosition);

        if (pTag.contains("patternData")) {
            String data = pTag.getString("patternData");
            if (!data.isEmpty()) {
                System.out.println("Loaded pattern data: " + data);
                pattern = PatternObject.loadData(data);
            } else {
                System.out.println("⚠ Warning: Loaded pattern data was EMPTY!");
            }
        } else {
            System.out.println("No 'patternData' found in CompoundTag.");
        }

        if (pTag.contains("texturePath")) {
            texturePath = new ResourceLocation(pTag.getString("texturePath"));
            System.out.println("Loaded texture path: " + texturePath);
        } else {
            System.out.println("⚠ Warning: No 'texturePath' found in CompoundTag.");
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level != null && !level.isClientSide) {
            syncWithClient();
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
        invalidateCaps();
    }
}
