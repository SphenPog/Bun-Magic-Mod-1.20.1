package net.sphen.magicmodbuns.block.entity;


import io.netty.util.Attribute;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sphen.magicmodbuns.MagicMod;
import net.sphen.magicmodbuns.block.ModBlocks;
import net.sphen.magicmodbuns.block.custom.AltarBlock;


public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MagicMod.MODID);

    public static final RegistryObject<BlockEntityType<MortarAndPestleBlockEntity>> MORTAR_PESTLE =
            BLOCK_ENTITIES.register("mortar_pestle", () ->
                    BlockEntityType.Builder.of(MortarAndPestleBlockEntity :: new,
                            ModBlocks.MORTAR_AND_PESTLE.get()).build(null));

    public static final RegistryObject<BlockEntityType<ChalkPatternBlockEntity>> CHALK_PATTERN =
            BLOCK_ENTITIES.register("chalk_pattern", () ->
                    BlockEntityType.Builder.of(ChalkPatternBlockEntity :: new,
                            ModBlocks.CHALK_PATTERN.get()).build(null));

    public static final RegistryObject<BlockEntityType<AltarBlockEntity>> ALTAR =
            BLOCK_ENTITIES.register("altar", () ->
                    BlockEntityType.Builder.of(AltarBlockEntity :: new,
                            ModBlocks.ALTAR_BLOCK.get()).build(null));

    public static void register(IEventBus eventBus){
        BLOCK_ENTITIES.register(eventBus);
    }
}
