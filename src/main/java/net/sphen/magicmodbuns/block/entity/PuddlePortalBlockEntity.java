package net.sphen.magicmodbuns.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.sphen.magicmodbuns.spells.actions.TeleportEntityAction;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PuddlePortalBlockEntity extends BlockEntity {

    private UUID portalId = UUID.randomUUID();
    private UUID casterId;
    private Optional<UUID> linkedPortalId = Optional.empty();
    private Optional<BlockPos> linkedPortalPos = Optional.empty();
    private Optional<ResourceKey<Level>> linkedPortalDimension = Optional.empty();
    private long expirationTick;

    private final ConcurrentHashMap<UUID, Long> entitiesInside = new ConcurrentHashMap<>();
    private static final int TELEPORT_DELAY_TICKS = 20; // 1 second

    public PuddlePortalBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.PUDDLE.get(), pPos, pBlockState);
    }

    public static void serverTick(Level pLevel, BlockPos pPos, BlockState pState, PuddlePortalBlockEntity pBlockEntity) {
        if (!pBlockEntity.isLinked()) {
            pBlockEntity.entitiesInside.clear();
            return;
        }

        long currentTime = pLevel.getGameTime();
        AABB portalArea = new AABB(pPos);
        List<LivingEntity> entitiesInZone = pLevel.getEntitiesOfClass(LivingEntity.class, portalArea);

        for (LivingEntity entity : entitiesInZone) {
            long entryTime = pBlockEntity.entitiesInside.computeIfAbsent(entity.getUUID(), k -> {
                return currentTime;
            });

            if (currentTime - entryTime > TELEPORT_DELAY_TICKS) {
                pBlockEntity.getLinkedPortalInfo().ifPresent(info -> {
                    ServerLevel targetLevel = pLevel.getServer().getLevel(info.dimension());
                    if (targetLevel != null) {

                        new TeleportEntityAction().execute(entity, targetLevel, info.pos());
                        pBlockEntity.entitiesInside.remove(entity.getUUID());
                    }
                });
            }
        }

        // Clean up entities that have left the portal
        pBlockEntity.entitiesInside.keySet().removeIf(entityId -> {
            Entity entity = ((ServerLevel) pLevel).getEntity(entityId);
            if (entity == null || !portalArea.intersects(entity.getBoundingBox())) {
                return true;
            }
            return false;
        });
    }


    // Data Access
    public void setData(UUID casterId, long expirationTick) {
        this.casterId = casterId;
        this.expirationTick = expirationTick;
        setChanged();
    }

    public UUID getCasterId() {
        return this.casterId;
    }

    public UUID getPortalId() {
        return this.portalId;
    }


    public boolean isLinked() {
        return linkedPortalId.isPresent();
    }

    public record LinkedPortalInfo(BlockPos pos, ResourceKey<Level> dimension) {}

    public Optional<LinkedPortalInfo> getLinkedPortalInfo() {
        if (linkedPortalPos.isPresent() && linkedPortalDimension.isPresent()) {
            return Optional.of(new LinkedPortalInfo(linkedPortalPos.get(), linkedPortalDimension.get()));
        }
        return Optional.empty();
    }

    public void linkTo(PuddlePortalBlockEntity other) {
        this.linkedPortalId = Optional.of(other.getPortalId());
        this.linkedPortalPos = Optional.of(other.getBlockPos());
        this.linkedPortalDimension = Optional.of(other.getLevel().dimension());

        other.linkedPortalId = Optional.of(this.getPortalId());
        other.linkedPortalPos = Optional.of(this.getBlockPos());
        other.linkedPortalDimension = Optional.of(this.getLevel().dimension());

        setChanged();
        other.setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.putUUID("portalId", portalId);
        if (casterId != null) pTag.putUUID("casterId", casterId);
        pTag.putLong("expirationTick", expirationTick);

        linkedPortalId.ifPresent(uuid -> pTag.putUUID("linkedPortalId", uuid));
        linkedPortalPos.ifPresent(pos -> pTag.put("linkedPortalPos", NbtUtils.writeBlockPos(pos)));
        linkedPortalDimension.ifPresent(key -> pTag.putString("linkedPortalDimension", key.location().toString()));
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        if (pTag.hasUUID("portalId")) {
            this.portalId = pTag.getUUID("portalId");
        } else {
            this.portalId = UUID.randomUUID();
        }

        if (pTag.hasUUID("casterId")) this.casterId = pTag.getUUID("casterId");
        this.expirationTick = pTag.getLong("expirationTick");

        if (pTag.hasUUID("linkedPortalId")) this.linkedPortalId = Optional.of(pTag.getUUID("linkedPortalId"));
        if (pTag.contains("linkedPortalPos")) this.linkedPortalPos = Optional.of(NbtUtils.readBlockPos(pTag.getCompound("linkedPortalPos")));
        if (pTag.contains("linkedPortalDimension")) {
            this.linkedPortalDimension = Optional.of(ResourceKey.create(net.minecraft.core.registries.Registries.DIMENSION, new ResourceLocation(pTag.getString("linkedPortalDimension"))));
        }
    }
}