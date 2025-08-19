package net.sphen.magicmodbuns.animations.block.altar_block;

import net.minecraft.resources.ResourceLocation;
import net.sphen.magicmodbuns.MagicMod;
import net.sphen.magicmodbuns.block.entity.AltarBlockEntity;
import software.bernie.geckolib.model.GeoModel;

public class AltarBlockModel extends GeoModel<AltarBlockEntity> {
    @Override
    public ResourceLocation getModelResource(AltarBlockEntity animatable) {
        return new ResourceLocation(MagicMod.MODID,"geo/altar_block.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(AltarBlockEntity animatable) {
        return new ResourceLocation(MagicMod.MODID,"textures/block/altar_block_texture.png");
    }

    @Override
    public ResourceLocation getAnimationResource(AltarBlockEntity animatable) {
        return new ResourceLocation(MagicMod.MODID, "animations/altar_block.animation.json");
    }
}
